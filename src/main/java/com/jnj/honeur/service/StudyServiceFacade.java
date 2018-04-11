package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Study;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.logging.Logger;

/**
 * Temporary implementation for testing
 */
@Component
public class StudyServiceFacade {

    private static final Logger LOGGER = Logger.getLogger(StudyServiceFacade.class.getName());
    private static final StudyServiceFacade INSTANCE = new StudyServiceFacade();

    public static StudyServiceFacade getInstance() {
        return INSTANCE;
    }

    private long companyId;
    private StudyCatalogueRestClient restClient = new StudyCatalogueRestClient("https://localhost:8448/studies");

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study createStudy(Study study) {
        return restClient.createStudy(study);
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study saveStudy(Study study) {
        return restClient.saveStudy(study);
    }

    @Indexable(type = IndexableType.DELETE)
    public void deleteStudy(Study study) {

    }

    public List<Study> findStudies() {
        List<Study> studies = restClient.findAllStudies();
        LOGGER.info("findStudies response: " + studies);
        return studies;
    }

    public Study findStudyById(final Long id) {
        return restClient.findStudyById(id);
    }

}
