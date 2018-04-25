package com.jnj.honeur.catalogue.model;

import com.jnj.honeur.storage.model.StorageLogEntry;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Peter Moorthamer
 */
public class SharedNotebookStatisticsCalculatorTest {

    private User user1;User user2;User user3;User user4;
    private Organization org1;Organization org2;Organization org3;
    private Notebook notebook;
    private SharedNotebook sharedNotebook;
    private SharedNotebookStatisticsCalculator calculator;

    @Before
    public void setup() {
        user1 = createUser(1L);
        user2 = createUser(2L);
        user3 = createUser(3L);
        user4 = createUser(4L);

        org1 = createOrganization(1L);
        org2 = createOrganization(2L);
        org3 = createOrganization(3L);

        List<Organization> organizations = Arrays.asList(org1, org2, org3);
        List<User> users = Arrays.asList(user1, user2, user3, user4);

        notebook = new Notebook();

        sharedNotebook = new SharedNotebook();
        sharedNotebook.setNotebook(notebook);
        sharedNotebook.setStorageLogEntryList(Arrays.asList(
                createStorageLogEntry(StorageLogEntry.Action.DOWNLOAD, user1.getEmailAddress()),
                createStorageLogEntry(StorageLogEntry.Action.DOWNLOAD, user2.getEmailAddress()),
                createStorageLogEntry(StorageLogEntry.Action.DOWNLOAD, user3.getEmailAddress())));

        SharedNotebookResult notebookResult1 = new SharedNotebookResult();
        notebookResult1.setSharedNotebook(sharedNotebook);
        notebookResult1.setStorageLogEntryList(Collections.singletonList(
                createStorageLogEntry(StorageLogEntry.Action.UPLOAD, user1.getEmailAddress())));

        SharedNotebookResult notebookResult2 = new SharedNotebookResult();
        notebookResult2.setSharedNotebook(sharedNotebook);
        notebookResult2.setStorageLogEntryList(Collections.singletonList(
                createStorageLogEntry(StorageLogEntry.Action.UPLOAD, user2.getEmailAddress())));

        sharedNotebook.addNotebookResult(notebookResult1);
        sharedNotebook.addNotebookResult(notebookResult2);

        notebook.setSharedNotebookList(Collections.singletonList(sharedNotebook));

        calculator = new SharedNotebookStatisticsCalculator(notebook, organizations, users);
    }

    private Organization createOrganization(long number) {
        Organization org = Mockito.mock(Organization.class);
        when(org.getPrimaryKey()).thenReturn(number);
        when(org.getName()).thenReturn("Org_" + number);
        return org;
    }

    private User createUser(long number) {
        User user = Mockito.mock(User.class);
        when(user.getPrimaryKey()).thenReturn(number);
        when(user.getEmailAddress()).thenReturn("test_" + number + "@test.com");
        return user;
    }

    private StorageLogEntry createStorageLogEntry(StorageLogEntry.Action action, String user) {
        StorageLogEntry logEntry = new StorageLogEntry();
        logEntry.setAction(action);
        logEntry.setUser(user);
        return logEntry;
    }

    @Test
    public void calculate() {
        List<SharedNotebookStatistics> statistics = calculator.calculate();
        assertNotNull(statistics);
    }

    @Test
    public void getDownloadUsers() {
        List<User> downloadUsers = calculator.getDownloadUsers(sharedNotebook);
        assertNotNull(downloadUsers);
        assertEquals(3, downloadUsers.size());
        assertTrue(downloadUsers.contains(user1));
        assertTrue(downloadUsers.contains(user2));
        assertTrue(downloadUsers.contains(user3));
    }

    @Test
    public void getResultUploadUsers() {
        List<User> resultUploadUsers = calculator.getResultUploadUsers(sharedNotebook);
        assertNotNull(resultUploadUsers);
        assertEquals(2, resultUploadUsers.size());
        assertTrue(resultUploadUsers.contains(user1));
        assertTrue(resultUploadUsers.contains(user2));
    }
}