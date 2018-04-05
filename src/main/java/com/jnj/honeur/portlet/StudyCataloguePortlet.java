package com.jnj.honeur.portlet;

import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.model.Notebook;
import com.jnj.honeur.model.Study;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.*;
import java.io.IOException;

/**
 * @author Peter Moorthamer
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=study-catalogue-portlet Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + StudyCataloguePortletKeys.StudyCatalogue,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class StudyCataloguePortlet extends MVCPortlet {

    private static final String STUDIES = "studies";
    private static final String STUDY_NOTEBOOKS = "studyNotebooks";

	@Reference
    private StudyServiceFacade studyServiceFacade;

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {

        renderRequest.setAttribute(STUDIES, studyServiceFacade.findStudies());
		renderRequest.setAttribute(STUDY_NOTEBOOKS, studyServiceFacade.findStudyNotebooks(null));

		super.render(renderRequest, renderResponse);
	}

    public void addStudy(ActionRequest request, ActionResponse response) {
        PortletPreferences prefs = request.getPreferences();
        String studyName = ParamUtil.getString(request, "studyName");
        Study study = new Study();
        study.setName(studyName);
        studyServiceFacade.createStudy(study);
    }



	public void addNotebook(ActionRequest request, ActionResponse response) {
		PortletPreferences prefs = request.getPreferences();
		String notebookUrl = ParamUtil.getString(request, "notebookUrl");
		Notebook notebook = new Notebook();
		notebook.setUrl(notebookUrl);
		studyServiceFacade.createNotebook(null, notebook);
	}

	public void shareNotebook(ActionRequest request, ActionResponse response) {

	}
}