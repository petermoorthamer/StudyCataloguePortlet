package com.jnj.honeur.search;

import com.jnj.honeur.model.Study;
import com.jnj.honeur.model.StudyModel;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;

import java.util.Locale;

@Component(
        immediate = true,
        service = Indexer.class
)
public class StudyModelIndexer extends BaseIndexer<StudyModel> {
    public static final String CLASS_NAME = StudyModel.class.getName();
    private static final int SUMMARY_MAX_LENGTH = 200;

    //@Reference
    private StudyServiceFacade studyServiceFacade = StudyServiceFacade.getInstance();

    public StudyModelIndexer() {
        setDefaultSelectedFieldNames(StudyModel.COMPANY_ID, StudyModel.GROUP_ID, StudyModel.TITLE, StudyModel.UID);
        setPermissionAware(true);
        setFilterSearch(true);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker, String entryClassName, long entryClassPK, String actionId) throws Exception {
        return true;
    }

    @Override
    public void postProcessContextBooleanFilter(BooleanFilter contextBooleanFilter, SearchContext searchContext) throws Exception {
        addStatus(contextBooleanFilter, searchContext);
    }

    @Override
    public void postProcessSearchQuery(BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter, SearchContext searchContext) throws Exception {
        addSearchLocalizedTerm(searchQuery, searchContext, StudyModel.TITLE, false);
    }

    @Override
    protected void doDelete(StudyModel studyModel) throws Exception {
        deleteDocument(studyModel.getCompanyId(), studyModel.getId());
    }

    @Override
    protected Document doGetDocument(StudyModel studyModel) throws Exception {
        Locale defaulLocale = PortalUtil.getSiteDefaultLocale(studyModel.getGroupId());
        Document document = getBaseModelDocument(CLASS_NAME, studyModel);

//        document.addDate(Field.MODIFIED_DATE, studyModel.getModifiedDate());
        document.addText(
                LocalizationUtil.getLocalizedName(StudyModel.TITLE, defaulLocale.toString()),
                studyModel.getName()
        );

        return document;
    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, javax.portlet.PortletRequest portletRequest, javax.portlet.PortletResponse portletResponse) {
        Summary summary = createSummary(document);
        summary.setMaxContentLength(SUMMARY_MAX_LENGTH);
        return summary;
    }

    @Override
    protected void doReindex(StudyModel studyModel) throws Exception {
        IndexWriterHelperUtil.updateDocument(getSearchEngineId(), studyModel.getCompanyId(), getDocument(studyModel), isCommitImmediately());
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        Study study = studyServiceFacade.findStudyById(classPK);
        StudyModel studyModel = new StudyModel(study);
        doReindex(studyModel);
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {
        long companyId = GetterUtil.getLong(ids[0]);
//        reindexStudy(companyId);
    }
/*
    protected void reindexStudy(long companyId) throws PortalException {
        final IndexableActionableDynamicQuery query = StudyLocalServiceUtil.getIndexableActionableDynamicQuery();
        query.setCompanyId(companyId);

        query.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<StudyModel>() {
            @Override
            public void performAction(StudyModel studyModel) throws PortalException{
                Document document = getDocument(studyModel);
                query.addDocuments(document);
            }
        });

        query.setSearchEngineId(getSearchEngineId());
        query.performActions();
    }
    */
}
