/**
 *
 */
package com.automic.nexus.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.actions.AbstractAction;
import com.automic.nexus.actions.ActionFactory;
import com.automic.nexus.cli.Cli;
import com.automic.nexus.cli.CliOptions;
import com.automic.nexus.constants.Action;
import com.automic.nexus.constants.Constants;
import com.automic.nexus.exception.AutomicException;

/**
 * Helper class to delegate request to specific Action based on input arguments .
 * */
public final class ClientHelper {

    private static final Logger LOGGER = LogManager.getLogger(ClientHelper.class);

    private ClientHelper() {
    }

    /**
     * Method to delegate parameters to an instance of {@link AbstractAction} based on the value of Action parameter.
     *
     * @param map
     *            of options with key as option name and value is option value
     * @throws AutomicException
     */
    public static void executeAction(String[] args) throws AutomicException {
        String action = new Cli(new CliOptions(), args).getOptionValue(Constants.ACTION).toUpperCase();
        LOGGER.info("Execution starts for action [" + action + "]...");
        AbstractAction useraction = ActionFactory.getAction(Action.valueOf(action));
        useraction.executeAction(args);
    }
}
