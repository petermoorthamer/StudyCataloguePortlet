package com.jnj.honeur.catalogue.model;

import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;

import java.util.List;

public class SharedNotebook {

    private Notebook notebook;
    private StorageFileInfo storageFileInfo;
    private List<StorageLogEntry> storageLogEntryList;
    private String downloadUrl;

    public Notebook getNotebook() {
        return notebook;
    }
    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public String getUuid() {
        return storageFileInfo.getUuid();
    }

    public StorageFileInfo getStorageFileInfo() {
        return storageFileInfo;
    }
    public void setStorageFileInfo(StorageFileInfo storageFileInfo) {
        this.storageFileInfo = storageFileInfo;
    }

    public List<StorageLogEntry> getStorageLogEntryList() {
        return storageLogEntryList;
    }
    public void setStorageLogEntryList(List<StorageLogEntry> storageLogEntryList) {
        this.storageLogEntryList = storageLogEntryList;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
