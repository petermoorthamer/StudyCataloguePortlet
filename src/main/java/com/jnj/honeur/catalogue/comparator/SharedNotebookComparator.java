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
        if(n1.getCreationDate() != null && n2.getCreationDate() != null) {
            return n1.getCreationDate().compareTo(n2.getCreationDate());
        } else if(n1.getId() != null && n2.getId() != null) {
            return n1.getId().compareTo(n2.getId());
        }
        return 1;
    }

}
