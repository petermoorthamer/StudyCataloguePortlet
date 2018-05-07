package com.jnj.honeur.catalogue.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a notebook file that has been shared with one or more organizations
 * @author Peter Moorthamer
 */
public class SharedNotebook extends AbstractStorageFile {

    public static final String VERSION_PREFIX = "V";
    private static final String ID_SEPARATOR = ",";

    private Long id;
    private Notebook notebook;
    private String sharedOrganizationIds;
    private String version = "";
    private String resultSummary;
    private List<SharedNotebookResult> notebookResults = new ArrayList<>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Notebook getNotebook() {
        return notebook;
    }
    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public String getSharedOrganizationIds() {
        return sharedOrganizationIds;
    }
    public void setSharedOrganizationIds(String sharedOrganizationIds) {
        this.sharedOrganizationIds = sharedOrganizationIds;
    }

    public boolean isSharedWith(String organizationId) {
        return sharedOrganizationIds != null && sharedOrganizationIds.contains(organizationId);
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getResultSummary() {
        return resultSummary;
    }
    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public List<SharedNotebookResult> getNotebookResults() {
        return notebookResults;
    }
    public void setNotebookResults(List<SharedNotebookResult> notebookResults) {
        this.notebookResults = notebookResults;
    }

    public boolean addNotebookResult(SharedNotebookResult notebookResult) {
        return notebookResults.add(notebookResult);
    }

    public Optional<SharedNotebookResult> findNotebookResult(final String notebookResultUuid) {
        return getNotebookResults().stream().filter(r -> r.getUuid().equals(notebookResultUuid)).findFirst();
    }
}
