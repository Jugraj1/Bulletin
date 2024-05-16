package com.example.app_2100.search.parser;

public class AuthorExp extends Exp{
    private final Exp nameExp;


    public AuthorExp(NameExp nameExp) {
        this.nameExp = nameExp;
    }

    @Override
    public String show(){
        return nameExp.show();
    }
}
