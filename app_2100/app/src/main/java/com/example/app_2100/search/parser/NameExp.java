package com.example.app_2100.search.parser;

public class NameExp extends Exp{
    private final Exp wordExp;
    private Exp nameExp;

    public NameExp(Exp wordExp, Exp nameExp) {
        this.wordExp = wordExp;
        this.nameExp = nameExp;
    }

    public NameExp(Exp wordExp) {
        this.wordExp = wordExp;
    }

    @Override
    public String show() {
        if (nameExp == null) {
            return wordExp.show();
        }
        return wordExp.show() + " " + nameExp.show();
    }
}
