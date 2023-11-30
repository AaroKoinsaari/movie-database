package com.moviedb.controllers;

public class InputValidator {

    public static boolean isValidReleaseYear(String year) {
        // Tarkista, että syöte on numeerinen ja vuosiluku on määritellyllä välillä
        if (year != null && year.matches("\\d{4}")) {
            int y = Integer.parseInt(year);
            return y >= 1900 && y <= 2099;
        }
        return false;
    }
}
