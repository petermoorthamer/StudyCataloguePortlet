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

    private Notebook notebook;
    private String version = "";
    private List<SharedNotebookResult> notebookResults = new ArrayList<>();

    public Notebook getNotebook() {
        return notebook;
    }
    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
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
