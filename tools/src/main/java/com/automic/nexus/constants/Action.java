package com.automic.nexus.constants;

/**
 * @author sumitsamson
 *
 */

/**
 * Action enum is used to define the valid user action
 */

public enum Action {

    UPLOAD_ARTIFACT;

    public static String getActionNames() {
        Action[] actions = Action.values();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < actions.length; i++) {
            sb.append(actions[i].name());
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }
}
