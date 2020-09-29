package com.automic.cda.ws.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automic.cda.util.CdaProperties;
import com.automic.cda.ws.rest.exceptions.BambooCdaRuntimeException;
import com.automic.cda.ws.rest.filter.GenericResponseFilter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public abstract class CDARestWebService implements AutoCloseable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CDARestWebService.class);
	
    protected static final String REST_API = CdaProperties.getInstance().getProperty("CdaRestEndpoint");

    protected String cdaBaseURL;
    protected Client client;

	    public CDARestWebService(String cdaBaseURL, String username, String password) {
	    	try {
		        this.cdaBaseURL = cdaBaseURL;
		        client = Client.create(getClientConfig());
		        client.addFilter(new HTTPBasicAuthFilter(username, password));
		        client.addFilter(new GenericResponseFilter());
		        LOGGER.info("Client initialized..");
	    	} catch (ClientHandlerException | BambooCdaRuntimeException  e){
	            LOGGER.error("Error occured while creating the client: "+ e.getMessage());
	    	}
	    }

	    
	 /**
	  *  Get Client Config
	  * @return ClientConfig
	  */
	    private ClientConfig getClientConfig() {
	        LOGGER.info("getClientConfig..");

	        ClientConfig config = new DefaultClientConfig();
	        LOGGER.info("CdaProperties.getInstance().getProperty(connectionTimeOut): "+CdaProperties.getInstance().getProperty("connectionTimeOut"));
	        config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT,
	                Integer.valueOf(CdaProperties.getInstance().getProperty("connectionTimeOut")));
	        config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT,
	                Integer.valueOf(CdaProperties.getInstance().getProperty("readTimeOut")));
	        return config;
	    }
	    
	@Override
	public void close() {
        if (null != client) {
            try {
                client.destroy();
                LOGGER.info("Client Destroyed..");
            } catch (Exception e) {
                LOGGER.error("Error occured while destroying the client.");
            }
        }
    }

	
	
}
