package com.moviedb.controllers;

public class InputValidator {

    public static boolean isValidReleaseYear(String year) {
        if (year != null && year.matches("\\d{4}")) {
            int y = Integer.parseInt(year);
            return y >= 1900 && y <= 2099;
        }
        return false;
    }


    public static boolean isValidDirectorName(String name) {
        return name != null && !name.isEmpty() && name.matches("[a-zA-ZäöåÄÖÅ '-]+");
    }
}
