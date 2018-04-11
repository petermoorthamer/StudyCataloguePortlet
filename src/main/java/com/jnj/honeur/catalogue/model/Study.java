package com.jnj.honeur.catalogue.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a study record in the HONEUR Study Catalogue
 * @author Peter Moorthamer
 */

public class Study implements Serializable {

    private Long id;
    private String name;
    private String number;
    private String description;
    private String acknowledgments;
    private Calendar modifiedDate;
    private Set<Notebook> notebooks;

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
