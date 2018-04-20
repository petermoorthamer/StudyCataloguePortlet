package com.jnj.honeur.catalogue.comparator;

import com.jnj.honeur.catalogue.model.SharedNotebook;

import java.util.Comparator;

/**
 * Comparator for sorting shared notebooks by their creation date
 * @author Peter Moorthamer
 */
public class SharedNotebookComparator implements Comparator<SharedNotebook> {

    @Override
    public int compare(SharedNotebook n1, SharedNotebook n2) {
        return n1.getCreationDate().compareTo(n2.getCreationDate());
    }

}
