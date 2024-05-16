package com.example.app_2100.search.tokenizer;

import com.example.app_2100.search.parser.CharExp;

import java.util.Scanner;

/**
 * This class builds upon codes from lab exercises however overall content has been modified to fit our own requirement.
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
public class Tokenizer {
    private String buffer;          // String to be transformed into tokens each time next() is called.
    private Token currentToken;     // The current token. The next token is extracted when next() is called.
    private int pos = 0;


    /**
     * To help you both test and understand what this tokenizer is doing, we have included a main method
     * which you can run. Once running, provide a mathematical string to the terminal and you will
     * receive back the result of your tokenization.
     */
    public static void main(String[] args) {
        // Create a scanner to get the user's input.
        Scanner scanner = new Scanner(System.in);

        /*
         Continue to get the user's input until they exit.
         To exit press: Control + D or providing the string 'q'
         Example input you can try: ((1 + 2) * 5)/2
         */
        System.out.println("Provide a title to be tokenized:");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            // Check if 'quit' is provided.
            if (input.equals("q"))
                break;

            // Create an instance of the tokenizer.
            Tokenizer tokenizer = new Tokenizer(input);

            // Print all the tokens.
            while (tokenizer.hasNext()) {
                System.out.print(tokenizer.current() + " ");
                tokenizer.next();
            }
            System.out.println();
        }
    }

    /**
     * Tokenizer class constructor
     * The constructor extracts the first token and save it to currentToken
     * **** please do not modify this part ****
     */
    public Tokenizer(String text) {
        buffer = removeRedundantSpaces(text);          // save input text (string)
        next();                 // extracts the first token.
    }

    /**
     * This function will find and extract a next token from {@code _buffer} and
     * save the token to {@code currentToken}.
     */
    public void next() {
        if (buffer.isEmpty()) {
            currentToken = null;    // if there's no string left, set currentToken null and return
            return;
        }
        char firstChar = buffer.charAt(0);
        if (firstChar == '@')
            currentToken = new Token("@", Token.Type.AT);
        else if (firstChar == ' ') {
            currentToken = new Token(" ", Token.Type.SPACE);
        }
        else if (firstChar == '(')
            currentToken = new Token("(", Token.Type.LBRA);
        else if (firstChar == ')')
            currentToken = new Token(")", Token.Type.RBRA);
        else if (CharExp.isCharValid(firstChar)) {
            currentToken = new Token(String.valueOf(firstChar), Token.Type.CHAR);
        }
        else {
            throw new Token.IllegalTokenException("Invalid input:" + firstChar);
        }



        // ########## YOUR CODE ENDS HERE ##########
        // Remove the extracted token from buffer
        int tokenLen = currentToken.getToken().length();
        buffer = buffer.substring(tokenLen);
    }

    /**
     * Returns the current token extracted by {@code next()}
     * **** please do not modify this part ****
     *
     * @return type: Token
     */
    public Token current() {
        return currentToken;
    }

    /**
     * Check whether tokenizer still has tokens left
     * **** please do not modify this part ****
     *
     * @return type: boolean
     */
    public boolean hasNext() {
        return currentToken != null;
    }


    public static String removeRedundantSpaces(String input) {

        // Replace multiple spaces with a single space
        input = input.replaceAll("\\s+", " ");
        if (input.charAt(0) == ' ') {
            input = input.substring(1);
        }
        if (input.charAt(input.length() - 1) == ' ') {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }
}
