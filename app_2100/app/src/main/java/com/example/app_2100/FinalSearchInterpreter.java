package com.example.app_2100;

import java.util.*;

public class FinalSearchInterpreter {
    public static List<Query> process(String input){
        List<Query> list = new ArrayList<>();
        List<List<String>> list1 = QueryBuilder.parse(input);
        for(List<String> item : list1){
            list.add(QueryInterprate.Interprate(item));
            System.out.println(QueryInterprate.Interprate(item));
        }
        return list;
    }
}
