package com.automic.nexus.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;

/**
 *
 * Utility Class that has many utility methods to put validations on {@link Object}, {@link String}, {@link File}.
 *
 */
public final class Validator {

    private Validator() {
    }

    /**
     * Method to check if an Object is null
     *
     * @param field
     * @return true or false
     */
    public static boolean checkNotNull(Object field) {
        return field != null;
    }

    /**
     * Method to check if a String is not empty
     *
     * @param field
     * @return true if String is not empty else false
     */
    public static boolean checkNotEmpty(String field) {
        return field != null && !field.isEmpty();
    }

    /**
     * Method to check if file represented by a string literal exists or not
     *
     * @param filePath
     * @return true or false
     */
    public static boolean checkFileExists(File file) {
        return file.exists() && file.isFile();
    }

    /**
     * Method to check if path specified by string literal is a Directory and it exists or not
     *
     * @param dir
     * @return true or false
     */
    public static boolean checkDirectoryExists(File dir) {
        return dir.exists() && !dir.isFile();
    }

    /**
     * Method to check if Parent Folder exists or not
     *
     * @param filePath
     * @return true if exists else false
     */
    public static boolean checkFileDirectoryExists(File file) {
        File folderPath = file.getParentFile();
        return checkNotNull(folderPath) && folderPath.exists() && !folderPath.isFile();
    }

    /**
     * Method to check if a text matches the given pattern
     *
     * @param pattern
     *            pattern to match
     * @param text
     *            String to match the pattern
     * @return true or false
     */
    public static boolean isValidText(String pattern, String text) {
        return Pattern.matches(pattern, text);
    }

    public static void validateURI(String uri) throws AutomicException {

        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            new AutomicException(String.format(ExceptionConstants.INVALID_BASE_URL, uri), e);
        }
    }
}
