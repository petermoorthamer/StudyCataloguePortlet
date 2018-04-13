package com.jnj.honeur.catalogue.comparator;

import com.jnj.honeur.catalogue.model.Study;

import java.util.Comparator;

/**
 * Comparator for sorting storage files by their last modified date
 * @author Peter Moorthamer
 */
public class StudyComparator implements Comparator<Study> {

    @Override
    public int compare(Study s1, Study s2) {
        return s1.getId().compareTo(s2.getId());
    }

}
