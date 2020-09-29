package com.automic.cda.util;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automic.cda.constants.BambooConstants;
import com.automic.cda.ws.rest.CDARestWebService;
import com.automic.cda.ws.rest.exceptions.BambooCdaRuntimeException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * @author yogitadalal
 *
 */
public class RestServiceUtility extends CDARestWebService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceUtility.class);

	public RestServiceUtility(String cdaBaseUrl, String username, String password) {
		super(cdaBaseUrl, username, password);
	}

	public boolean createDeploymentPackage(String pkgJson) throws Exception {
		WebResource webResource = client.resource(cdaBaseURL).path(REST_API).path("packages");
		LOGGER.info("Calling url " + webResource.getURI());
		webResource.entity(pkgJson, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		return true;
	}
	
	
	/**
	 * This method will check if provided credentials are valid or not.
	 */
	public boolean validateCredentails() {
        try {
        	WebResource webResource = client.resource(cdaBaseURL).path(REST_API).path(BambooConstants.WORKFLOW)
                    .queryParam(BambooConstants.MAX_RESULTS, "1");
            LOGGER.info("Calling url " + webResource.getURI());
	        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	        // even if http status code is 200 we check if response content is json or not
	        int status = response.getStatus();
	        if (HttpStatus.SC_OK == status) {
	            List<String> headerValues = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);
	            if (headerValues.get(0).contains(MediaType.APPLICATION_JSON)) {
	                return true;
	            }
	        }
        } catch (ClientHandlerException | BambooCdaRuntimeException  e){
            return false;
        }
       
        return false;
    }
	
	
}