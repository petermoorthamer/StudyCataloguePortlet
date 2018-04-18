package com.jnj.honeur.catalogue.model;

import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;

import java.util.Date;
import java.util.List;

public abstract class AbstractStorageFile {

    protected StorageFileInfo storageFileInfo;
    protected List<StorageLogEntry> storageLogEntryList;
    protected String downloadUrl;

    public String getUuid() {
        return storageFileInfo.getUuid();
    }

    public Date getLastModified() {
        return getStorageFileInfo().getLastModified();
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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


}
