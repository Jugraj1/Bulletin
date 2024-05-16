package com.example.app_2100.search;

import java.util.*;

/**
 * Methods that can help with tree data structures
 *
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
public class SearchUtils {
    /**
     * Get the total number of indices/ids from the tree.
     *
     * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
     * @param tree tree that store post indices/ids
     * @return total number of indices/ids from the tree
     */
    public static  <T extends Comparable<T>, K extends Comparable<K>> int getIndexSizeFromTreeRec(AVLTree<FieldIndex<T, K>> tree) {

        int size = 0;
        if (tree == null)
            return size;

        int leftSize = 0, rightSize = 0;
        // traverse the left child
        if (!(tree.leftNode instanceof AVLTree.EmptyAVL)) {
            leftSize = getIndexSizeFromTreeRec((AVLTree<FieldIndex<T, K>>) tree.leftNode);
        }
        if (!(tree.rightNode instanceof AVLTree.EmptyAVL)) {
            rightSize = getIndexSizeFromTreeRec((AVLTree<FieldIndex<T, K>>) tree.rightNode);
        }
        return leftSize + (size + 1) * tree.value.getIndices().size() + rightSize;

    }

    /**
     * Insert an FieldIndex to a tree, different from a normal object which involves merging.
     *
     * @author Jinzheng Ren (u7641234)
     * @param tree tree that store post indices/ids
     * @param a element to be inserted
     * @return tree after insertion
     */
    public static <T extends Comparable<T>, K extends Comparable<K>> AVLTree<FieldIndex<T, K>> insertFieldIndex(AVLTree<FieldIndex<T, K>> tree, FieldIndex<T, K> a) {
        if (tree.find(a) == null) {
            tree = tree.insert(a);
        } else {
            tree.find(a).value.merge(a);
        }
        return tree;
    }

    /**
     * Compute a 0 to 1 similarity value between two strings based on Levenshtein distance.
     *
     * @author Jinzheng Ren (u7641234)
     * @param query first string to be compared
     * @param ref second string to be compared
     * @return an integer that is the Levenshtein distance between two strings
     */
    public static double getTextsSimilarity(String query, String ref){
        int distance = levenshteinDistance(query, ref);
        double similarity = 1.0 - ((double) distance / Math.max(query.length(), ref.length()));
        return similarity;
    }

    /**
     * Returns a list of top n similar texts given a query text and a list of reference texts, this is for testing purpose
     *
     * @author Jinzheng Ren (u7641234)
     * @param query first string to be compared
     * @param refs a list of reference texts to be compared
     * @return a list of top n similar texts
     */
    public static List<String> findTopNSimilarTexts(String query, List<String> refs, int n) {
        List<SimilarText> similarTexts = new ArrayList<>();
        for (String ref : refs) {
            int distance = levenshteinDistance(query, ref);
            double similarity = 1.0 - ((double) distance / Math.max(query.length(), ref.length()));
            similarTexts.add(new SimilarText(ref, similarity));
        }

        // Sort by similarity
        Collections.sort(similarTexts, Comparator.reverseOrder());

        List<String> topN = new ArrayList<>();
        for (int i = 0; i < Math.min(n, similarTexts.size()); i++) {
            topN.add(similarTexts.get(i).text);
        }

        return topN;
    }

    /**
     * Compute Levenshtein distance between two strings
     *
     * @author Jinzheng Ren (u7641234)
     * @param str1 first string to be compared
     * @param str2 second string to be compared
     * @return an integer that is the Levenshtein distance between two strings
     */
    public static int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i][j - 1], dp[i - 1][j]));
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }

    /**
     * An object that store a text and a similarity value (with respect to another text)
     *
     * @author Jinzheng Ren (u7641234)
     */
    static class SimilarText implements Comparable<SimilarText> {
        String text;
        double similarity;

        public SimilarText(String text, double similarity) {
            this.text = text;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(SimilarText o) {
            return Double.compare(this.similarity, o.similarity);
        }
    }

}
