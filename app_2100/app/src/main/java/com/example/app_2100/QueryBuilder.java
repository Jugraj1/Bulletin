package com.example.app_2100;

import java.util.*;

public class QueryBuilder {

    static int currentPosition = 0;

    public static List<List<String>> parse(String sentence) {
        List<List<String>> queries = new ArrayList<>();
        List<String> tokens = Tokeniser.tokenize(sentence);
        currentPosition = 0;
        parseExpr(tokens, queries);
        return queries;
    }

    private static void parseExpr(List<String> tokens, List<List<String>> queries) {
        List<String> query = new ArrayList<>();
        parseTerm(tokens, query);
        queries.add(query);
        while (currentPosition < tokens.size() && tokens.get(currentPosition).equalsIgnoreCase("or")) {
            query = new ArrayList<>();
            currentPosition++; // Move past "or"
            parseTerm(tokens, query);
            queries.add(query);
        }
    }

    private static void parseTerm(List<String> tokens, List<String> query) {
        parseFactor(tokens, query);
        while (currentPosition < tokens.size() && tokens.get(currentPosition).equalsIgnoreCase("and")) {
            currentPosition++; // Move past "and"
            parseFactor(tokens, query);
        }
    }

    private static void parseFactor(List<String> tokens, List<String> query) {
        String token = tokens.get(currentPosition);
        if (!token.equalsIgnoreCase("and") && !token.equalsIgnoreCase("or")) {
            query.add(token);
        }
        currentPosition++; // Move past word
    }
}
