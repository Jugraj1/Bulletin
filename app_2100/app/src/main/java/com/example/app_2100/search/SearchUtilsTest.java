package com.example.app_2100.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Tests for searchUtils class
 *
 * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
 */
public class SearchUtilsTest {

    /**
     * Tests for searchByRange method in AVL Tree.
     *
     * @author Jinzheng Ren (u7641234)
     */
    @Test(timeout = 1000)
    public void searchTest() {
        AVLTree<Date> datesAVL = new AVLTree<>(new Date(2024 - 1900, Calendar.APRIL, 15)); // April 15, 2024
        datesAVL = datesAVL.insert(new Date(2024 - 1900, Calendar.APRIL, 20)); // April 20, 2024
        datesAVL = datesAVL.insert(new Date(2024 - 1900, Calendar.MAY, 1));  // May 1, 2024
        datesAVL = datesAVL.insert(new Date(2024 - 1900, Calendar.MAY, 10)); // May 10, 2024
        datesAVL = datesAVL.insert(new Date(2024 - 1900, Calendar.MAY, 15)); // May 15, 2024
        datesAVL = datesAVL.insert(new Date(2024 - 1900, Calendar.MAY, 25)); // May 25, 2024
        datesAVL = datesAVL.insert(new Date(2024- 1900, Calendar.JUNE, 5));  // June 5, 2024
        datesAVL = datesAVL.insert(new Date(2024 - 1900, Calendar.JUNE, 10)); // June 10, 2024

        // Test searching for dates within a range
        Date startDate = new Date(2024 - 1900, Calendar.MAY, 1);  // May 1, 2024
        Date endDate = new Date(2024 - 1900, Calendar.MAY, 20);   // May> 20, 2024

        List<Date> datesInRange = datesAVL.searchByRange(startDate, endDate);
        List<String> expectedDates = new ArrayList<>();
        expectedDates.add("Fri May 10 00:00:00 AEST 2024");
        expectedDates.add("Wed May 01 00:00:00 AEST 2024");
        expectedDates.add("Wed May 15 00:00:00 AEST 2024");

        System.out.println("Dates within the range:");
        for (int i = 0; i < datesInRange.size(); i ++) {
            assertEquals(expectedDates.get(i), datesInRange.get(i).toString());
        }

        AVLTree<Integer> avl2 = new AVLTree<>(50);
        avl2 = avl2.insert(40);
        avl2 = avl2.insert(60);
        avl2 = avl2.insert(30);
        avl2 = avl2.insert(45);
        avl2 = avl2.insert(55);
        avl2 = avl2.insert(10);

        int start = 43;
        int end = 56;
        List<Integer> list = avl2.searchByRange(start, end);
        List<Integer> expected = new ArrayList<>();
        expected.add(50);
        expected.add(45);
        expected.add(55);
        for (int i = 0; i < list.size(); i ++) {
            assertEquals(expected.get(i), list.get(i));
        }
    }

    /**
     * Tests for insertFieldIndex method in AVL Tree.
     *
     * @author Jinzheng Ren (u7641234)
     */
    @Test(timeout = 1000)
    public void insertFieldIndexTest() {
        FieldIndex<Date, Integer> d1 = new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.APRIL, 15), 0);
        AVLTree<FieldIndex<Date, Integer>> datesAVL = new AVLTree<>(d1);
        datesAVL = datesAVL.insert(new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.MARCH, 1), 2));
        datesAVL = datesAVL.insert(new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.JUNE, 1), 3));

        System.out.println("Test merge insert");
        datesAVL = SearchUtils.insertFieldIndex(datesAVL, new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.APRIL, 15), 1));
        String expectedTree = "Field{field=Mon Apr 15 00:00:00 AEST 2024, indices=[0, 1]}\n" +
                            "\t├─Field{field=Fri Mar 01 00:00:00 AEDT 2024, indices=[2]}\n" +
                            "\t\t├─null\n" +
                            "\t\t├─null\n" +
                            "\t├─Field{field=Sat Jun 01 00:00:00 AEST 2024, indices=[3]}\n" +
                            "\t\t├─null\n" +
                            "\t\t├─null";

        assertEquals(expectedTree, datesAVL.display(1));

        System.out.println("Test new insert");
        datesAVL = SearchUtils.insertFieldIndex(datesAVL, new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.SEPTEMBER, 30), 3));
        String expectedTree2 = "Field{field=Mon Apr 15 00:00:00 AEST 2024, indices=[0, 1]}\n" +
                                "\t├─Field{field=Fri Mar 01 00:00:00 AEDT 2024, indices=[2]}\n" +
                                "\t\t├─null\n" +
                                "\t\t├─null\n" +
                                "\t├─Field{field=Sat Jun 01 00:00:00 AEST 2024, indices=[3]}\n" +
                                "\t\t├─null\n" +
                                "\t\t├─Field{field=Mon Sep 30 00:00:00 AEST 2024, indices=[3]}\n" +
                                "\t\t\t├─null\n" +
                                "\t\t\t├─null";
        assertEquals(expectedTree2, datesAVL.display(1));
        assertEquals(-1, datesAVL.getBalanceFactor());
    }

    /**
     * Tests for getTextsSimilarity, findTopNSimilarTexts methods in AVL Tree.
     *
     * @author Jinzheng Ren (u7641234)
     */
    @Test(timeout = 1000)
    public void findSimilarTextsTest() {
        // Sample list of texts
        List<String> texts = new ArrayList<>();
        texts.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        texts.add("Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        texts.add("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.");
        // Query text
        String query = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor";

        // Calculate similarity using Levenshtein distance
        List<SearchUtils.SimilarText> similarTexts = new ArrayList<>();
        for (String text : texts) {
            int distance = SearchUtils.levenshteinDistance(query, text);
            double similarity = 1.0 - ((double) distance / Math.max(query.length(), text.length()));
            similarTexts.add(new SearchUtils.SimilarText(text, similarity));
        }
        // Sort by similarity
        Collections.sort(similarTexts, Comparator.reverseOrder());
        // Display top N similar texts
        int n = 3; // Change this to get different top N results
        for (int i = 0; i < Math.min(n, similarTexts.size()); i++) {
            System.out.println("Similarity: " + similarTexts.get(i).similarity + ", Text: " + similarTexts.get(i).text);
        }
        // Simple test
        List<String> result = SearchUtils.findTopNSimilarTexts(query, texts, 4);
        String expectedResult = "[Lorem ipsum dolor sit amet, consectetur adipiscing elit., Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat., Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.]";
        assertEquals(expectedResult, result.toString());
    }

    /**
     * Tests for getIndexSizeFromTreeRec method in AVL Tree.
     *
     * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
     */
    @Test(timeout = 1000)
    public void getIndexSizeFromTreeRecTest() {
        FieldIndex<Date, Integer> d1 = new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.APRIL, 15), 0);
        AVLTree<FieldIndex<Date, Integer>> datesAVL = new AVLTree<>(d1);
        datesAVL = datesAVL.insert(new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.MARCH, 1), 2));
        datesAVL = datesAVL.insert(new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.JUNE, 1), 3));
        datesAVL = SearchUtils.insertFieldIndex(datesAVL, new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.APRIL, 15), 1));
        datesAVL = SearchUtils.insertFieldIndex(datesAVL, new FieldIndex<Date, Integer>(new Date(2024 - 1900, Calendar.SEPTEMBER, 30), 3));

        assertEquals(5, SearchUtils.getIndexSizeFromTreeRec(datesAVL));
    }
}
