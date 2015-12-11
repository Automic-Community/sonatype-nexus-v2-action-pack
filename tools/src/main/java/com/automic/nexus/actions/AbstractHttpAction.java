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
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.Client;

/**
 * This class defines the execution of any action.It provides some initializations and validations on common inputs .The
 * child actions will implement its executeSpecific() method as per their own need.
 */
public abstract class AbstractHttpAction extends AbstractAction {
    private static final Logger LOGGER = LogManager.getLogger(AbstractHttpAction.class);

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

        try {
            NexusValidator.lessThan(readTimeOut, Constants.ZERO, "Read Timeout");
            NexusValidator.lessThan(connectionTimeOut, Constants.ZERO, "Connect Timeout");
        } catch (AutomicException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Method to execute the action.
     *
     * @throws AutomicException
     */
    protected abstract void executeSpecific() throws AutomicException;

}
