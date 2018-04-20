package com.jnj.honeur.catalogue.model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a study record in the HONEUR Study Catalogue
 * @author Peter Moorthamer
 */

public class Study implements Serializable {

    private static final String ID_SEPARATOR = ",";

    private Long id;
    private String name;
    private String number;
    private String description;
    private String acknowledgments;
    private Long leadUserId;
    private String collaboratingOrganizationIds;
    private Calendar modifiedDate;
    private Set<Notebook> notebooks = new HashSet<>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getAcknowledgments() {
        return acknowledgments;
    }
    public void setAcknowledgments(String acknowledgments) {
        this.acknowledgments = acknowledgments;
    }

    public Long getLeadUserId() {
        return leadUserId;
    }
    public void setLeadUserId(Long leadUserId) {
        this.leadUserId = leadUserId;
    }

    public boolean isLeadUserId(Long leadUserId) {
        return this.leadUserId != null && this.leadUserId.equals(leadUserId);
    }

    public String getCollaboratingOrganizationIds() {
        return collaboratingOrganizationIds;
    }
    public void setCollaboratingOrganizationIds(String collaboratingOrganizationIds) {
        this.collaboratingOrganizationIds = collaboratingOrganizationIds;
    }

    public String[] getCollaboratorIds() {
        if(collaboratingOrganizationIds == null || collaboratingOrganizationIds.isEmpty()) {
            return null;
        }
        return this.collaboratingOrganizationIds.split(ID_SEPARATOR);
    }
    public void setCollaboratorIds(String[] collaboratorIds) {
        if(collaboratorIds == null) {
            this.collaboratingOrganizationIds = null;
        } else {
            this.collaboratingOrganizationIds = String.join(ID_SEPARATOR, collaboratorIds);
        }
    }

    public boolean hasCollaborator(String collaboratorId) {
        return collaboratingOrganizationIds != null && collaboratingOrganizationIds.contains(collaboratorId);
    }
    public boolean hasCollaborator(Long collaboratorId) {
        return hasCollaborator(collaboratorId.toString());
    }

    public Calendar getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(Calendar modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Set<Notebook> getNotebooks() {
        return notebooks;
    }
    public void setNotebooks(Set<Notebook> notebooks) {
        this.notebooks = notebooks;
    }

    public List<Notebook> getNotebookList() {
        return new ArrayList<>(getNotebooks());
    }

    public boolean addNotebook(final Notebook notebook) {
        Study study = new Study();
        study.setId(getId());
        notebook.setStudy(study);
        return notebooks.add(notebook);
    }
    public boolean removeNotebook(Notebook notebook) {
        return this.notebooks.remove(notebook);
    }

    public Optional<Notebook> findNotebook(final Long notebookId) {
        return getNotebooks().stream().filter(n -> n.getId().equals(notebookId)).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(id == null) { return false; }
        Study study = (Study) o;
        return Objects.equals(id, study.id);
    }

    @Override
    public int hashCode() {
        if(id == null) { return super.hashCode(); }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Study{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
