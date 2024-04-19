import java.util.ArrayList;
import java.util.List;

public class Tokeniser {

    public static List<String> tokenize(String sentence) {
        List<String> tokens = new ArrayList<>();

        // Split the sentence into words
        String[] words = sentence.split("\\s+");

        // Loop through the words and identify 'and' or 'or'
        StringBuilder token = new StringBuilder();
        for (String word : words) {
            if (word.equalsIgnoreCase("and") || word.equalsIgnoreCase("or")) {
                // Add the current token to the list
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                // Add the 'and' or 'or' to the list as a separate token
                tokens.add(word);
            } else {
                // Append the word to the current token
                if (token.length() > 0) {
                    token.append(" ");
                }
                token.append(word);
            }
        }

        // Add the last token
        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }
}



