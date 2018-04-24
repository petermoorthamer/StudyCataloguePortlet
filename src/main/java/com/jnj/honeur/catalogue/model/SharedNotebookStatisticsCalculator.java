package com.jnj.honeur.catalogue.model;

import com.jnj.honeur.storage.model.StorageLogEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SharedNotebookStatisticsCalculator {

    private static Logger LOGGER = Logger.getLogger(SharedNotebookStatisticsCalculator.class.getName());

    private Notebook notebook;
    private List<Organization> organizations;
    private List<User> users;

    public SharedNotebookStatisticsCalculator(
            Notebook notebook,
            List<Organization> organizations,
            List<User> users) {
        this.notebook = notebook;
        this.organizations = organizations;
        this.users = users;
    }

    public List<SharedNotebookStatistics> calculate() {
        List<SharedNotebookStatistics> statisticsList = new ArrayList<>();
        for(SharedNotebook sharedNotebook:notebook.getSharedNotebookList()) {
            SharedNotebookStatistics statistics = new SharedNotebookStatistics(sharedNotebook);
            statistics.setParticipatingOrganizations(getOrganizationNames());
            statistics.setDownloadingOrganizations(new ArrayList<>());
            statistics.setUploadingOrganizations(new ArrayList<>());
            statisticsList.add(statistics);
        }
        return statisticsList;
    }

    List<User> getDownloadUsers(final SharedNotebook sharedNotebook) {
        return sharedNotebook.getStorageLogEntryList().stream()
                .filter(logEntry -> StorageLogEntry.Action.DOWNLOAD.equals(logEntry.getAction()) && logEntry.getUser() != null)
                .map(logEntry -> logEntry.getUser())
                .distinct()
                .map(username -> findUser(username))
                .filter(u -> u.isPresent())
                .map(u -> u.get())
                .collect(Collectors.toList());
    }

    List<User> getResultUploadUsers(final SharedNotebook sharedNotebook) {
        return sharedNotebook.getNotebookResults().stream()
                .flatMap(notebookResult -> notebookResult.getStorageLogEntryList().stream())
                .filter(logEntry -> StorageLogEntry.Action.UPLOAD.equals(logEntry.getAction()) && logEntry.getUser() != null)
                .map(logEntry -> logEntry.getUser())
                .distinct()
                .map(username -> findUser(username))
                .filter(user -> user.isPresent())
                .map(user -> user.get())
                .collect(Collectors.toList());
    }

    private List<String> getOrganizationNames() {
        return organizations.stream().map(org -> org.getName()).collect(Collectors.toList());
    }

    private Optional<User> findUser(String userPrincipalName) {
        if(userPrincipalName == null) {
            return Optional.empty();
        }
        return users.stream().filter(u -> userHasName(u, userPrincipalName)).findFirst();
    }

    private boolean userHasName(User user, String name) {
        try {
            return  name.equals(user.getScreenName()) ||
                    name.equals(user.getEmailAddress()) ||
                    name.equals(user.getLogin());
        } catch (PortalException e) {
            return false;
        }
    }

}
