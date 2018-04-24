package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Notebook;
import com.jnj.honeur.catalogue.model.SharedNotebook;
import com.jnj.honeur.catalogue.model.SharedNotebookResult;
import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Facade for the StorageService REST API
 * @author Peter Moorthamer
 */
@Component(
        immediate = true,
        service = StorageServiceFacade.class
)
public class StorageServiceFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceFacade.class);

    private static final String STORAGE_SERVICE_BASE_URL = "https://localhost:8445";
    private static final StorageServiceFacade INSTANCE = new StorageServiceFacade();

    public static StorageServiceFacade getInstance() {
        return INSTANCE;
    }

    private StorageServiceRestClient restClient = new StorageServiceRestClient(STORAGE_SERVICE_BASE_URL);

    private List<StorageFileInfo> getStudyNotebooks(final Long studyId) {
        return restClient.getStudyNotebooks(studyId);
    }

    public String shareStudyNotebook(Long studyId, Notebook notebook) {
        return restClient.shareStudyNotebook(studyId, notebook);
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

    public SharedNotebookResult getNotebookResult(final SharedNotebook sharedNotebook, final String notebookResultUuid) {
        Optional<SharedNotebookResult> notebookResultOptional = sharedNotebook.findNotebookResult(notebookResultUuid);
        if(!notebookResultOptional.isPresent()) {
            return null;
        }
        SharedNotebookResult notebookResult = notebookResultOptional.get();
        notebookResult.setSharedNotebook(sharedNotebook);
        notebookResult.setStorageLogEntryList(restClient.getNotebookResultStorageLog(notebookResultUuid));
        return notebookResult;
    }

    public void deleteFile(String uuid) {
        restClient.deleteFile(uuid);
    }

    private String getFileDownloadUrl(final String uuid) {
        return STORAGE_SERVICE_BASE_URL + "/file/" + uuid;
    }


}
