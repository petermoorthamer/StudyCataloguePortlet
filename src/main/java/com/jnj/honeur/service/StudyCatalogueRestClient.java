package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Study;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST client for saving and retrieving studies of the HONEUR Study Catalogue via the REST API
 * @author Peter Moorthamer
 */
public class StudyCatalogueRestClient {

    private static final Logger LOGGER = Logger.getLogger(StudyCatalogueRestClient.class.getName());

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
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Study> studyResponse = restTemplate.getForEntity(apiUrl + "/" + studyId, Study.class);
        return studyResponse.getBody();
    }

    public List<Study> findAllStudies() {
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

}
