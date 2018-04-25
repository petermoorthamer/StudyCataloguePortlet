package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.comparator.StudyComparator;
import com.jnj.honeur.catalogue.model.Study;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetLinkLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
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

    private static final String CATALOGUE_SERVER_BASE_URL = System.getenv("CATALOGUE_SERVER_BASE_URL"); // https://localhost:8448

    private StudyCatalogueRestClient restClient = new StudyCatalogueRestClient(CATALOGUE_SERVER_BASE_URL + "/studies");

    private AssetEntryLocalService assetEntryLocalService;
    private AssetLinkLocalService assetLinkLocalService;

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
        return restClient.createStudy(study);
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study saveStudy(Study study) {
        LOGGER.info("Save study : " + study);
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
        LOGGER.info("findStudies for user " + user);
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
        /*AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId,
                groupId, study.getCreateDate(), study.getModifiedDate(),
                Study.class.getName(), study.getId(), study.getUuid(), 0,
                serviceContext.getAssetCategoryIds(),
                serviceContext.getAssetTagNames(), true, true, null, null, null, null,
                ContentTypes.TEXT_HTML, study.getName(), null, null, null,
                null, 0, 0, null);


            assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(),
                    serviceContext.getAssetLinkEntryIds(),
                    AssetLinkConstants.TYPE_RELATED);
        */
    }

}
