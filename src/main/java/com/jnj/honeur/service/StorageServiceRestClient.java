package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Notebook;
import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class StorageServiceRestClient {

    private static final Logger LOGGER = Logger.getLogger(StorageServiceRestClient.class.getName());

    private String apiUrl;

    public StorageServiceRestClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<StorageFileInfo> getStudyNotebooks(final Long studyId) {
        final RestTemplate restTemplate = new RestTemplate();
        final String serviceUrl = apiUrl + "/notebooks/" + studyId;
        ResponseEntity<StorageFileInfo[]> response = restTemplate.getForEntity(serviceUrl, StorageFileInfo[].class);
        StorageFileInfo[] storedStudyNotebooks = response.getBody();
        if (storedStudyNotebooks == null || storedStudyNotebooks.length == 0) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(Arrays.asList(storedStudyNotebooks));
        }
    }

    public List<StorageLogEntry> getNotebookStorageLog(final String notebookUuid) {
        final RestTemplate restTemplate = new RestTemplate();
        final String serviceUrl = apiUrl + "/storage-logs/notebook/" + notebookUuid;
        ResponseEntity<StorageLogEntry[]> response = restTemplate.getForEntity(serviceUrl, StorageLogEntry[].class);
        StorageLogEntry[] storageLogEntries = response.getBody();
        if (storageLogEntries == null || storageLogEntries.length == 0) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(Arrays.asList(storageLogEntries));
        }
    }

    public String shareStudyNotebook(Study study, Notebook notebook) {
        final RestTemplate restTemplate = new RestTemplate();
        final String serviceUrl = apiUrl + "/notebooks/zeppelin/" + study.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("zeppelinNotebookId", notebook.getExternalId());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        URI location = restTemplate.postForLocation(serviceUrl, request);

        return parseUuid(location);
    }

    public List<StorageFileInfo> getNotebookResults(final Long studyId, final String notebookUuid) {
        final RestTemplate restTemplate = new RestTemplate();
        final String serviceUrl = apiUrl + "/notebook-results/" + studyId + "/" + notebookUuid;
        ResponseEntity<StorageFileInfo[]> response = restTemplate.getForEntity(serviceUrl, StorageFileInfo[].class);
        StorageFileInfo[] notebookResults = response.getBody();
        if (notebookResults == null || notebookResults.length == 0) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(Arrays.asList(notebookResults));
        }
    }

    private String parseUuid(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }

}
