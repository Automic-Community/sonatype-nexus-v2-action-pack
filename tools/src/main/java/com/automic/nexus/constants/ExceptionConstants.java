/**
 *
 */
package com.automic.nexus.constants;

/**
 * @author sumitsamson
 *
 */
public final class ExceptionConstants {

    public static final String GENERIC_ERROR_MSG = "System Error occured";

    public static final String INVALID_ARGS = "Improper Args. Possible cause : %s";
    public static final String UNABLE_TO_CLOSE_STREAM = "Error while closing stream";
    public static final String UNABLE_TO_FLUSH_STREAM = "Error while flushing stream";

    public static final String UNABLE_TO_WRITEFILE = "Error writing file ";

    public static final String INVALID_FILE = "File [%s] is invalid. Possibly file does not exist ";
    public static final String EMPTY_DIRECTORY = "No Files to attach in the given folder [%s] ";

    public static final String INVALID_INPUT_PARAMETER = "Invalid value for parameter [%s] : [%s]";
    public static final String INVALID_BASE_URL = "Invalid Base URL [%s]";
    public static final String SYSTEM_ERROR = "System Error";

    private ExceptionConstants() {
    }

}