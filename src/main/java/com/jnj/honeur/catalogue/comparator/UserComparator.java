package com.jnj.honeur.catalogue.comparator;

import com.liferay.portal.kernel.model.User;

import java.util.Comparator;

/**
 * Comparator for sorting Liferay users their full name
 * @author Peter Moorthamer
 */
public class UserComparator implements Comparator<User> {

    @Override
    public int compare(User u1, User u2) {
        return u1.getFullName().compareTo(u2.getFullName());
    }

}
