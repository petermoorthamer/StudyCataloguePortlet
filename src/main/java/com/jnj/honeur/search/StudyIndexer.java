package com.jnj.honeur.search;

import com.jnj.honeur.model.Study;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;
import java.util.Locale;

@Component(
        immediate = true,
        service = Indexer.class
)
public class StudyIndexer extends BaseIndexer<Study> {

    private static final String CLASS_NAME = Study.class.getName();
    private static final long COMPANY_ID = 20116;
    private static final long GROUP_ID = 35205;

    @Reference
    protected IndexWriterHelper indexWriterHelper;
    //@Reference
    private StudyServiceFacade studyServiceFacade = StudyServiceFacade.getInstance();


    public StudyIndexer() {
        setDefaultSelectedFieldNames(
                Field.ASSET_TAG_NAMES, Field.COMPANY_ID, Field.CONTENT,
                Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK, Field.GROUP_ID,
                Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID, Field.TITLE, Field.UID);
        setPermissionAware(true);
        setFilterSearch(true);
    }

    @Override
    protected void doDelete(Study study) throws Exception {
        deleteDocument(COMPANY_ID, study.getId());
    }

    @Override
    protected Document doGetDocument(Study study) throws Exception {
        Document document = new DocumentImpl();

        document.addDate(Field.MODIFIED_DATE, study.getModifiedDate());

        Locale defaultLocale =
                PortalUtil.getSiteDefaultLocale(GROUP_ID);
        String localizedField = LocalizationUtil.getLocalizedName(
                Field.TITLE, defaultLocale.toString());

        document.addText(localizedField, study.getName());
        return document;
    }

    @Override
    protected Summary doGetSummary(
            Document document, Locale locale, String snippet,
            PortletRequest portletRequest, PortletResponse portletResponse) {

        Summary summary = createSummary(document);
        summary.setMaxContentLength(200);
        return summary;
    }


    @Override
    protected void doReindex(String[] ids) throws Exception {
        List<Study> studies = studyServiceFacade.findStudies();
        for(Study study:studies) {
            doReindex(study);
        }
    }

    @Override
    protected void doReindex(Study study)
            throws Exception {

        Document document = getDocument(study);
        indexWriterHelper.updateDocument(
                getSearchEngineId(), COMPANY_ID, document,
                isCommitImmediately());
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        Study study = studyServiceFacade.findStudyById(classPK);
        doReindex(study);
    }

    @Override
    public boolean hasPermission(
            PermissionChecker permissionChecker, String entryClassName,
            long entryClassPK, String actionId)
            throws Exception {

        //return StudyPermission.contains(permissionChecker, entryClassPK, ActionKeys.VIEW);
        return super.hasPermission(permissionChecker, entryClassName, entryClassPK, actionId);
    }

    @Override
    public void postProcessContextBooleanFilter(
            BooleanFilter contextBooleanFilter, SearchContext searchContext)
            throws Exception {
        addStatus(contextBooleanFilter, searchContext);
    }

    @Override
    public void postProcessSearchQuery(
            BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
            SearchContext searchContext)
            throws Exception {

        addSearchLocalizedTerm(searchQuery, searchContext, Field.TITLE, false);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }
}
