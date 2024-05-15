package com.example.app_2100.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDValidator {
    public static boolean startsWithIdAndSixDigits(String input) {
        String pattern = "^#id\\d{6}$";

        Pattern regex = Pattern.compile(pattern);

        Matcher matcher = regex.matcher(input);

        return matcher.matches();
    }
}
