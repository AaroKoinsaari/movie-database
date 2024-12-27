/*
 * Copyright (c) 2023-2024 Aaro Koinsaari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.moviedb.controllers;


/**
 * A utility class providing static methods to validate different input fields
 * related to movie data.
 */
public class InputValidator {

    /**
     * Checks if a given string is a valid year in the range 1900 to 2099.
     *
     * @param year Year to validate.
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
