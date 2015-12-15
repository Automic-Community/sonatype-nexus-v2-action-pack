/**
 *
 */
package com.automic.nexus.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.config.HttpClientConfig;
import com.automic.nexus.constants.Constants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.filter.GenericResponseFilter;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.Validator;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class defines the execution of any action.It provides some initializations and validations on common inputs .The
 * child actions will implement its executeSpecific() method as per their own need.
 */
public abstract class AbstractHttpAction extends AbstractAction {
    private static final Logger LOGGER = LogManager.getLogger(AbstractHttpAction.class);

    /**
     * Service end point
     */
    protected String baseUrl;

    /**
     * Username to connect to Nexus
     */
    private String username;

    /**
     * Password to Nexus username
     */
    private String password;

    /**
     * Service end point
     */
    protected Client client;

    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeOut;

    /**
     * Read timeout in milliseconds
     */
    private int readTimeOut;

    public AbstractHttpAction() {
        addOption(Constants.READ_TIMEOUT, true, "Read timeout");
        addOption(Constants.CONNECTION_TIMEOUT, true, "connection timeout");
        addOption(Constants.BASE_URL, true, "Base URL of Nexus");
        addOption(Constants.NEXUS_USERNAME, true, "Username for Nexus Authentication");
        addOption(Constants.NEXUS_PASSWORD, true, "Password for Nexus user");
    }

    /**
     * This method initializes the arguments and calls the execute method.
     *
     * @throws AutomicException
     *             exception while executing an action
     */
    public final void execute() throws AutomicException {
        try {
            prepareCommonInputs();
            client = HttpClientConfig.getClient(this.connectionTimeOut, this.readTimeOut);
            client.addFilter(new GenericResponseFilter());
            if (!isAnonymousAccess()) {
                client.addFilter(new HTTPBasicAuthFilter(username, password));
            }
            executeSpecific();
        } finally {
            if (client != null) {
                client.destroy();
            }
        }
    }

    private void prepareCommonInputs() throws AutomicException {
        this.connectionTimeOut = CommonUtil.parseStringValue(getOptionValue(Constants.CONNECTION_TIMEOUT),
                Constants.MINUS_ONE);
        this.readTimeOut = CommonUtil.parseStringValue(getOptionValue(Constants.READ_TIMEOUT), Constants.MINUS_ONE);
        this.baseUrl = getOptionValue(Constants.BASE_URL);
        this.username = getOptionValue(Constants.NEXUS_USERNAME);
        this.password = getOptionValue(Constants.NEXUS_PASSWORD);

        try {
            NexusValidator.lessThan(readTimeOut, Constants.ZERO, "Read Timeout");
            NexusValidator.lessThan(connectionTimeOut, Constants.ZERO, "Connect Timeout");
            NexusValidator.checkNotEmpty(baseUrl, "Base URL");
        } catch (AutomicException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    private boolean isAnonymousAccess() {
        boolean ret = true;
        if (Validator.checkNotEmpty(username) || Validator.checkNotEmpty(password)) {
            ret = false;
        }
        return ret;
    }

    /**
     * Method to execute the action.
     *
     * @throws AutomicException
     */
    protected abstract void executeSpecific() throws AutomicException;

}
