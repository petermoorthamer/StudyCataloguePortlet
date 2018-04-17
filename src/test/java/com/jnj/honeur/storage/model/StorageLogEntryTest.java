package com.jnj.honeur.storage.model;

import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StorageLogEntryTest {

    @Test
    public void setDateTime() {
        StorageLogEntry logEntry = new StorageLogEntry();
        logEntry.setDateTime("2018-04-16T17:10:55.383+02:00");
        assertNotNull(logEntry.getDateTime());
        assertEquals(ZonedDateTime.parse("2018-04-16T17:10:55.383+02:00"), logEntry.getDateTime());
    }
}