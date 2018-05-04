package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.comparator.StudyComparator;
import com.jnj.honeur.catalogue.model.Study;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLinkConstants;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetLinkLocalService;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Facade for the StudyService REST API
 * @author Peter Moorthamer
 */
@Component(
        immediate = true,
        service = StudyServiceFacade.class
)
public class StudyServiceFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyServiceFacade.class.getName());

    private StudyCatalogueRestClient restClient = new StudyCatalogueRestClient(getCatalogueServerBaseUrl() + "/studies");

    @Reference
    private AssetEntryLocalService assetEntryLocalService;
    @Reference
    private AssetLinkLocalService assetLinkLocalService;

    private static String getCatalogueServerBaseUrl() {
        String catalogueServerBaseUrl = System.getenv("CATALOGUE_SERVER_BASE_URL");
        LOGGER.info("CATALOGUE_SERVER_BASE_URL env. variable: " + catalogueServerBaseUrl);
        if(catalogueServerBaseUrl == null) {
            ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();
            if(classLoader != null) {
                final Configuration configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");
                catalogueServerBaseUrl = configuration.get("CATALOGUE_SERVER_BASE_URL");
                LOGGER.info("CATALOGUE_SERVER_BASE_URL portlet property: " + catalogueServerBaseUrl);
            }
        }
        if(catalogueServerBaseUrl == null) {
            LOGGER.warn("CATALOGUE_SERVER_BASE_URL could not be resolved!");
        }
        return catalogueServerBaseUrl;
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study createOrSaveStudy(final Study study) {
        if(study.getId() == null || Long.valueOf(0).equals(study.getId())) {
            return createStudy(study);
        } else {
            return saveStudy(study);
        }
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study createStudy(Study study) {
        LOGGER.info("Create study : " + study);
        if(study.getUuid() == null) {
            study.setUuid(UUID.randomUUID().toString());
        }
        if(study.getCreateDate() == null) {
            study.setCreateDate(Calendar.getInstance());
        }
        return restClient.createStudy(study);
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study saveStudy(Study study) {
        LOGGER.info("Save study : " + study);
        study.setModifiedDate(Calendar.getInstance());
        return restClient.saveStudy(study);
    }

    @Indexable(type = IndexableType.DELETE)
    public void deleteStudy(Study study) {

    }

    public List<Study> findStudies() {
        LOGGER.info("findAllStudies");
        List<Study> studies = restClient.findAllStudies();
        studies.sort(new StudyComparator());
        LOGGER.info("findStudies response: " + studies);
        return studies;
    }

    public List<Study> findStudies(final User user) {
        LOGGER.info("findStudies for user with id " + user.getPrimaryKey());
        if(user == null) {
            return Collections.emptyList();
        }
        final List<Study> studies = filterAccessibleStudies(restClient.findAllStudies(), user);
        studies.sort(new StudyComparator());
        return studies;
    }

    public List<Study> filterAccessibleStudies(final List<Study> studies, final User user) {
        return studies
                .stream()
                .filter(s -> isStudyAccessibleForUser(s, user))
                .collect(Collectors.toList());
    }

    public boolean isStudyAccessibleForUser(Study study, User user) {
        try {
            if(study.isLeadUserId(user.getPrimaryKey())) {
                return true;
            }
            if(user.getOrganizationIds() == null) {
                return false;
            }
            for(long organizationId:user.getOrganizationIds()) {
                if(study.hasCollaborator(organizationId)) {
                    return true;
                }
            }
        } catch (PortalException e) {
            return false;
        }
        return false;
    }

    public Study findStudyById(final Long id) {
        LOGGER.info("findStudyById: " + id);
        return restClient.findStudyById(id);
    }

    public void addStudyAsset(Study study, long userId, long groupId, ServiceContext serviceContext) throws PortalException {
        LOGGER.info("Add asset for study with id " + study.getId());
        final Date createDate = study.getCreateDate() != null ? study.getCreateDate().getTime() : null;
        final Date modifiedDate = study.getModifiedDate() != null ? study.getModifiedDate().getTime() : null;
        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId,
                groupId, createDate, modifiedDate,
                Study.class.getName(), study.getId(), study.getUuid(), 0,
                serviceContext.getAssetCategoryIds(),
                serviceContext.getAssetTagNames(), true, true, null, null, null,
                ContentTypes.TEXT_HTML, study.getName(), study.getDescription(), null, null,
                null, 0, 0, null);


        assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(),
                serviceContext.getAssetLinkEntryIds(),
                AssetLinkConstants.TYPE_RELATED);

    }

    public void updateStudyAsset(Study study, long userId, long groupId, ServiceContext serviceContext) throws PortalException {
        LOGGER.info("Update asset for study with id " + study.getId());
        final Date createDate = study.getCreateDate() != null ? study.getCreateDate().getTime() : null;
        final Date modifiedDate = study.getModifiedDate() != null ? study.getModifiedDate().getTime() : null;
        AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId,
                serviceContext.getScopeGroupId(),
                createDate, modifiedDate,
                Study.class.getName(), study.getId(), study.getUuid(),
                0, serviceContext.getAssetCategoryIds(),
                serviceContext.getAssetTagNames(), true, true,
                createDate, null, null,
                ContentTypes.TEXT_HTML, study.getName(), study.getDescription(),
                null, null, null, 0, 0,
                serviceContext.getAssetPriority());

        assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(),
                serviceContext.getAssetLinkEntryIds(),
                AssetLinkConstants.TYPE_RELATED);
    }

    public void deleteStudyAsset(Long studyId) throws PortalException  {
        LOGGER.info("Delete asset for study with id " + studyId);
        AssetEntry assetEntry = assetEntryLocalService.fetchEntry(
                Study.class.getName(), studyId);

        assetLinkLocalService.deleteLinks(assetEntry.getEntryId());

        assetEntryLocalService.deleteEntry(assetEntry);
    }

    /*public SharedNotebook saveSharedNotebook(final SharedNotebook sharedNotebook) {
        return restClient.saveSharedNotebook(sharedNotebook);
    }*/

}
