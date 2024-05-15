package com.example.app_2100.search.parser;

import com.example.app_2100.search.parser.NameExp;
import com.example.app_2100.search.parser.SearchExp;
import com.example.app_2100.search.parser.TitleExp;
import com.example.app_2100.search.parser.AuthorExp;
import com.example.app_2100.search.parser.WordExp;
import com.example.app_2100.search.tokenizer.Token;
import com.example.app_2100.search.tokenizer.Tokenizer;
import java.util.Scanner;

/**
 * Note: You will need to have completed task 1 to complete this task.
 *
 * Welcome to task 2. In this task your job is to implement a simple parser.
 * It should be able to parser the following grammar rule:
 * <search>    ::=  <title> <author> | <title>
 * <title>   ::=  <word> <space> <title> | <space> <title> | <word>
 * <author> ::=  @ LBRA <name> RBRA
 * <name> ::= <word> <space> <name> | <word>
 * <word> ::= a <word> | b <word> | ... | z <word> | ... | a  | ... | z |
 *
 * Here are some rules you must abide by for this task:
 * 1. You may NOT modify any other classes in this task 2 package.
 * 2. You may create additional fields or methods to finish you implementation within this class.
 * <p>
 * Parsing, within the context of this lab, is the process of taking a bunch of tokens and
 * evaluating them. You will not need to 'evaluate' them within this class, instead, just
 * return an expression which can be evaluated.
 */
public class Parser {
    /**
     * The following exception should be thrown if the parse is faced with series of tokens that do not
     * correlate with any possible production rule.
     */
    public static class IllegalProductionException extends IllegalArgumentException {
        public IllegalProductionException(String errorMessage) {
            super(errorMessage);
        }
    }

    // The tokenizer (class field) this parser will use.
    Tokenizer tokenizer;

    /**
     * Parser class constructor
     * Simply sets the tokenizer field.
     * **** please do not modify this part ****
     */
    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * To help you both test and understand what this parser is doing, we have included a main method
     * which you can run. Once running, provide a mathematical string to the terminal and you will
     * receive back the result of your parsing.
     */
    public static void main(String[] args) {

        // Create a scanner to get the user's input.
        Scanner scanner = new Scanner(System.in);

        /*
         Continue to get the user's input until they exit.
         To exit press: Control + D or providing the string 'q'
         Example input you can try: ((1 + 2) * 5)/2
         Note that evaluations will round down to negative infinity (because they are integers).
         */
        System.out.println("Provide a search string to be parsed:");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            // Check if 'quit' is provided.
            if (input.equals("q"))
                break;

            // Create an instance of the tokenizer.
            Tokenizer tokenizer = new Tokenizer(input);

            // Print out the expression from the parser.
            Parser parser = new Parser(tokenizer);
            Exp expression = parser.parseSearchExp();
            System.out.println("Parsing: " + expression.show());
//            System.out.println("Evaluation: " + expression.evaluate());
        }
    }

    /**
     * Adheres to the grammar rule:
     * the first one restricts the search results of posts by that author
     * <search> ::=  <title> <author> | <title>
     *
     * @return type: SearchExp.
     */
    public SearchExp parseSearchExp() throws IllegalProductionException{
        TitleExp titleExp = parseTitleExp();

//        System.out.println(tokenizer.current().getToken());
        if (tokenizer.hasNext() && tokenizer.current().getType().equals(Token.Type.AT)) {
//            tokenizer.next();
            AuthorExp authorExp = parseAuthorExp();
            return new SearchExp(titleExp, authorExp);
        }
        return new SearchExp(titleExp);
    }
        /*
         TODO: Implement parse function for <exp>.
         TODO: Throw an IllegalProductionException if provided with tokens not conforming to the grammar.
         Hint 1: you know that the first item will always be a term (according to the grammar).
         Hint 2: the possible grammar return '<term> + <exp>' correlates with the class (SearchExp(term, exp)).
         */
        // ########## YOUR CODE STARTS HERE ##########



//        return null; // Change this return (if you want). It is simply a placeholder to prevent an error.
        // ########## YOUR CODE ENDS HERE ##########


//    public Exp parseExp2() {
//        /*
//         TODO: Implement parse function for <exp>.
//         TODO: Throw an IllegalProductionException if provided with tokens not conforming to the grammar.
//         Hint 1: you know that the first item will always be a term (according to the grammar).
//         Hint 2: the possible grammar return '<term> + <exp>' correlates with the class (SearchExp(term, exp)).
//         */
//        // ########## YOUR CODE STARTS HERE ##########
//        Exp term = parseTerm();
//        if (tokenizer.hasNext()) {
//            Exp exp;
//            if (tokenizer.current().getToken().equals("+")) {
//                tokenizer.next();
//                exp = parseExp2();
//                term =  new AddExp(term, exp);
//            } else if (tokenizer.current().getToken().equals("-")) {
//                tokenizer.next();
//                exp = parseExp2();
//                term = new SubExp(term, exp);
//            } else if (tokenizer.current().getType().equals(Token.Type.INT)) {
//                throw new IllegalProductionException("BBBB");
//            }
//        }
//        return term;
//        return null; // Change this return (if you want). It is simply a placeholder to prevent an error.
        // ########## YOUR CODE ENDS HERE ##########
