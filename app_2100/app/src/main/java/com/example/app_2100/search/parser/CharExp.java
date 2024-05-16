package com.example.app_2100.search.parser;

public class CharExp {
    private Character character;
    public CharExp(Character h) {
        this.character = h;
    }
    public static final String specialCharacters = "!#$%^&*-_=+[]{};:'\"<>,.?/\\|1234567890";
    public static boolean isCharValid(char ch) {
        return (ch >= 'a') && (ch <= 'z')
                || (ch >= 'A') && (ch <= 'Z')
                || specialCharacters.contains(Character.toString(ch));
    }
}
