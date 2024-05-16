package com.example.app_2100.search.parser;

//import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import com.example.app_2100.search.tokenizer.Tokenizer;

import org.junit.Test;

/**
 * Tests for Parser class
 *
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
public class ParserTest {
    private static Tokenizer tokenizer;

    @Test(timeout=1000)
    public void validTitle1() {
    	Tokenizer tokenizer = new Tokenizer("some titles");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        String title = parser.getTitle();
        assertEquals("some titles", title);
    }

    @Test(timeout=1000)
    public void validTitle2() {
        Tokenizer tokenizer = new Tokenizer("  ok");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        String title = parser.getTitle();
        assertEquals("ok", title);
    }

    @Test(timeout=1000)
    public void validTitle3() {
        Tokenizer tokenizer = new Tokenizer("  ok");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        String title = parser.getTitle();
        assertEquals("ok", title);
    }

    @Test(timeout=1000)
    public void validTitleAuthor1() {
        Tokenizer tokenizer = new Tokenizer("hello by @(peter)");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        String title = parser.getTitle();
        assertEquals("hello by", title);
    }

    @Test(timeout=1000)
    public void validTitleAuthor2() {
        Tokenizer tokenizer = new Tokenizer("hello by @(peter)");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        String author = parser.getAuthor();
        assertEquals("peter", author);
    }

    @Test(timeout=1000)
    public void validTitleAuthor3() {
        Tokenizer tokenizer = new Tokenizer("hello by @(peter ren)");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        String author = parser.getAuthor();
        assertEquals("peter ren", author);
    }

    @Test(expected = NullPointerException.class)
    public void invalidAuthor() {
        Tokenizer tokenizer = new Tokenizer("  ok");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        parser.getAuthor();
    }

    @Test(expected = Parser.IllegalProductionException.class)
    public void invalidAuthor2() {
        Tokenizer tokenizer = new Tokenizer("hello @(Peter");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        parser.getAuthor();
    }

    @Test(expected = Parser.IllegalProductionException.class)
    public void invalidAuthor3() {
        Tokenizer tokenizer = new Tokenizer("hello @Peter");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        parser.getAuthor();
    }

    @Test(expected = Parser.IllegalProductionException.class)
    public void invalidAuthor4() {
        Tokenizer tokenizer = new Tokenizer("hello @Peter)");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        parser.getAuthor();
    }

    @Test(expected = Parser.IllegalProductionException.class)
    public void invalidAuthor5() {
        Tokenizer tokenizer = new Tokenizer("@Peter)");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        parser.getAuthor();
    }

    @Test(expected = Parser.IllegalProductionException.class)
    public void invalidAuthor6() {
        Tokenizer tokenizer = new Tokenizer("@Peter)");
        // Print out the expression from the parser.
        Parser parser = new Parser(tokenizer);
        parser.getTitle();
    }


}
