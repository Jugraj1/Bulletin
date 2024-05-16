package com.example.app_2100.search.parser;

/**
 * This class builds upon codes from lab exercises however overall content has been modified to fit our own requirement.
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
public class WordExp extends Exp {

    private final String charExp;
    private Exp wordExp;


    public WordExp(String charExp, Exp wordExp) {
        this.charExp = charExp;
        this.wordExp = wordExp;
    }

    public WordExp(String charExp) {
        this.charExp = charExp;
    }

    @Override
    public String show() {
        if (wordExp == null) {
            return charExp;
        }
        return charExp + wordExp.show();
    }
}
