package com.example.app_2100.search.parser;

/**
 * This class builds upon codes from lab exercises however overall content has been modified to fit our own requirement.
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
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
