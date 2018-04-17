package com.jnj.honeur.catalogue.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * Represents a notebook linked to a study record in the HONEUR Study Catalogue
 * @author Peter Moorthamer
 */

public class Notebook implements Serializable {

    private Long id;
    private String externalId;
    private String name;
    private String url;
    private Calendar modifiedDate;
    private Study study;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        if(externalId == null) {
            return parseExternalIdFromUrl();
        }
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    private String parseExternalIdFromUrl() {
        if(url == null || !url.contains("/")) {
            return null;
        }
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Calendar getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(Calendar modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Study getStudy() {
        return study;
    }
    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(id == null) { return false; }
        Notebook notebook = (Notebook) o;
        return Objects.equals(id, notebook.id);
    }

    @Override
    public int hashCode() {
        if(id == null) { return super.hashCode(); }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notebook{" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", study=" + study +
                '}';
    }
}
