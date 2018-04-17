package com.jnj.honeur.portlet;

import com.jnj.honeur.catalogue.model.Notebook;
import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.service.StorageServiceFacade;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;

import javax.portlet.*;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(StudyCataloguePortlet.class.getName());

    public static final String STUDY = "study";
    public static final String STUDIES = "studies";
    public static final String STUDY_NOTEBOOK = "studyNotebook";
    public static final String STUDY_NOTEBOOKS = "studyNotebooks";
    public static final String SHARED_STUDY_NOTEBOOKS = "sharedStudyNotebooks";
    public static final String SHARED_STUDY_NOTEBOOK_LOGS = "sharedStudyNotebookLogs";

	//@Reference
    private StudyServiceFacade studyServiceFacade = StudyServiceFacade.getInstance();
    private StorageServiceFacade storageServiceFacade = StorageServiceFacade.getInstance();


	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {
        LOGGER.info("StudyCataloguePortlet.render");

        studyServiceFacade.setCompanyId(PortalUtil.getCompanyId(renderRequest));

        renderRequest.setAttribute(STUDIES, studyServiceFacade.findStudies());

        Long studyId = ParamUtil.getLong(renderRequest, "studyId");

        if(studyId > 0) {
            final Study study = studyServiceFacade.findStudyById(studyId);
            renderRequest.setAttribute(STUDY, study);
            LOGGER.info("StudyId: " + studyId);
            Long notebookId = ParamUtil.getLong(renderRequest, "notebookId");
            LOGGER.info("NotebookId: " + notebookId);
            if(notebookId > 0) {
                Optional<Notebook> notebook = study.findNotebook(notebookId);
                if(notebook.isPresent()) {
                    renderRequest.setAttribute(STUDY_NOTEBOOK, notebook.get());
                    renderRequest.setAttribute(SHARED_STUDY_NOTEBOOKS, storageServiceFacade.getSharedStudyNotebooks(study, notebook.get()));
                } else {
                    LOGGER.info("No notebook found with ID " + notebookId);
                }
            }
            String sharedNotebookUuid = ParamUtil.getString(renderRequest, "sharedNotebookUuid");
            if(!sharedNotebookUuid.isEmpty()) {

            }
        }

		super.render(renderRequest, renderResponse);
	}

    public void newStudy(ActionRequest request, ActionResponse response) {
        LOGGER.info("StudyCataloguePortlet.newStudy");
        request.setAttribute(STUDY, new Study());
        response.setRenderParameter("mvcPath", "/study-details.jsp");
    }

	public void createOrSaveStudy(ActionRequest request, ActionResponse response) {
		final Study study = new Study();
		study.setId(ParamUtil.getLong(request, "studyId"));
		study.setName(ParamUtil.getString(request, "studyName"));
		study.setNumber(ParamUtil.getString(request, "studyNumber"));
		study.setDescription(ParamUtil.getString(request, "studyDescription"));
		study.setAcknowledgments(ParamUtil.getString(request, "studyAcknowledgments"));
		studyServiceFacade.createOrSaveStudy(study);
	}

    public void newNotebook(ActionRequest request, ActionResponse response) {
        LOGGER.info("StudyCataloguePortlet.newNotebook");

        Long studyId = ParamUtil.getLong(request, "studyId");
        LOGGER.info("newNotebook: " + studyId);
        final Study study = studyServiceFacade.findStudyById(studyId);
	    final Notebook notebook = new Notebook();
	    notebook.setStudy(study);

        request.setAttribute(STUDY, study);
	    request.setAttribute(STUDY_NOTEBOOK, notebook);
        response.setRenderParameter("mvcPath", "/notebook-details.jsp");
    }

	public void createOrSaveNotebook(ActionRequest request, ActionResponse response) {
	    Long studyId = ParamUtil.getLong(request, "studyId");
        Study study = studyServiceFacade.findStudyById(studyId);

        Long notebookId = ParamUtil.getLong(request, "notebookId");
        String notebookUrl = ParamUtil.getString(request, "notebookUrl");
		Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setUrl(notebookUrl);

        study.addNotebook(notebook);

        studyServiceFacade.saveStudy(study);

        request.setAttribute(STUDY, study);
        response.setRenderParameter("mvcPath", "/study-details.jsp");
	}

	public void shareNotebook(ActionRequest request, ActionResponse response) {
        Long studyId = ParamUtil.getLong(request, "studyId");
        Study study = studyServiceFacade.findStudyById(studyId);
        Long notebookId = ParamUtil.getLong(request, "notebookId");
        Optional<Notebook> notebook = study.findNotebook(notebookId);
        if(!notebook.isPresent()) {
            LOGGER.severe("No notebook found with ID " + notebookId);
            return;
        }
        String notebookUuid = storageServiceFacade.shareStudyNotebook(study, notebook.get());
        System.out.println("Notebook UUID: " + notebookUuid);

        request.setAttribute(STUDY, study);
        request.setAttribute(STUDY_NOTEBOOK, notebook.get());
        request.setAttribute(SHARED_STUDY_NOTEBOOKS, storageServiceFacade.getSharedStudyNotebooks(study, notebook.get()));
        response.setRenderParameter("mvcPath", "/notebook-details.jsp");
	}
}