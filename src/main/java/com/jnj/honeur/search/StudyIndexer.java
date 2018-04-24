package com.jnj.honeur.search;

import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component(
        immediate = true,
        service = Indexer.class
)
public class StudyIndexer extends BaseIndexer<Study> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyIndexer.class);

    private static final String CLASS_NAME = Study.class.getName();
    private static final long COMPANY_ID = 20116;
    private static final long GROUP_ID = 35205;

    @Reference
    private IndexWriterHelper indexWriterHelper;
    @Reference
    private StudyServiceFacade studyServiceFacade;

    public StudyIndexer() {
        LOGGER.info("New StudyIndexer");

        setDefaultSelectedFieldNames(
                Field.COMPANY_ID, Field.TITLE, Field.CONTENT,
                Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK, Field.GROUP_ID,
                Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID, Field.UID);

        setDefaultSelectedLocalizedFieldNames(Field.TITLE, Field.CONTENT);
        setFilterSearch(true);
        setPermissionAware(false);
    }

    @Override
    protected void doDelete(Study study) throws Exception {
        LOGGER.info("doDelete: " + study);
        deleteDocument(COMPANY_ID, study.getId());
    }

    @Override
    protected Document doGetDocument(Study study) throws Exception {
        LOGGER.info("doGetDocument: " + study);

        final Document document = new DocumentImpl();

        String className = Study.class.getName();
        long classPK = study.getId();
        DocumentHelper documentHelper = new DocumentHelper(document);
        documentHelper.setEntryKey(className, classPK);
        document.addUID(className, classPK);
        document.addKeyword(Field.COMPANY_ID, PortalUtil.getDefaultCompanyId());
        document.addKeyword(Field.GROUP_ID, GROUP_ID);
        document.addKeyword(Field.SCOPE_GROUP_ID, GROUP_ID);
        document.addKeyword(Field.STATUS, WorkflowConstants.STATUS_APPROVED);

        Locale defaultLocale = PortalUtil.getSiteDefaultLocale(GROUP_ID);
        String titleLocalizedField = LocalizationUtil.getLocalizedName(Field.TITLE, defaultLocale.toString());
        String contentLocalizedField = LocalizationUtil.getLocalizedName(Field.CONTENT, defaultLocale.toString());

        document.addText(titleLocalizedField, study.getName());
        document.addText(contentLocalizedField, study.getDescription());
        document.addText("name", study.getName());
        document.addText("number", study.getNumber());
        document.addText("description", study.getDescription());
        document.addText("acknowledgments", study.getAcknowledgments());
        if(study.getModifiedDate() != null) {
            document.addDate(Field.MODIFIED_DATE, study.getModifiedDate().getTime());
        }

        LOGGER.info("Document: " + document);
        return document;
    }

    @Override
    protected Summary doGetSummary(
            Document document, Locale locale, String snippet,
            PortletRequest portletRequest, PortletResponse portletResponse) {

        LOGGER.info("doGetSummary: " + document);

        Summary summary = createSummary(document);
        summary.setMaxContentLength(200);
        return summary;
    }


    @Override
    protected void doReindex(String[] ids) throws Exception {
        LOGGER.info("doReindex ids: " + ids);

        List<Study> studies = studyServiceFacade.findStudies();
        for(Study study:studies) {
            doReindex(study);
        }
    }

    @Override
    protected void doReindex(Study study) throws Exception {
        LOGGER.info("doReindex study: " + study);

        Document document = getDocument(study);
        indexWriterHelper.updateDocument(
                getSearchEngineId(), COMPANY_ID, document,
                isCommitImmediately());
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        LOGGER.info(String.format("doReindex %s with PK %s: ", className, classPK));
        Study study = studyServiceFacade.findStudyById(classPK);
        doReindex(study);
    }

    @Override
    public boolean hasPermission(
            PermissionChecker permissionChecker, String entryClassName,
            long entryClassPK, String actionId)
            throws Exception {

        LOGGER.info("hasPermission");

        //return StudyPermission.contains(permissionChecker, entryClassPK, ActionKeys.VIEW);
        //return super.hasPermission(permissionChecker, entryClassName, entryClassPK, actionId);
        return true;
    }

    @Override
    public void postProcessContextBooleanFilter(
            BooleanFilter contextBooleanFilter, SearchContext searchContext)
            throws Exception {

        LOGGER.info("postProcessContextBooleanFilter");
        addStatus(contextBooleanFilter, searchContext);
    }

    @Override
    public void postProcessSearchQuery(
            BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
            SearchContext searchContext)
            throws Exception {

        LOGGER.info("postProcessSearchQuery");
        addSearchLocalizedTerm(searchQuery, searchContext, Field.TITLE, true);
        addSearchLocalizedTerm(searchQuery, searchContext, Field.CONTENT, true);
        addSearchTerm(searchQuery, searchContext, "name", true);
        addSearchTerm(searchQuery, searchContext, "number", true);
        addSearchTerm(searchQuery, searchContext, "description", true);
        addSearchTerm(searchQuery, searchContext, "acknowledgments", true);

        Map<String, Object> params = (LinkedHashMap<String, Object>)searchContext.getAttribute("params");
        if (params != null) {
            String expandoAttributes = (String)params.get("expandoAttributes");
            if (Validator.isNotNull(expandoAttributes)) {
                LOGGER.info("expandoAttributes: " + expandoAttributes);
                addSearchExpando(searchQuery, searchContext, expandoAttributes);
            }
        }
    }

    @Override
    public String getClassName() {
        LOGGER.debug("getClassName: " + CLASS_NAME);
        return CLASS_NAME;
    }
}
