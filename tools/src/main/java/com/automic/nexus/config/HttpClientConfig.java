package com.automic.nexus.config;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * This class is used to instantiate HTTP Client required by action(s).
 *
 */
public final class HttpClientConfig {

    private static ClientConfig config = new DefaultClientConfig();

    private HttpClientConfig() {
    }

    public static Client getClient(int connectionTimeOut, int readTimeOut) {

        Client client;
        config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, connectionTimeOut);
        config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, readTimeOut);

        client = Client.create(config);

        return client;
    }

    public static ClientConfig getClientConfig() {
        return config;
    }

}
