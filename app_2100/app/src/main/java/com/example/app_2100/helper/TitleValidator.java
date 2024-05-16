package com.example.app_2100.helper;

import java.util.regex.*;

public class TitleValidator {
    public static boolean validate(String title) {
        return (title.matches(".*\\s+.*")) && (title.matches("\".*\"") && !title.equals("\"\""));
    }
}

