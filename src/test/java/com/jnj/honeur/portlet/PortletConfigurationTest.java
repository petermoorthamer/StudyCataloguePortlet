package com.jnj.honeur.portlet;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PortletConfigurationTest {

    @Test
    public void getCatalogueServerBaseUrl() {
        System.out.println(PortletConfiguration.getCatalogueServerBaseUrl());
        assertNotNull(PortletConfiguration.getCatalogueServerBaseUrl());
    }

    @Test
    public void getStorageServerBaseUrl() {
        System.out.println(PortletConfiguration.getStorageServerBaseUrl());
        assertNotNull(PortletConfiguration.getStorageServerBaseUrl());
    }
}