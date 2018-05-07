package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.SharedNotebook;
import com.jnj.honeur.catalogue.model.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * REST client for saving and retrieving studies of the HONEUR Study Catalogue via the REST API
 * @author Peter Moorthamer
 */
public class StudyCatalogueRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyCatalogueRestClient.class);

    private String apiUrl;

    public StudyCatalogueRestClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Study createStudy(final Study study) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Study> request = new HttpEntity<>(study);
        URI location = restTemplate.postForLocation(apiUrl, request);
        study.setId(parseId(location));
        return study;
    }

    public Study saveStudy(final Study study) {
        LOGGER.info("StudyCatalogueRestClient.saveStudy: " + study);
        LOGGER.info("StudyCatalogueRestClient.saveStudy: " + study.getNotebooks());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Study> request = new HttpEntity<>(study);
        ResponseEntity response = restTemplate.exchange(apiUrl, HttpMethod.PUT, request, ResponseEntity.class);
        LOGGER.info("saveStudy response: " + response);
        return study;
    }

    public Study findStudyById(final Long studyId) {
        LOGGER.info("findStudyById: " + apiUrl + "/" + studyId);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Study> studyResponse = restTemplate.getForEntity(apiUrl + "/" + studyId, Study.class);
        return studyResponse.getBody();
    }

    public List<Study> findAllStudies() {
        LOGGER.info("findAllStudies on " + apiUrl);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Study[]>  studiesResponse = restTemplate.getForEntity(apiUrl, Study[].class);
        Study[] studies = studiesResponse.getBody();
        if(studies == null || studies.length == 0) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(Arrays.asList(studies));
        }
    }

    private Long parseId(URI uri) {
        String path = uri.getPath();
        return Long.valueOf(path.substring(path.lastIndexOf("/") + 1));
    }

    public SharedNotebook saveSharedNotebook(final SharedNotebook sharedNotebook) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<SharedNotebook> request = new HttpEntity<>(sharedNotebook);
        LOGGER.info("Notebook ID: " + sharedNotebook.getNotebook().getId());
        LOGGER.info("Study ID: " + sharedNotebook.getNotebook().getStudy().getId());
        Long studyId = sharedNotebook.getNotebook().getStudy().getId();
        Long notebookId = sharedNotebook.getNotebook().getId();
        String serviceUrl = apiUrl + "/" + studyId + "/" + notebookId + "/" + sharedNotebook.getUuid();
        LOGGER.info("saveSharedNotebook: " + serviceUrl);
        URI location = restTemplate.postForLocation(serviceUrl, request);
        sharedNotebook.setId(parseId(location));
        return sharedNotebook;
    }

}
