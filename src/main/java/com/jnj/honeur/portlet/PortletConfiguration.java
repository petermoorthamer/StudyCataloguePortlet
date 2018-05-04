package com.jnj.honeur.portlet;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for loading the portlet configuration properties
 * @author Peter Moorthamer
 */
public class PortletConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortletConfiguration.class);

    private static Configuration configuration;

    private static Configuration getConfiguration() {
        if(configuration == null) {
            ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();
            if(classLoader != null) {
                configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");
            }
        }
        return configuration;
    }

    private static String getProperty(String propertyName) {
        String propertyValue = System.getenv(propertyName);
        LOGGER.info(String.format("Environment variable %s = %s", propertyName, propertyValue));
        if(propertyValue == null && getConfiguration() != null) {
            propertyValue = getConfiguration().get(propertyName);
            LOGGER.info(String.format("Portlet property %s = %s", propertyName, propertyValue));
        }
        if(propertyValue == null) {
            LOGGER.warn(String.format("Property %s could not be resolved!", propertyName));
        }
        return propertyValue;
    }

    public static String getCatalogueServerBaseUrl() {
        String catalogueServerBaseUrl = getProperty("CATALOGUE_SERVER_BASE_URL");
        if(catalogueServerBaseUrl == null) {
            catalogueServerBaseUrl = "https://dev.honeur.org/catalogue";
            LOGGER.warn("CATALOGUE_SERVER_BASE_URL: fallback to " + catalogueServerBaseUrl);
        }
        return catalogueServerBaseUrl;
    }

    public static String getStorageServerBaseUrl() {
        String storageServerBaseUrl = getProperty("STORAGE_SERVER_BASE_URL");
        if(storageServerBaseUrl == null) {
            storageServerBaseUrl = "https://dev.honeur.org/storage";
            LOGGER.warn("STORAGE_SERVER_BASE_URL: fallback to " + storageServerBaseUrl);
        }
        return storageServerBaseUrl;
    }

}
