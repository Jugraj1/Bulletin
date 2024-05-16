//package com.example.app_2100.search;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Test;
//
//public class TokenizerTest {
//
//    /*
//        We suggest you test your tokenizer by also running it in your own terminal and checking the output.
//
//        We advise you write additional tests to increase the confidence of your implementation. Simply getting these
//	    tests correct does not mean your solution is robust enough pass the marking tests.
//     */
//
//    private static Tokenizer tokenizer;
//    private static final String MID = "12 * 5 - 3";
//    private static final String ADVANCED = "(100 + 2! - 40) / 10 * 4789";
//    private static final String ADD_AND_SUB = "+-";
//
//    @Test(timeout=1000)
//    public void testAddToken() {
//    	tokenizer = new Tokenizer(ADD_AND_SUB);
//
//    	// check the type of the first token
//        assertEquals("wrong token type", Token.Type.ADD, tokenizer.current().getType());
//
//        // check the actual token value"
//        assertEquals("wrong token value", "+", tokenizer.current().getToken());
//    }
//
//    @Test(timeout=1000)
//    public void testSubToken() {
//    	tokenizer = new Tokenizer(ADD_AND_SUB);
//
//        // extract next token (just to skip first passCase token)
//        tokenizer.next();
//
//        // check the type of the first token
//        assertEquals("wrong token type", Token.Type.SUB, tokenizer.current().getType());
//
//        // check the actual token value
//        assertEquals("wrong token value", "-", tokenizer.current().getToken());
//    }
//
//    @Test(timeout=1000)
//    public void testFirstToken(){
//    	tokenizer = new Tokenizer(ADVANCED);
//
//    	// check the type of the first token
//        assertEquals("wrong token type", Token.Type.LBRA, tokenizer.current().getType());
//        // check the actual token value
//        assertEquals("wrong token value", "(", tokenizer.current().getToken());
//    }
//
//    @Test(timeout=1000)
//    public void testMidTokenResult() {
//        tokenizer = new Tokenizer(MID);
//
//        // test first token INT(12)
//        assertEquals(new Token("12", Token.Type.INT), tokenizer.current());
//
//        // test second token *
//        tokenizer.next();
//        assertEquals(Token.Type.MUL, tokenizer.current().getType());
//
//        // test third token INT(5)
//        tokenizer.next();
//        assertEquals(new Token("5", Token.Type.INT), tokenizer.current());
//
//        // test forth token -
//        tokenizer.next();
//        assertEquals(Token.Type.SUB, tokenizer.current().getType());
//
//        // test fifth token INT(3)
//        tokenizer.next();
//        assertEquals(new Token("3", Token.Type.INT), tokenizer.current());
//    }
//
//    @Test(timeout=1000)
//    public void testAdvancedTokenResult(){
//    	tokenizer = new Tokenizer(ADVANCED);
//
//        // test first token (
//        assertEquals(Token.Type.LBRA, tokenizer.current().getType());
//
//        // test second token INT(100)
//        tokenizer.next();
//        assertEquals(new Token("100", Token.Type.INT), tokenizer.current());
//
//        // test third token +
//        tokenizer.next();
//        assertEquals(new Token("+", Token.Type.ADD), tokenizer.current());
//
//        // test fourth token INT(2)
//        tokenizer.next();
//        assertEquals(new Token("2", Token.Type.INT), tokenizer.current());
//
//        // test fifth token
//        tokenizer.next();
//        assertEquals(new Token("!", Token.Type.FAC), tokenizer.current());
//
//        // test sixth token -
//        tokenizer.next();
//        assertEquals(new Token("-", Token.Type.SUB), tokenizer.current());
//
//        // test seventh token INT(40)
//        tokenizer.next();
//        assertEquals(new Token("40", Token.Type.INT), tokenizer.current());
//
//        // test eighth token )
//        tokenizer.next();
//        assertEquals(new Token(")", Token.Type.RBRA), tokenizer.current());
//
//        // test ninth token /
//        tokenizer.next();
//        assertEquals(new Token("/", Token.Type.DIV), tokenizer.current());
//
//        // test tenth token INT(10)
//        tokenizer.next();
//        assertEquals(new Token("10", Token.Type.INT), tokenizer.current());
//
//        // test eleventh token *
//        tokenizer.next();
//        assertEquals(new Token("*", Token.Type.MUL), tokenizer.current());
//
//        // test token number twelve INT(4789)
//        tokenizer.next();
//        assertEquals(new Token("4789", Token.Type.INT), tokenizer.current());
//    }
//
//}
