package com.automic.nexus.util.validator;

import java.io.File;

import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.Validator;

/**
 * This class provides common validations as required by action(s).
 *
 */

public class NexusValidator {

    public static void checkNotEmpty(String parameter, String parameterName) throws AutomicException {
        if (!Validator.checkNotEmpty(parameter)) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
                    parameter));
        }
    }

    public static void checkFileDirectoryExists(File filePath) throws AutomicException {
        if (!Validator.checkFileDirectoryExists(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, filePath);
            throw new AutomicException(errMsg);
        }
    }

    public static void checkFileExists(File filePath) throws AutomicException {
        if (!Validator.checkFileExists(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, filePath);
            throw new AutomicException(errMsg);
        }
    }



    public static void lessThan(int value, int lessThan, String parameterName) throws AutomicException {
        if ( value < lessThan ) {
            String errMsg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName, value);
            throw new AutomicException(errMsg);
        }
    }

    public static void checkFilePathNotEmpty(String filePath) throws AutomicException {
        checkNotEmpty(filePath, "File Path");
    }



}
