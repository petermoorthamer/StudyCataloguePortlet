package com.jnj.honeur.portlet;

import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.model.Notebook;
import com.jnj.honeur.model.Study;
import com.jnj.honeur.model.StudyModel;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
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
    private static final String STUDYMODELS = "studyModels";

    //@Reference
    private StudyServiceFacade studyServiceFacade = StudyServiceFacade.getInstance();

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {

        studyServiceFacade.setCompanyId(PortalUtil.getCompanyId(renderRequest));

        renderRequest.setAttribute(STUDIES, studyServiceFacade.findStudies());
        renderRequest.setAttribute(STUDY_NOTEBOOKS, studyServiceFacade.findStudyNotebooks(null));
        renderRequest.setAttribute(STUDYMODELS, studyServiceFacade.findStudyModels());

        super.render(renderRequest, renderResponse);
    }

    public void addStudy(ActionRequest request, ActionResponse response) {
        PortletPreferences prefs = request.getPreferences();
        String studyName = ParamUtil.getString(request, "studyName");
        Study study = new Study();
        study.setName(studyName);
        studyServiceFacade.createStudy(study);
    }

    public void saveStudyModel(ActionRequest request, ActionResponse response) throws PortletException{
        try {
            Long studyId = ParamUtil.getLong(request, "id");
            String studyName = ParamUtil.getString(request, "name");

            if (studyId == 0) {
                StudyModel studyModel = new StudyModel(new Study());
                studyModel.setName(studyName);
                User user = PortalUtil.getUser(request);
                studyModel.setUserId(user.getUserId());
                studyModel.setUserName(user.getScreenName());
                studyModel.setUuid(PortalUUIDUtil.generate());
                studyServiceFacade.createStudyModel(studyModel);
            }
            else {
                StudyModel studyModel = studyServiceFacade.findStudyModelById(studyId);
                studyModel.setName(studyName);
                studyServiceFacade.saveStudyModel(studyModel);
            }

            // Do a redirect back to StudyModel list.
            response.setRenderParameter("mvcPath", StudyCataloguePortletKeys.StudyModelAssetURLList + ".jsp");
        } catch (PortalException e) {
            throw new PortletException(e);
        }
    }

    public void deleteStudyModel(ActionRequest request, ActionResponse response) {
        long studyId = ParamUtil.getLong(request, "studyId");
        StudyModel studyModel = studyServiceFacade.findStudyModelById(studyId);
        studyServiceFacade.deleteStudyModel(studyModel);
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