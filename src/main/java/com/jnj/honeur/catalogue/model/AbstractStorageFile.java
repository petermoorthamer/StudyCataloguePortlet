package com.jnj.honeur.catalogue.model;

import com.jnj.honeur.storage.model.StorageFileInfo;
import com.jnj.honeur.storage.model.StorageLogEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Peter Moorthamer
 */
public abstract class AbstractStorageFile {

    protected StorageFileInfo storageFileInfo;
    protected List<StorageLogEntry> storageLogEntryList = new ArrayList<>();
    protected String downloadUrl;

    public String getUuid() {
        return storageFileInfo.getUuid();
    }

    public String getFilename() {
        return storageFileInfo.getOriginalFilename();
    }

    public Date getCreationDate() {
        return getStorageFileInfo().getLastModified();
    }

    public String getCreatedBy() {
        Optional<StorageLogEntry> uploadLogEntry = getStorageLogEntryList().stream().filter(l -> StorageLogEntry.Action.UPLOAD.equals(l.getAction())).findFirst();
        return uploadLogEntry.map(StorageLogEntry::getUser).orElse("");
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
