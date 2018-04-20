package com.jnj.honeur.portlet;

import com.jnj.honeur.catalogue.comparator.UserComparator;
import com.jnj.honeur.catalogue.model.Notebook;
import com.jnj.honeur.catalogue.model.SharedNotebook;
import com.jnj.honeur.catalogue.model.SharedNotebookResult;
import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.service.StorageServiceFacade;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public static final String STUDIES = "studies";
    public static final String STUDY = "study";
    public static final String NOTEBOOK = "notebook";
    public static final String SHARED_NOTEBOOK = "sharedNotebook";
    public static final String SHARED_NOTEBOOK_RESULT = "sharedNotebookResult";
    public static final String ORGANIZATIONS = "organizations";
    public static final String USERS = "users";

    @Reference
    private OrganizationLocalService organizationService;
    @Reference
    private UserLocalService userService;

	//@Reference
    private StudyServiceFacade studyServiceFacade = StudyServiceFacade.getInstance();
    private StorageServiceFacade storageServiceFacade = StorageServiceFacade.getInstance();

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {
        System.out.println("StudyCataloguePortlet.render");

        // studyServiceFacade.setCompanyId(PortalUtil.getCompanyId(renderRequest));
        final User loggedOnUser = getLoggedOnUser(renderRequest);

        Long studyId = ParamUtil.getLong(renderRequest, "studyId");
        if(studyId <= 0) {
            renderRequest.setAttribute(STUDIES, studyServiceFacade.findStudies(loggedOnUser));
        } else {
            final Study study = prepareStudyView(renderRequest);
            if(study != null) {
                final Notebook notebook = prepareNotebookView(study, renderRequest);
                final SharedNotebook sharedNotebook = prepareSharedNotebookView(study, notebook, renderRequest);
                prepareNotebookResultView(sharedNotebook, renderRequest);
            }

        }
        renderRequest.setAttribute(ORGANIZATIONS, findOrganizations());
        renderRequest.setAttribute(USERS, findUsers());

		super.render(renderRequest, renderResponse);
	}

	private User getLoggedOnUser(final PortletRequest request) {
        try {
            User loggedOnUser = PortalUtil.getUser(request);
            LOGGER.info("loggedOnUser: " + loggedOnUser);
            return loggedOnUser;
        } catch (PortalException e) {
            LOGGER.warning(e.getMessage());
        }
        return null;
    }

	private List<Organization> findOrganizations() {
        final List<Organization> organizations = organizationService.getOrganizations(0, 250);
        return organizations.stream().filter(org -> !org.getName().startsWith("Liferay")).collect(Collectors.toList());
    }

    private List<User> findUsers() {
        final List<User> users = userService.getUsers(0, 250);
        return users.stream()
                .filter(usr -> !usr.getFullName().startsWith("Test"))
                .sorted(new UserComparator())
                .collect(Collectors.toList());
    }

	private Study prepareStudyView(final PortletRequest request) {
        Long studyId = ParamUtil.getLong(request, "studyId");
        System.out.println("StudyId: " + studyId);
        if(studyId > 0) {
            final Study study = studyServiceFacade.findStudyById(studyId);
            request.setAttribute(STUDY, study);
            return study;
        }
        return null;
    }

    private Notebook prepareNotebookView(final Study study, final PortletRequest request) {
        Long notebookId = ParamUtil.getLong(request, "notebookId");
        System.out.println("NotebookId: " + notebookId);
        if(notebookId > 0) {
            Optional<Notebook> notebookOptional = study.findNotebook(notebookId);
            if(notebookOptional.isPresent()) {
                final Notebook notebook = notebookOptional.get();
                notebook.setSharedNotebookList(storageServiceFacade.getSharedStudyNotebooks(study, notebook));
                request.setAttribute(NOTEBOOK, notebook);
                return notebook;
            } else {
                LOGGER.warning("No notebook found with ID " + notebookId);
            }
        }
        return null;
    }

    private SharedNotebook prepareSharedNotebookView(final Study study, final Notebook notebook, final PortletRequest request) {
        String sharedNotebookUuid = ParamUtil.getString(request, "sharedNotebookUuid");
        System.out.println("sharedNotebookUuid: " + sharedNotebookUuid);
        if(sharedNotebookUuid.isEmpty()) {
           return null;
        }
        Optional<SharedNotebook> sharedNotebookOptional = notebook.findSharedNotebook(sharedNotebookUuid);
        if(!sharedNotebookOptional.isPresent()) {
            LOGGER.warning("No shared notebook found with UUID " + sharedNotebookUuid);
            return null;
        }
        final SharedNotebook sharedNotebook = sharedNotebookOptional.get();
        sharedNotebook.setNotebookResults(storageServiceFacade.getNotebookResults(study.getId(), sharedNotebookUuid));

        request.setAttribute(SHARED_NOTEBOOK, sharedNotebook);

        return sharedNotebook;
	}

    private SharedNotebookResult prepareNotebookResultView(final SharedNotebook sharedNotebook, final PortletRequest request) {
        String sharedNotebookResultUuid = ParamUtil.getString(request, "sharedNotebookResultUuid");
        System.out.println("sharedNotebookResultUuid: " + sharedNotebookResultUuid);
        if(sharedNotebookResultUuid.isEmpty()) {
            return null;
        }
        SharedNotebookResult notebookResult = storageServiceFacade.getNotebookResult(sharedNotebook, sharedNotebookResultUuid);

        request.setAttribute(SHARED_NOTEBOOK_RESULT, notebookResult);

        return notebookResult;
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
		study.setLeadUserId(ParamUtil.getLong(request, "studyLead"));

		String[] collaboratorIds = ParamUtil.getStringValues(request, "collaborators");
        study.setCollaboratorIds(collaboratorIds);
		studyServiceFacade.createOrSaveStudy(study);

        request.setAttribute(STUDY, study);
        response.setRenderParameter("mvcPath", "/study-details.jsp");
	}

    public void newNotebook(ActionRequest request, ActionResponse response) {
        LOGGER.info("StudyCataloguePortlet.newNotebook");

        Long studyId = ParamUtil.getLong(request, "studyId");
        LOGGER.info("newNotebook: " + studyId);
        final Study study = studyServiceFacade.findStudyById(studyId);
	    final Notebook notebook = new Notebook();
	    notebook.setStudy(study);

        request.setAttribute(STUDY, study);
	    request.setAttribute(NOTEBOOK, notebook);
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
        String notebookUuid = storageServiceFacade.shareStudyNotebook(study.getId(), notebook.get());
        LOGGER.info("Notebook UUID: " + notebookUuid);

        response.setRenderParameter("mvcPath", "/notebook-details.jsp");
	}

    public void deleteSharedNotebook(ActionRequest request, ActionResponse response) {
        String sharedNotebookUuid = ParamUtil.getString(request, "sharedNotebookUuid");
	    System.out.println("deleteSharedNotebook: " + sharedNotebookUuid);
	    storageServiceFacade.deleteFile(sharedNotebookUuid);

        response.setRenderParameter("mvcPath", "/notebook-details.jsp");
    }

    public void uploadNotebookResult(ActionRequest request, ActionResponse response) {
        String sharedNotebookUuid = ParamUtil.getString(request, "sharedNotebookUuid");
        UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
        File uploadedFile = uploadRequest.getFile("file");
        long sizeInBytes = uploadRequest.getSize("file");
        String originalFileName = uploadRequest.getFileName("file");

        Path copied = Paths.get("/Users/peter/Desktop/" + originalFileName);
        try {
            Files.copy(uploadedFile.toPath(), copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        System.out.println("Uploaded file: " + uploadedFile.getAbsolutePath());
        System.out.println("Uploaded file size: " + sizeInBytes);
        System.out.println("Copied file: " + copied);
    }

    public void deleteNotebookResult(ActionRequest request, ActionResponse response) {
        String sharedNotebookResultUuid = ParamUtil.getString(request, "sharedNotebookResultUuid");
        System.out.println("deleteNotebookResult: " + sharedNotebookResultUuid);
        storageServiceFacade.deleteFile(sharedNotebookResultUuid);
    }
}