package com.jnj.honeur.catalogue.model;

import java.util.ArrayList;
import java.util.List;

public class SharedNotebook extends AbstractStorageFile {

    private Notebook notebook;
    private List<SharedNotebookResult> notebookResults = new ArrayList<>();

    public Notebook getNotebook() {
        return notebook;
    }
    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
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
}
