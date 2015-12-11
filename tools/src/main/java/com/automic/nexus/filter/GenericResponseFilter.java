package com.automic.nexus.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.exception.AutomicRuntimeException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the response to validate it.
 *
 */

public class GenericResponseFilter extends ClientFilter {

    private static final int HTTP_SUCCESS_START = 200;
    private static final int HTTP_SUCCESS_END = 299;

    private static final Logger LOGGER = LogManager.getLogger(GenericResponseFilter.class);

    @Override
    public ClientResponse handle(ClientRequest request) {
        // cookie addition/updation goes here

        ClientResponse response = getNext().handle(request);
        if (!(response.getStatus() >= HTTP_SUCCESS_START && response.getStatus() <= HTTP_SUCCESS_END)) {
            LOGGER.error("Response code for " + request.getURI() + " is " + response.getStatus());
            String errorMsg = response.getEntity(String.class);
            LOGGER.error(errorMsg);
            throw new AutomicRuntimeException(errorMsg);

        }
        return response;
    }

}
