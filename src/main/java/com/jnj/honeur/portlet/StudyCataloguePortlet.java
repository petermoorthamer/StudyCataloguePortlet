package com.jnj.honeur.portlet;

import com.jnj.honeur.constants.StudyCataloguePortletKeys;

import com.jnj.honeur.model.Notebook;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.*;

import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.util.List;

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

	private static final String STUDY_NOTEBOOKS = "studyNotebooks";

	private StudyServiceFacade studyServiceFacade = new StudyServiceFacade();



	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {

		List<Notebook> notebooks = studyServiceFacade.findStudyNotebooks(null);

		renderRequest.setAttribute(STUDY_NOTEBOOKS, notebooks);

		super.render(renderRequest, renderResponse);
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