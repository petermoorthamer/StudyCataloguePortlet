package com.jnj.honeur.portlet;

import com.jnj.honeur.catalogue.comparator.StudyComparator;
import com.jnj.honeur.catalogue.comparator.UserComparator;
import com.jnj.honeur.catalogue.model.*;
import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.service.StorageServiceFacade;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
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
        "javax.portlet.init-param.add-process-action-success-action=false",
		"javax.portlet.name=" + StudyCataloguePortletKeys.StudyCatalogue,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class StudyCataloguePortlet extends MVCPortlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyCataloguePortlet.class);

    public static final String STUDIES = "studies";
    public static final String STUDY = "study";
    public static final String STUDY_COLLABORATORS = "studyCollaborators";
    public static final String STUDY_COLLABORATORS_AVAILABLE = "availableStudyCollaborators";
    public static final String NOTEBOOK = "notebook";
    public static final String NOTEBOOK_STATISTICS = "notebookStatistics";
    public static final String SHARED_NOTEBOOK = "sharedNotebook";
    public static final String SHARED_NOTEBOOK_RESULT = "sharedNotebookResult";
    public static final String ORGANIZATIONS = "organizations";
    public static final String USERS = "users";

    @Reference
    private OrganizationLocalService organizationService;
    @Reference
    private UserLocalService userService;

    private StudyServiceFacade studyServiceFacade; // = StudyServiceFacade.getInstance();
    private StorageServiceFacade storageServiceFacade; // = StorageServiceFacade.getInstance();

    @Reference(unbind = "-")
    public void setStudyServiceFacade(StudyServiceFacade studyServiceFacade) {
        this.studyServiceFacade = studyServiceFacade;
    }

    @Reference(unbind = "-")
    public void setStorageServiceFacade(StorageServiceFacade storageServiceFacade) {
        this.storageServiceFacade = storageServiceFacade;
    }

    @Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {
        LOGGER.info("StudyCataloguePortlet.render");

        logLiferayIds(renderRequest, renderResponse);

        final List<Organization> organizations = findOrganizations();
        final List<User> users = findUsers();

        Long studyId = ParamUtil.getLong(renderRequest, "studyId");
        /*if(studyId <= 0) {
            studyId = processViewInContextUrl(renderRequest);
        }*/
        LOGGER.info("StudyCataloguePortlet.render - studyId: " + studyId);
        if(studyId <= 0) {
            prepareStudyListView(renderRequest);
        } else {
            final Study study = prepareStudyView(organizations, renderRequest);
            if(study != null) {
                final Notebook notebook = prepareNotebookView(study, renderRequest);
                if(notebook != null) {
                    prepareSharedNotebookStatistics(notebook, organizations, users, renderRequest);
                    final SharedNotebook sharedNotebook = prepareSharedNotebookView(study, notebook, renderRequest);
                    prepareNotebookResultView(sharedNotebook, renderRequest);
                }
            }
        }

        renderRequest.setAttribute(ORGANIZATIONS, organizations);
        renderRequest.setAttribute(USERS, users);

		super.render(renderRequest, renderResponse);
	}

	private void logLiferayIds(RenderRequest renderRequest, RenderResponse renderResponse) {
        try {
            LOGGER.debug("Group ID: " + PortalUtil.getScopeGroupId(renderRequest));
            LOGGER.debug("Company ID: " + PortalUtil.getCompanyId(renderRequest));
        } catch (PortalException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

	/*private Long processViewInContextUrl(final RenderRequest renderRequest) {
        LOGGER.info("processViewInContextUrl");
        HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
        HttpServletRequest originalHttpServletRequest = PortalUtil.getOriginalServletRequest(httpServletRequest);

        String studyId = originalHttpServletRequest.getParameter("_StudyCatalogue_studyId");
        if(!StringUtils.isEmpty(studyId)) {
            LOGGER.info("studyId: " + studyId);
            String mvcPath = originalHttpServletRequest.getParameter("_StudyCatalogue_mvcPath");
            if(!StringUtils.isEmpty(mvcPath)) {
                LOGGER.info("mvcPath: " + mvcPath);
                renderRequest.setAttribute("mvcPath", mvcPath);
            }
            return Long.valueOf(studyId);
        }
        return 0L;
    }*/

	private User getLoggedOnUser(final PortletRequest request) {
        try {
            User loggedOnUser = PortalUtil.getUser(request);
            LOGGER.debug("loggedOnUser: " + loggedOnUser);
            return loggedOnUser;
        } catch (PortalException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

	private List<Organization> findOrganizations() {
        LOGGER.debug("findOrganizations");
        final List<Organization> organizations = organizationService.getOrganizations(0, 250);
        return organizations.stream().filter(org -> !org.getName().startsWith("Liferay")).collect(Collectors.toList());
    }

    private List<User> findUsers() {
        LOGGER.debug("findUsers");
        final List<User> users = userService.getUsers(0, 250);
        return users.stream()
                .filter(usr -> !usr.getFullName().startsWith("Test"))
                .sorted(new UserComparator())
                .collect(Collectors.toList());
    }

    private List<Organization> getCollaborators(final Study study, final List<Organization> organisations) {
        return organisations.stream()
                .filter(org -> study.hasCollaborator(org.getPrimaryKey()))
                .collect(Collectors.toList());
    }

    private List<Organization> getAvailableCollaborators(final Study study, final List<Organization> organisations) {
        return organisations.stream()
                .filter(org -> !study.hasCollaborator(org.getPrimaryKey()))
                .collect(Collectors.toList());
    }

    private List<Study> prepareStudyListView(final PortletRequest request) {
        final User loggedOnUser = getLoggedOnUser(request);
        final String keywords = ParamUtil.getString(request, "keywords");

        List<Study> studies = null;
        if(!StringUtils.isEmpty(keywords)) {
            LOGGER.info("prepareStudyListView with search keywords: " + keywords);
            studies = searchStudies(request, keywords, loggedOnUser);
        } else {
            LOGGER.info("find all studies for the logged in user");
            studies = studyServiceFacade.findStudies(loggedOnUser);
        }
        request.setAttribute(STUDIES, studies);
        request.setAttribute("keywords", keywords);

        return studies;
    }

	private Study prepareStudyView(final List<Organization> organizations, final PortletRequest request) {
        Long studyId = ParamUtil.getLong(request, "studyId");
        LOGGER.debug("StudyId: " + studyId);
        if(studyId > 0) {
            final Study study = studyServiceFacade.findStudyById(studyId);
            request.setAttribute(STUDY, study);
            request.setAttribute(STUDY_COLLABORATORS, getCollaborators(study, organizations));
            request.setAttribute(STUDY_COLLABORATORS_AVAILABLE, getAvailableCollaborators(study, organizations));
            return study;
        }
        return null;
    }

    private Notebook prepareNotebookView(final Study study, final PortletRequest request) {
        Long notebookId = ParamUtil.getLong(request, "notebookId");
        LOGGER.debug("NotebookId: " + notebookId);
        if(notebookId > 0) {
            Optional<Notebook> notebookOptional = study.findNotebook(notebookId);
            if(notebookOptional.isPresent()) {
                try {
                    final Notebook notebook = notebookOptional.get();
                    LOGGER.info("Shared notebooks: " + notebook.getSharedNotebookList());
                    notebook.setSharedNotebooks(retrieveSharedNotebooks(study, notebook));
                    request.setAttribute(NOTEBOOK, notebook);
                    return notebook;
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    SessionErrors.add(request, e.getClass().getName());
                    return null;
                }
            } else {
                LOGGER.warn("No notebook found with ID " + notebookId);
            }
        }
        return null;
    }

    private Set<SharedNotebook> retrieveSharedNotebooks(final Study study, final Notebook notebook) {
        final Set<SharedNotebook> sharedNotebooks = new HashSet<>();
        final List<SharedNotebook> sharedNotebookList = storageServiceFacade.getSharedStudyNotebooks(study, notebook);
        for(SharedNotebook sn:sharedNotebookList) {
            Optional<SharedNotebook> sharedNotebook = notebook.findSharedNotebook(sn.getUuid());
            if(!sharedNotebook.isPresent()) {
                LOGGER.warn("Notebook found on HSS that is not linked to the study notebook!");
                continue;
            }
            sn.setSharedOrganizationIds(sharedNotebook.get().getSharedOrganizationIds());
            sn.setId(sharedNotebook.get().getId());
            sharedNotebooks.add(sn);
        }
        return sharedNotebooks;
    }

    private List<SharedNotebookStatistics> prepareSharedNotebookStatistics(final Notebook notebook, final List<Organization> organizations, final List<User> users, final PortletRequest request) {
	    SharedNotebookStatisticsCalculator calculator = new SharedNotebookStatisticsCalculator(notebook, organizations, users);
        List<SharedNotebookStatistics> statistics = calculator.calculate();
        request.setAttribute(NOTEBOOK_STATISTICS, statistics);
        return statistics;
    }

    private SharedNotebook prepareSharedNotebookView(final Study study, final Notebook notebook, final PortletRequest request) {
        String sharedNotebookUuid = ParamUtil.getString(request, "sharedNotebookUuid");
        LOGGER.debug("sharedNotebookUuid: " + sharedNotebookUuid);
        if(sharedNotebookUuid.isEmpty()) {
           return null;
        }
        Optional<SharedNotebook> sharedNotebookOptional = notebook.findSharedNotebook(sharedNotebookUuid);
        if(!sharedNotebookOptional.isPresent()) {
            LOGGER.warn("No shared notebook found with UUID " + sharedNotebookUuid);
            return null;
        }
        final SharedNotebook sharedNotebook = sharedNotebookOptional.get();
        sharedNotebook.setNotebookResults(storageServiceFacade.getNotebookResults(study.getId(), sharedNotebookUuid));

        request.setAttribute(SHARED_NOTEBOOK, sharedNotebook);

        return sharedNotebook;
	}

    private SharedNotebookResult prepareNotebookResultView(final SharedNotebook sharedNotebook, final PortletRequest request) {
        String sharedNotebookResultUuid = ParamUtil.getString(request, "sharedNotebookResultUuid");
        LOGGER.debug("sharedNotebookResultUuid: " + sharedNotebookResultUuid);
        if(sharedNotebookResultUuid.isEmpty()) {
            return null;
        }
        try {
            final SharedNotebookResult notebookResult = storageServiceFacade.getNotebookResult(sharedNotebook, sharedNotebookResultUuid);
            request.setAttribute(SHARED_NOTEBOOK_RESULT, notebookResult);
            return notebookResult;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
            return null;
        }
    }

    public void newStudy(ActionRequest request, ActionResponse response) {
        LOGGER.info("StudyCataloguePortlet.newStudy");
        final Study study = new Study();
        final User loggedOnUser = getLoggedOnUser(request);
        if(loggedOnUser != null) {
            study.setCreatorUserId(loggedOnUser.getPrimaryKey());
        }
        study.setCreateDate(Calendar.getInstance());
        request.setAttribute(STUDY, study);
        response.setRenderParameter("mvcPath", "/study-details.jsp");
    }

    public void addStudyCollaborator(ActionRequest request, ActionResponse response) {
        try {
            Long studyId = ParamUtil.getLong(request, "studyId");
            LOGGER.debug("StudyId: " + studyId);
            Long organizationId = ParamUtil.getLong(request, "collaboratorId");
            LOGGER.info("CollaboratorId: " + organizationId);
            if(organizationId > 0) {
                LOGGER.info("addStudyCollaborator: " + organizationId);
                final Study study = studyServiceFacade.findStudyById(studyId);
                study.addCollaboratingOrganizationId(organizationId);
                studyServiceFacade.saveStudy(study);

                prepareStudyView(findOrganizations(), request);
                response.setRenderParameter("mvcPath", "/study-details.jsp");

            } else {
                LOGGER.warn("No collaborator selected!");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

    public void removeStudyCollaborator(ActionRequest request, ActionResponse response) {
        try {
            Long studyId = ParamUtil.getLong(request, "studyId");
            LOGGER.debug("StudyId: " + studyId);
            Long organizationId = ParamUtil.getLong(request, "collaboratorId");
            LOGGER.info("CollaboratorId: " + organizationId);
            if(organizationId > 0) {
                LOGGER.info("removeStudyCollaborator: " + organizationId);
                final Study study = studyServiceFacade.findStudyById(studyId);
                study.removeCollaboratingOrganizationId(organizationId);
                studyServiceFacade.saveStudy(study);

                prepareStudyView(findOrganizations(), request);
                response.setRenderParameter("mvcPath", "/study-details.jsp");
            } else {
                LOGGER.warn("No collaborator selected!");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

	public void createOrSaveStudy(ActionRequest request, ActionResponse response) {
        try {
            boolean newStudy = true;
            Study study = new Study();
            Long studyId = ParamUtil.getLong(request, "studyId");
            if(studyId > 0) {
                newStudy = false;
                study = studyServiceFacade.findStudyById(studyId);
            }

            processChangesOnSubmit(study, request);
            studyServiceFacade.createOrSaveStudy(study);

            addOrUpdateStudyAsset(study, newStudy, request);

            reIndexStudyIndex(study.getId());

            request.setAttribute(STUDY, study);
            response.setRenderParameter("mvcPath", "/study-details.jsp");

            SessionMessages.add(request, "studySaved");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

	private void addOrUpdateStudyAsset(Study study, boolean newStudy, PortletRequest request) {
        LOGGER.info("addOrUpdateStudyAsset for study with id: " + study.getId());
        try {
            final ServiceContext serviceContext = ServiceContextFactory.getInstance(Study.class.getName(), request);
            final User loggedOnUser = getLoggedOnUser(request);
            long userId = loggedOnUser != null ? loggedOnUser.getPrimaryKey() : 0L;
            long groupId = PortalUtil.getScopeGroupId(request);
            if(newStudy) {
                studyServiceFacade.addStudyAsset(study, userId, groupId, serviceContext);
            } else {
                studyServiceFacade.updateStudyAsset(study, userId, groupId, serviceContext);
            }
        } catch (PortalException e) {
            LOGGER.error("Study asset could not be added or updated!", e);
        }
    }

	private Study processChangesOnSubmit(Study study, PortletRequest request) {
        study.setId(ParamUtil.getLong(request, "studyId"));
        study.setName(ParamUtil.getString(request, "studyName"));
        study.setNumber(ParamUtil.getString(request, "studyNumber"));
        study.setDescription(ParamUtil.getString(request, "studyDescription"));
        study.setAcknowledgments(ParamUtil.getString(request, "studyAcknowledgments"));
        study.setLeadUserId(ParamUtil.getLong(request, "studyLead"));

        //String[] collaboratorIds = ParamUtil.getStringValues(request, "collaborators");
        //study.setCollaboratorIds(collaboratorIds);

        return study;
    }

	private void reIndexStudyIndex(Long studyId) {
        try {
            LOGGER.info("Re-index study with id " + studyId);
            final Indexer indexer = IndexerRegistryUtil.getIndexer(Study.class);
            indexer.reindex(indexer.getClassName(), studyId);
        } catch (SearchException e) {
            LOGGER.warn("Study re-indexing failed!", e);
        }
    }

	public List<Study> searchStudies(final PortletRequest request, String keywords, User loggedOnUser) {
        LOGGER.info("*** searchStudies START ***");
        LOGGER.info("searchStudies with keywords: " + keywords);

        final List<Study> studies = new ArrayList<>();

        try {
            final Indexer indexer = IndexerRegistryUtil.getIndexer(Study.class);
            LOGGER.debug("Indexer: " + indexer);
            LOGGER.debug("Indexer enabled: " + indexer.isIndexerEnabled());
            LOGGER.debug("Indexer search class names: ");
            for(String className:indexer.getSearchClassNames()) {
                LOGGER.debug("Indexer search class name: " + className);
            }

            final SearchContext searchContext = getSearchContext(request, keywords);
            LOGGER.info("Indexer search count: " + indexer.searchCount(searchContext));

            Hits hits = indexer.search(getSearchContext(request, keywords), Field.ENTRY_CLASS_PK);
            LOGGER.info("# Hits: " + hits.getLength());
            LOGGER.info("Query class: " + hits.getQuery().getClass());


            for (int i = 0; i < hits.getDocs().length; i++) {
                Document doc = hits.doc(i);
                LOGGER.info("Document: " + doc);

                long studyId = GetterUtil.getLong(doc.get(Field.ENTRY_CLASS_PK));
                Study study = studyServiceFacade.findStudyById(studyId);
                if(studyServiceFacade.isStudyAccessibleForUser(study, loggedOnUser)) {
                    studies.add(study);
                }
            }

            studies.sort(new StudyComparator());

            request.setAttribute(STUDIES, studies);

        } catch (SearchException e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }

        LOGGER.info("*** searchStudies END ***");

        return studies;
    }

    private SearchContext getSearchContext(final PortletRequest request, String keywords) {
        final HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
        final SearchContext searchContext = SearchContextFactory.getInstance(httpServletRequest);

        searchContext.setLike(true);
        searchContext.setAndSearch(false);
        searchContext.setKeywords(keywords);
        searchContext.setAttribute("paginationType", "more");
        searchContext.setStart(0);
        searchContext.setEnd(50);

        return searchContext;
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
        try {
            Long studyId = ParamUtil.getLong(request, "studyId");
            Study study = studyServiceFacade.findStudyById(studyId);

            Long notebookId = ParamUtil.getLong(request, "notebookId");
            String notebookName = ParamUtil.getString(request, "notebookName");
            String notebookUrl = ParamUtil.getString(request, "notebookUrl");

            LOGGER.info("createOrSaveNotebook " + notebookName + ": " + notebookUrl);

            final Notebook notebook = new Notebook();
            notebook.setId(notebookId);
            notebook.setName(notebookName);
            notebook.setUrl(notebookUrl);

            boolean added = study.addNotebook(notebook);
            LOGGER.info("Notebook added to study : " + added);

            studyServiceFacade.saveStudy(study);

            request.setAttribute(STUDY, study);
            response.setRenderParameter("mvcPath", "/study-details.jsp");

            SessionMessages.add(request, "notebookSaved");

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

	public void shareNotebook(ActionRequest request, ActionResponse response) {
        try {
            Long studyId = ParamUtil.getLong(request, "studyId");
            Study study = studyServiceFacade.findStudyById(studyId);
            Long notebookId = ParamUtil.getLong(request, "notebookId");
            Optional<Notebook> notebook = study.findNotebook(notebookId);
            if(!notebook.isPresent()) {
                LOGGER.error("No notebook found with ID " + notebookId);
                return;
            }
            String notebookUuid = storageServiceFacade.shareStudyNotebook(study.getId(), notebook.get());
            LOGGER.info("Notebook UUID: " + notebookUuid);

            studyServiceFacade.saveSharedNotebook(createSharedNotebook(study, notebook.get(), notebookUuid));

            //prepareNotebookView(study, request);

            response.setRenderParameter("mvcPath", "/notebook-details.jsp");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

    private SharedNotebook createSharedNotebook(final Study study, final Notebook notebook, String sharedNotebookUuid) {
        final SharedNotebook sharedNotebook = new SharedNotebook();
        if(notebook.getStudy() == null) {
            Study notebookStudy = new Study();
            notebookStudy.setId(study.getId());
            notebook.setStudy(notebookStudy);
        }
        sharedNotebook.setNotebook(notebook);
        sharedNotebook.setUuid(sharedNotebookUuid);

        return sharedNotebook;
    }

    public void deleteSharedNotebook(ActionRequest request, ActionResponse response) {
        try {
            String sharedNotebookUuid = ParamUtil.getString(request, "sharedNotebookUuid");
            LOGGER.debug("deleteSharedNotebook: " + sharedNotebookUuid);
            storageServiceFacade.deleteFile(sharedNotebookUuid);

            response.setRenderParameter("mvcPath", "/notebook-details.jsp");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

    public void uploadNotebookResult(ActionRequest request, ActionResponse response) {
        try {
            String sharedNotebookUuid = ParamUtil.getString(request, "sharedNotebookUuid");
            UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
            File uploadedFile = uploadRequest.getFile("file");
            long sizeInBytes = uploadRequest.getSize("file");
            String originalFileName = uploadRequest.getFileName("file");

            Path copied = Paths.get("/Users/peter/Desktop/" + originalFileName);
            try {
                Files.copy(uploadedFile.toPath(), copied, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            LOGGER.debug("Uploaded file: " + uploadedFile.getAbsolutePath());
            LOGGER.debug("Uploaded file size: " + sizeInBytes);
            LOGGER.debug("Copied file: " + copied);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }

    public void deleteNotebookResult(ActionRequest request, ActionResponse response) {
        try {
            String sharedNotebookResultUuid = ParamUtil.getString(request, "sharedNotebookResultUuid");
            LOGGER.debug("deleteNotebookResult: " + sharedNotebookResultUuid);
            storageServiceFacade.deleteFile(sharedNotebookResultUuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            SessionErrors.add(request, e.getClass().getName());
        }
    }
}