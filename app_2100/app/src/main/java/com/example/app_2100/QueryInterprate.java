package com.example.app_2100;

import java.util.*;

public class QueryInterprate {
    public static Query Interprate(List<String> query){
        Query out = new Query();
        for(String item : query){
            if(URLValidator.isValidURL(item)){

                out.URL = item;
            } else if (IDValidator.startsWithIdAndSixDigits(item)) {

                out.ID = item;
            } else if (TitleValidator.validate(item)) {

                out.Title = item;
            }
        }
    return out;
    }
}



