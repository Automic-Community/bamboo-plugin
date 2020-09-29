package com.automic.cda.ws.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automic.cda.ws.rest.exceptions.BambooCdaRuntimeException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public final class GenericResponseFilter extends ClientFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericResponseFilter.class);

    private static final int HTTP_SUCCESS_START = 200;
    private static final int HTTP_SUCCESS_END = 299;

    @Override
    public ClientResponse handle(ClientRequest request) {

        ClientResponse response = getNext().handle(request);
        LOGGER.info(String.format("Request entity -> %s %nQuery params   -> %s", request.getEntity(), request.getURI().getQuery()));

        String responseMsg = "";
        int status = response.getStatus();
        if (!(status >= HTTP_SUCCESS_START && status <= HTTP_SUCCESS_END)) {
            LOGGER.error("Error occured while calling url " + request.getURI().toString() + " status [ " + status + " ]");
            responseMsg = response.getEntity(String.class);
            try {
                JsonObject responseJson = new JsonParser().parse(responseMsg).getAsJsonObject();
                responseMsg = (!responseJson.get("error").isJsonNull()) ? responseJson.get("error").getAsString() : "";
            } catch (JsonSyntaxException jse) {
                LOGGER.error(responseMsg);
                responseMsg = "System error occured";
            }
            throw new BambooCdaRuntimeException(status, responseMsg);
        }
        return response;
    }

}
