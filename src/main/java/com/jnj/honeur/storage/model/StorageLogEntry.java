package com.jnj.honeur.storage.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents a storage log entry
 * @author Peter Moorthamer
 */
public class StorageLogEntry {

    public enum Action {
        UPLOAD, DOWNLOAD, DELETE
    }

    private Long id;
    private String user;
    private Action action;
    private String storageFileClass;
    private String storageFileName;
    private String storageFileUuid;
    private String storageFileKey;
    private ZonedDateTime dateTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }

    public String getStorageFileClass() {
        return storageFileClass;
    }
    public void setStorageFileClass(String storageFileClass) {
        this.storageFileClass = storageFileClass;
    }

    public String getStorageFileName() {
        return storageFileName;
    }
    public void setStorageFileName(String storageFileName) {
        this.storageFileName = storageFileName;
    }

    public String getStorageFileUuid() {
        return storageFileUuid;
    }
    public void setStorageFileUuid(String storageFileUuid) {
        this.storageFileUuid = storageFileUuid;
    }

    public String getStorageFileKey() {
        return storageFileKey;
    }
    public void setStorageFileKey(String storageFileKey) {
        this.storageFileKey = storageFileKey;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTimeString) {
        this.dateTime = ZonedDateTime.parse(dateTimeString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(id == null) {
            return super.equals(o);
        }
        StorageLogEntry that = (StorageLogEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        if(id == null) {
            return super.hashCode();
        }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StorageLogEntry{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", action=" + action +
                ", storageFileClass='" + storageFileClass + '\'' +
                ", storageFileName='" + storageFileName + '\'' +
                ", storageFileUuid='" + storageFileUuid + '\'' +
                ", storageFileKey='" + storageFileKey + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
