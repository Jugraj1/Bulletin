package com.example.app_2100.search.parser;

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