//    }

    /**
     * Adheres to the grammar rule:
     * <title> ::= <word> space <title> | <word>
     *
     * @return type: TitleExp.
     */
    public TitleExp parseTitleExp() throws IllegalProductionException{
        /*
         TODO: Implement parse function for <term>.
         TODO: Throw an IllegalProductionException if provided with tokens not conforming to the grammar.
         Hint: you know that the first item will always be a factor (according to the grammar).
         */
        // ########## YOUR CODE STARTS HERE ##########
        WordExp wordExp = parseWordExp();
        if (tokenizer.hasNext()) {
            TitleExp restTitle;
            if (tokenizer.current().getType().equals(Token.Type.CHAR)) {
//                tokenizer.next();
                restTitle = parseTitleExp();
//                if (is it space) {
//                    tokenizer.next();
//                    if (is null?) {
//
//                    }
//                }
                return new TitleExp(wordExp, restTitle);
            }
        }
        return new TitleExp(wordExp);
//        return null; // Change this return (if you want). It is simply a placeholder to prevent an error.
        // ########## YOUR CODE ENDS HERE ##########
    }

    /**
     * Adheres to the grammar rule:
     * <author> ::=  @ LBRA <name> RBRA
     *
     * @return type: Exp.
     */
    public AuthorExp parseAuthorExp() throws IllegalProductionException {
        /*
         TODO: Implement parse function for <factor>.
         TODO: Throw an IllegalProductionException if provided with tokens not conforming to the grammar.
         Hint: you know that the first item will always be a coefficient (according to the grammar).
         */
        // ########## YOUR CODE STARTS HERE ##########
        if (tokenizer.current().getType().equals(Token.Type.AT)) {
            tokenizer.next();
            if (tokenizer.hasNext()) {
                if (tokenizer.current().getType().equals(Token.Type.LBRA)) {
                    System.out.println(tokenizer.current());
                    tokenizer.next();
                    NameExp nameExp = parseNameExp();
                    System.out.println(nameExp.show() + ": Name");
                    if (!tokenizer.hasNext()) {
                        throw new IllegalProductionException("parseAuthorExp()");
                    }
                    if (tokenizer.hasNext() && !(tokenizer.current().getType().equals(Token.Type.RBRA))) {
                        System.out.println("jj thompson");
                        throw new IllegalProductionException("parseAuthorExp()");
                    }
                    return new AuthorExp(nameExp);
                } else {
                    System.out.println("jj");
                    throw new IllegalProductionException("parseAuthorExp()");
                }
            } else {
                throw new IllegalProductionException("parseAuthorExp()");
            }
        } else {
            throw new IllegalProductionException("parseAuthorExp()");
        }


//        return null; // Change this return (if you want). It is simply a placeholder to prevent an error.
        // ########## YOUR CODE ENDS HERE ##########
    }

    /**
     * Adheres to the grammar rule:
     * <name> ::= <word> <space> <name> | <word>
     *
     * @return type: Exp.
     */
    public NameExp parseNameExp() throws IllegalProductionException{
        /*
         TODO: Implement parse function for <coefficient>.
         TODO: Throw an IllegalProductionException if provided with tokens not conforming to the grammar.
         Hint: you can use Integer.parseInt() to convert a string into an integer.
         Fun fact: Integer.parseInt() is using a parser!

         */
        WordExp wordExp = parseWordExp();
        if (tokenizer.hasNext() && tokenizer.current().getType().equals(Token.Type.CHAR)) {
//            tokenizer.next();
            NameExp nameExp = parseNameExp();
            if (tokenizer.hasNext() && tokenizer.current().getType().equals(Token.Type.SPACE)) {
                tokenizer.next();
            }
            System.out.println(tokenizer.current() + "@@@@@");
            return new NameExp(wordExp, nameExp);
        }
        return new NameExp(wordExp);

//        return ; // Change this return (if you want). It is simply a placeholder to prevent an error.
        // ########## YOUR CODE ENDS HERE ##########
    }


    /**
     * Adheres to the grammar rule:
     * <word> ::= <char> <word> | <char>
     * <word> ::= a <word> | b <word> | ... | z <word> | ... | a  | ... | z |
     * @return type: Exp.
     */
    public WordExp parseWordExp() throws IllegalProductionException {
        /*
         TODO: Implement parse function for <coefficient>.
         TODO: Throw an IllegalProductionException if provided with tokens not conforming to the grammar.
         Hint: you can use Integer.parseInt() to convert a string into an integer.
         Fun fact: Integer.parseInt() is using a parser!

         */
//        String word = tokenizer.current().getToken();
//        for (int i = 0; i < word.length(); i++) {
        String charExp = tokenizer.current().getToken();
        if (!(CharExp.isCharValid(charExp.charAt(0)))) {
            throw new IllegalProductionException("parseWordExp()");
        }
//        }
        WordExp wordExp = null;
        tokenizer.next();
        if (tokenizer.hasNext()) {
            if (tokenizer.current().getType().equals(Token.Type.CHAR)) {

                wordExp = parseWordExp();
                System.out.println(wordExp.show());
            }

            else if (tokenizer.current().getType().equals(Token.Type.SPACE)) {
//                wordExp = new WordExp(" ");
                tokenizer.next();
            }
        }
        return new WordExp(charExp, wordExp);
//        char charExp = word.charAt(0);
//        if (tokenizer.hasNext()) {
//            WordExp wordExp = parseWordExp();

//            var token = tokenizer.current().getToken();
//            wordExp = new WordExp(charExp, wordExp);
//            while (ch : ken[]) {
//                wordExp.append(...)
//            }

//            return new WordExp(charExp, wordExp);
//        }
//        return new WordExp(charExp);

//        return ; // Change this return (if you want). It is simply a placeholder to prevent an error.
        // ########## YOUR CODE ENDS HERE ##########
    }

    public String getTitle() {
        return parseSearchExp().getTitle();
    }

    public String getAuthor() {
        return parseSearchExp().getAuthor();
    }

}
