package com.automic.nexus.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.constants.Action;
import com.automic.nexus.exception.AutomicException;

/**
 * @author sumitsamson
 *
 */

/**
 * Action Factory class for initializing the user action. This class will create the UserAction instances based on user
 * action Exception: Exception will be thrown when user action is not valid ,i.e,LOGIN,LOGOUT,READ,CREATE,DELETE etc
 */
public final class ActionFactory {

    private static final Logger LOGGER = LogManager.getLogger(ActionFactory.class);

    private ActionFactory() {
    }

    public static AbstractAction getAction(Action inputAction) throws AutomicException {

        AbstractAction action = null;

        switch (inputAction) {
            case UPLOAD_ARTIFACT:
            	action = new UploadArtifactAction();
                break;
            default:
                String msg = "Invalid Action.. Please enter valid action " + Action.getActionNames();
                LOGGER.error(msg);
                throw new AutomicException(msg);
        }
        return action;
    }

}
