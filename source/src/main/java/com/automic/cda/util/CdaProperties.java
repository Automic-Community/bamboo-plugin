package com.automic.cda.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CdaProperties {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(CdaProperties.class);
    private final Properties configProp = new Properties();
    private static  CdaProperties instance;

    private CdaProperties() {
        InputStream iStream = this.getClass().getClassLoader().getResourceAsStream("PluginProperties.properties");

        if (iStream != null) {
            try {
                configProp.load(iStream);
                LOGGER.info("PluginProperties.properties file loaded successfully");
            } catch (IOException e) {
                LOGGER.error("Fail to load PluginProperties.properties from classpath ", e);
            }
        }
    }

    public static CdaProperties getInstance() {
    	if(instance == null)
    		instance = new CdaProperties();
        return instance;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

}
