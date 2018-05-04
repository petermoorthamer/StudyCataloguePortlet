package com.jnj.honeur.portlet;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class for loading the portlet configuration properties
 * @author Peter Moorthamer
 */
public class PortletConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortletConfiguration.class);

    private static Configuration configuration;
    private static Properties properties;

    private static Configuration getConfiguration() {
        if(configuration == null) {
            ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();
            if(classLoader != null) {
                configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");
            }
        }
        return configuration;
    }

    private static Properties getProperties() {
        if(properties == null) {
            try {
                InputStream input = PortletConfiguration.class.getClassLoader().getResourceAsStream("portlet.properties");
                properties = new Properties();
                properties.load(input);
            } catch (IOException e) {
                LOGGER.warn("Unable to read from properties file portlet.properties");
                properties = null;
            }
        }
        return properties;
    }

    private static String getProperty(String propertyName) {
        String propertyValue = System.getenv(propertyName);
        LOGGER.info(String.format("Environment variable %s = %s", propertyName, propertyValue));
        if(propertyValue == null && getProperties() != null) {
            propertyValue = getProperties().getProperty(propertyName);
            LOGGER.info(String.format("Portlet property %s = %s", propertyName, propertyValue));
        }
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
        return getProperty("CATALOGUE_SERVER_BASE_URL");
    }

    public static String getStorageServerBaseUrl() {
        return getProperty("STORAGE_SERVER_BASE_URL");
    }

}
