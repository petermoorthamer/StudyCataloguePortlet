package com.jnj.honeur.catalogue.model;

import com.jnj.honeur.catalogue.comparator.SharedNotebookComparator;

import java.io.Serializable;
import java.util.*;

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
    private String resultSummary;
    private Set<SharedNotebook> sharedNotebooks = new LinkedHashSet<>();

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
        if(this.externalId == null) {
            setExternalId(parseExternalIdFromUrl());
        }
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

    public Set<SharedNotebook> getSharedNotebooks() {
        return sharedNotebooks;
    }
    public void setSharedNotebooks(Set<SharedNotebook> sharedNotebooks) {
        this.sharedNotebooks = sharedNotebooks;
    }

    public List<SharedNotebook> getSharedNotebookList() {
        final List<SharedNotebook> sharedNotebookList = new ArrayList<>(sharedNotebooks);
        initSharedNotebookVersions(sharedNotebookList);
        return sharedNotebookList;
    }

    public Optional<SharedNotebook> findSharedNotebook(final String notebookUuid) {
        return getSharedNotebookList().stream().filter(n -> n.getUuid().equals(notebookUuid)).findFirst();
    }

    public boolean isShared() {
        return !sharedNotebooks.isEmpty();
    }

    private void initSharedNotebookVersions(final List<SharedNotebook> sharedNotebookList) {
        sharedNotebookList.sort(new SharedNotebookComparator());
        int version = 1;
        for(SharedNotebook sn:sharedNotebookList) {
            sn.setVersion(SharedNotebook.VERSION_PREFIX + version++);
        }
    }

    public String getResultSummary() {
        return resultSummary;
    }
    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
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
