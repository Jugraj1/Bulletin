package com.example.app_2100.search.parser;

/**
 * This class builds upon codes from lab exercises however overall content has been modified to fit our own requirement.
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
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
