package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Notebook;
import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Rest client for the HONEUR Storage Service (HSS)
 * @author Peter Moorthamer
 */

public class StorageServiceRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceRestClient.class);

    private String apiUrl;

    public StorageServiceRestClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<StorageFileInfo> getStudyNotebooks(final Long studyId) {
        final String serviceUrl = apiUrl + "/notebooks/" + studyId;
        return getStorageFileInfo(serviceUrl);
    }

    public List<StorageLogEntry> getNotebookStorageLog(final String notebookUuid) {
        final String serviceUrl = apiUrl + "/storage-logs/notebook/" + notebookUuid;
        return getStorageLogs(serviceUrl);
    }

    public String shareStudyNotebook(final Long studyId, Notebook notebook) {
        final RestTemplate restTemplate = new RestTemplate();
        final String serviceUrl = apiUrl + "/notebooks/zeppelin/" + studyId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("zeppelinNotebookId", notebook.getExternalId());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        URI location = restTemplate.postForLocation(serviceUrl, request);

        return parseUuid(location);
    }

    public List<StorageFileInfo> getNotebookResults(final Long studyId, final String notebookUuid) {
        final String serviceUrl = apiUrl + "/notebook-results/" + studyId + "/" + notebookUuid;
        return getStorageFileInfo(serviceUrl);
    }

    public List<StorageLogEntry> getNotebookResultStorageLog(final String notebookResultUuid) {
        final String serviceUrl = apiUrl + "/storage-logs/notebook-result/" + notebookResultUuid;
        return getStorageLogs(serviceUrl);
    }

    public void deleteFile(String uuid) {
        LOGGER.info("StorageServiceRestClient: deleteFile: " + uuid);
        final String serviceUrl = apiUrl + "/file/" + uuid;
        LOGGER.info("serviceUrl: " + serviceUrl);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(serviceUrl);
    }

    private List<StorageFileInfo> getStorageFileInfo(final String serviceUrl) {
        final RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StorageFileInfo[]> response = restTemplate.getForEntity(serviceUrl, StorageFileInfo[].class);
        StorageFileInfo[] StorageFileInfoArray = response.getBody();
        if (StorageFileInfoArray == null || StorageFileInfoArray.length == 0) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(Arrays.asList(StorageFileInfoArray));
        }
    }

    private List<StorageLogEntry> getStorageLogs(String serviceUrl) {
        final RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StorageLogEntry[]> response = restTemplate.getForEntity(serviceUrl, StorageLogEntry[].class);
        StorageLogEntry[] storageLogEntries = response.getBody();
        if (storageLogEntries == null || storageLogEntries.length == 0) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(Arrays.asList(storageLogEntries));
        }
    }

    private String parseUuid(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }

}
