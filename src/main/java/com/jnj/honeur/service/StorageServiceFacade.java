package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Notebook;
import com.jnj.honeur.catalogue.model.SharedNotebook;
import com.jnj.honeur.catalogue.model.SharedNotebookResult;
import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StorageServiceFacade {

    private static final Logger LOGGER = Logger.getLogger(StorageServiceFacade.class.getName());
    private static final String STORAGE_SERVICE_BASE_URL = "https://localhost:8445";
    private static final StorageServiceFacade INSTANCE = new StorageServiceFacade();

    public static StorageServiceFacade getInstance() {
        return INSTANCE;
    }

    private StorageServiceRestClient restClient = new StorageServiceRestClient(STORAGE_SERVICE_BASE_URL);

    private List<StorageFileInfo> getStudyNotebooks(final Long studyId) {
        return restClient.getStudyNotebooks(studyId);
    }

    public String shareStudyNotebook(Study study, Notebook notebook) {
        return restClient.shareStudyNotebook(study, notebook);
    }

    public List<SharedNotebook> getSharedStudyNotebooks(Study study, Notebook notebook) {
        final List<SharedNotebook> sharedNotebooks = new ArrayList<>();
        final List<StorageFileInfo> studyNotebooks = getStudyNotebooks(study.getId());
        for(StorageFileInfo fileInfo:studyNotebooks) {
            if(fileInfo.getOriginalFilename().contains(notebook.getExternalId())) {
                final SharedNotebook sharedNotebook = new SharedNotebook();
                sharedNotebook.setNotebook(notebook);
                sharedNotebook.setStorageFileInfo(fileInfo);
                sharedNotebook.setStorageLogEntryList(getNotebookStorageLog(fileInfo.getUuid()));
                sharedNotebook.setDownloadUrl(getFileDownloadUrl(fileInfo.getUuid()));
                sharedNotebooks.add(sharedNotebook);
            }
        }
        return sharedNotebooks;
    }

    public List<StorageLogEntry> getNotebookStorageLog(String notebookUuid) {
        return restClient.getNotebookStorageLog(notebookUuid);
    }

    public List<SharedNotebookResult> getNotebookResults(final Long studyId, final String notebookUuid) {
        final List<SharedNotebookResult> notebookResults = new ArrayList<>();
        for(StorageFileInfo storageFileInfo:restClient.getNotebookResults(studyId, notebookUuid)) {
            SharedNotebookResult notebookResult = new SharedNotebookResult();
            notebookResult.setStorageFileInfo(storageFileInfo);
            notebookResult.setDownloadUrl(getFileDownloadUrl(storageFileInfo.getUuid()));
            notebookResults.add(notebookResult);
        }
        return notebookResults;
    }

    private String getFileDownloadUrl(final String uuid) {
        return STORAGE_SERVICE_BASE_URL + "/file/" + uuid;
    }


}
