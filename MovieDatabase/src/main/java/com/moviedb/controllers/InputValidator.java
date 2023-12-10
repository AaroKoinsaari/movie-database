package com.moviedb.controllers;

/**
 * A utility class providing static methods to validate different input fields
 * related to movie data.
 */
public class InputValidator {

    /**
     * Checks if a given string is a valid year in the range 1900 to 2099.
     *
     * @param year The string representation of the year to validate.
     * @return true if the year is valid, false otherwise.
     */
    public static boolean isValidReleaseYear(String year) {
        if (year != null && !year.trim().isEmpty() && year.matches("\\d{4}")) {
            int y = Integer.parseInt(year);
            return y < 1900 || y > 2099;
        }
        return true;
    }


    /**
     * Validates if a given string is a valid text.
     * The text should be non-null, non-empty, and contain only letters, spaces, dots, apostrophes, or hyphens.
     *
     * @param text The text or the text to validate.
     * @return true if the text is valid, false otherwise.
     */
    public static boolean isValidText(String text) {
        return text == null || text.trim().isEmpty() || !text.matches("[\\p{L} ./,'-]+");
    }


    /**
     * Checks if the given string is a valid integer value.
     *
     * @param str The string to check.
     * @return true if the string is an integer, false otherwise.
     */
    public static boolean isInteger(String str) {
        return str == null || str.trim().isEmpty() || !str.matches("-?\\d+");
    }
}
