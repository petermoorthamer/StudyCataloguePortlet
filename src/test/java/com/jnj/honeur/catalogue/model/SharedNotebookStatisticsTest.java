package com.jnj.honeur.catalogue.model;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SharedNotebookStatisticsTest {

    private SharedNotebookStatistics statistics;

    @Before
    public void setup() {
        SharedNotebook sharedNotebook = new SharedNotebook();
        sharedNotebook.setVersion("V1");
        statistics = new SharedNotebookStatistics(sharedNotebook);
        statistics.setParticipatingOrganizations(Arrays.asList("1","2", "3", "4", "5"));
        statistics.setDownloadingOrganizations(Arrays.asList("1","2", "5"));
        statistics.setUploadingOrganizations(Arrays.asList("1","2"));
    }

    @Test
    public void getVersion() {
        assertEquals("V1", statistics.getVersion());
    }

    @Test
    public void getTotalCount() {
        assertEquals(5, statistics.getTotalCount());
    }

    @Test
    public void getDownloadCount() {
        assertEquals(3, statistics.getDownloadCount());
    }

    @Test
    public void getUploadCount() {
        assertEquals(2, statistics.getUploadCount());
    }

    @Test
    public void getDownloadPercentage() {
        assertEquals(new BigDecimal("60.0"), statistics.getDownloadPercentage());
    }

    @Test
    public void getUploadPercentage() {
        assertEquals(new BigDecimal("40.0"), statistics.getUploadPercentage());
    }
}