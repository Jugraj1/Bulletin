package com.example.app_2100.search;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

import android.util.Log;

/**
 * This class builds upon codes from lab exercises and introduces additional tests for deletion.
 */
public class AVLTreeTest {
    /*
        For feedback, we have provided you with a visualisation of what your implementation 'should' look like.
        Do not modify any of the existing tests.

	    We advise you write additional tests to increase the confidence of your implementation. Simply getting these
	    tests correct does not mean your solution is robust enough pass the marking tests.

	    Lastly, check out:
	    https://www.cs.usfca.edu/~galles/visualization/AVLtree.html
	    for visualisation of AVL tree operations. Although please note that they allow for duplicates (they insert
	    duplicates to the right node) as opposed to not allowing duplicates.
     */

    /*
        You want to write your own tests but not sure how?
        Here is a very simple tutorial:

        1. Decide on what you want to test.
        2. Get an accurate representation of what your AVL Tree should look like. A good place to find such a
            representation is by using an AVL Tree visualiser such as the one by USFCA:
            https://www.cs.usfca.edu/~galles/visualization/AVLtree.html

        3. Write an additional JUnit4 test in this AVLTreeTest.java class. To do this, think of a name for your
            test (standard convention has Junit test names end in test) and then use the following code (change 'Void' to 'void'):
            @Test(timeout = 1000)
            public Void someTest() {
                // Your assertions go here
            }

        4. Create an instance of the AVLTree class and provide the inputs IN THE CORRECT ORDER. The order here
            matters as it will affect rotations. Try to input values in the same order that the representation in (2)
            does. To input values simply use the code:
            AVLTree<Integer> avl = new AVLTree<>(<root value>)
                .insert(<second value>)
                .insert(<third value>)
                .insert(<forth value>);
            you can continue this code to insert as many values as you want.

        The next steps will depend on what you want to test:

        5a. For height, balance factor, or value tests: Calculate what your expecting the height/balance/value to be
            according to your accurate representation in (2). Then use the code:
            assertEquals(<expected value>, avl.getBalanceFactor() or avl.getHeight() or avl.value);

        5b. For testing whether something is or is not null, use the code:
            assertNull(<input>); or assertNotNull(<input>);

        5c. For testing the overall structure of the code you may choose to use 'assertEquals' and provide a string.
            However, please know that if even a single character mismatches between expectation and actual, the test
            will fail.

            The Tree 'toString()' output is recursive and the 'toString()' method can be found in Tree.java.
            {value=<root node value>, leftNode={}, rightNode={}}
            Below is an example of such a test:
            assertEquals("{value=5, leftNode={}, rightNode={}}", avl.toString());
     */

    @Test(timeout = 1000)
    public void immutableTest1() {
        // Simple check if the implementation is immutable. relies on insert() implementation
        AVLTree<Integer> avl = new AVLTree<>(5);
        avl.insert(10);
        String expected = "{value=5, leftNode={}, rightNode={}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test(timeout = 1000)
    public void immutableTest2() {
        // more complex check if the implementation is immutable, relies on insert() implementation
        AVLTree<Integer> avl = new AVLTree<>(1);
        avl = avl.insert(15);
        avl = avl.insert(45);
        avl.insert(10);
        avl.insert(50);
        avl.insert(3);
        String expected = "{value=15, leftNode={value=1, leftNode={}, rightNode={}}, rightNode={value=45, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test(timeout = 1000)
    public void insertInOrderTest() {
        // Simply check if the insertion correctly places values (no rotation check).
        AVLTree<Integer> avl = new AVLTree<>(5);
        avl = avl.insert(10);
        String expected = "{value=5, leftNode={}, rightNode={value=10, leftNode={}, rightNode={}}}";
        assertNotNull(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl
                ,
                10, (int) avl.rightNode.value);

        avl = avl.insert(1);
        expected = "{value=5, leftNode={value=1, leftNode={}, rightNode={}}, rightNode={value=10, leftNode={}, rightNode={}}}";
        assertNotNull(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertEquals(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                1, (int) avl.leftNode.value);
    }

    @Test(timeout = 1000)
    public void insertDuplicateTest() {
        // As per the implementation requirements, duplicates should be ignored.
        AVLTree<Integer> avl = new AVLTree<>(5).insert(5);
        String expected = "{value=5, leftNode={}, rightNode={}}";
        assertEquals(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getHeight());

        // Double checking encase anyone changes height output.
        assertNull("\nInsertion does not properly handle duplicates" +
                "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl, avl.leftNode.value);

        assertNull("\nInsertion does not properly handle duplicates" +
                "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl, avl.rightNode.value);
    }

    @Test(timeout = 1000)
    public void leftRotateTest() {
        // constructing the tree manually to avoid using the insert method
        AVLTree<Integer> avlRightRightChild = new AVLTree<>(10);
        AVLTree<Integer> avlRightChild = new AVLTree<>(8,new AVLTree.EmptyAVL<>(),avlRightRightChild);
        AVLTree<Integer> avl = new AVLTree<>(5,new AVLTree.EmptyAVL<>(),avlRightChild).leftRotate();
        String expected = "{value=8, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=10, leftNode={}, rightNode={}}}";
        // Check root value
        assertNotNull(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertEquals(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                8, (int) avl.value);

        // Check left child value
        assertNotNull(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertEquals(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                5, (int) avl.leftNode.value);

        // Check right child value
        assertNotNull(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                10, (int) avl.rightNode.value);
    }

    @Test(timeout = 1000)
    public void rightRotateTest() {
        // constructing the tree manually to avoid using the insert method
        AVLTree<Integer> avlLeftLeftChild = new AVLTree<>(3);
        AVLTree<Integer> avlLeftChild = new AVLTree<>(6,avlLeftLeftChild,new AVLTree.EmptyAVL<>());
        AVLTree<Integer> avl = new AVLTree<>(10,avlLeftChild,new AVLTree.EmptyAVL<>()).rightRotate();
        String expected = "{value=6, leftNode={value=3, leftNode={}, rightNode={}}, rightNode={value=10, leftNode={}, rightNode={}}}";
        // Check root value
        assertNotNull(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertEquals(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                6, (int) avl.value);

        // Check left child value
        assertNotNull(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertEquals(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                3, (int) avl.leftNode.value);

        // Check right child value
        assertNotNull(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                10, (int) avl.rightNode.value);
    }

    // NOTE: Below test relies on insert() method
    @Test(timeout = 1000)
    public void balanceFactorTest() {
        // Ensure insertion results in balanced tree.
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        String expected = "{value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );

        avl = avl.insert(22).insert(21);
        expected = "{value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=21, leftNode={value=20, leftNode={}, rightNode={}}, rightNode={value=22, leftNode={}, rightNode={}}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left, right, left rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                -1, avl.getBalanceFactor()
        );

        avl = avl.insert(23);
        expected = "{value=21, leftNode={value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=20, leftNode={}, rightNode={}}}, rightNode={value=22, leftNode={}, rightNode={value=23, leftNode={}, rightNode={}}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left, right, left, left rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );

        //Advanced
        avl = new AVLTree<>(10)
                .insert(5)
                .insert(6)
                .insert(4)
                .insert(7)
                .insert(2)
                .insert(1)
                .insert(0)
                .insert(3);
        expected = "{value=6, leftNode={value=2, leftNode={value=1, leftNode={value=0, leftNode={}, rightNode={}}, rightNode={}}, rightNode={value=4, leftNode={value=3, leftNode={}, rightNode={}}, rightNode={value=5, leftNode={}, rightNode={}}}}, rightNode={value=10, leftNode={value=7, leftNode={}, rightNode={}}, rightNode={}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left, right, right, right, left, right rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                1, avl.getBalanceFactor()
        );
    }

    // NOTE: Below test relies on insert() method
    @Test(timeout = 1000)
    public void advancedRotationsInsertionTest() {
        // Cause a situation with a RR, RL, LL or LR rotation is required.
        AVLTree<Integer> avl = new AVLTree<>(14)
                .insert(17)
                .insert(11)
                .insert(7)
                .insert(53)
                .insert(4)
                .insert(13)
                .insert(12)
                .insert(8)
                .insert(60)
                .insert(19)
                .insert(16)
                .insert(20);

        String expected = "{value=14, leftNode={value=11, leftNode={value=7, leftNode={value=4, leftNode={}, rightNode={}}, rightNode={value=8, leftNode={}, rightNode={}}}, rightNode={value=12, leftNode={}, rightNode={value=13, leftNode={}, rightNode={}}}}, rightNode={value=19, leftNode={value=17, leftNode={value=16, leftNode={}, rightNode={}}, rightNode={}}, rightNode={value=53, leftNode={value=20, leftNode={}, rightNode={}}, rightNode={value=60, leftNode={}, rightNode={}}}}}";
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                14, (int) avl.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                11, (int) avl.leftNode.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                19, (int) avl.rightNode.value
        );

        // Another double rotation requiring test.
        avl = new AVLTree<>(40)
                .insert(20)
                .insert(10)
                .insert(25)
                .insert(30)
                .insert(22)
                .insert(50);

        expected = "{value=25, leftNode={value=20, leftNode={value=10, leftNode={}, rightNode={}}, rightNode={value=22, leftNode={}, rightNode={}}}, rightNode={value=40, leftNode={value=30, leftNode={}, rightNode={}}, rightNode={value=50, leftNode={}, rightNode={}}}}";
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                25, (int) avl.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                20, (int) avl.leftNode.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                40, (int) avl.rightNode.value
        );
    }

    /**
     * Test for the delete function
     * @author Jinzheng Ren (u7641234)
     */
    @Test(timeout = 1000)
    public void deleteTest() {
        // Test case 1
        AVLTree<Integer> avl1 = new AVLTree<>(20);
        avl1 = avl1.insert(10);
        avl1 = avl1.insert(30);
        avl1 = avl1.insert(5);
        avl1 = avl1.insert(15);

        String avl1String = "10\n" +
                            "\t├─5\n" +
                            "\t\t├─null\n" +
                            "\t\t├─null\n" +
                            "\t├─20\n" +
                            "\t\t├─15\n" +
                            "\t\t\t├─null\n" +
                            "\t\t\t├─null\n" +
                            "\t\t├─null";
        System.out.println("avl1 deletes 30");
        avl1 = (AVLTree<Integer>) avl1.delete(30);
        assertEquals(avl1String, avl1.display(1));
        assertEquals(-1, avl1.getBalanceFactor());
        System.out.println("avl1 finishes deleting 30");

        // Test case 2
        AVLTree<Integer> avl2 = new AVLTree<>(50);
        avl2 = avl2.insert(40);
        avl2 = avl2.insert(60);
        avl2 = avl2.insert(30);
        avl2 = avl2.insert(45);
        avl2 = avl2.insert(55);
        avl2 = avl2.insert(10);
        System.out.println("avl2 deletes 55");
        avl2 = (AVLTree<Integer>) avl2.delete(55);
        String avl2String ="40\n" +
                        "\t├─30\n" +
                        "\t\t├─10\n" +
                        "\t\t\t├─null\n" +
                        "\t\t\t├─null\n" +
                        "\t\t├─null\n" +
                        "\t├─50\n" +
                        "\t\t├─45\n" +
                        "\t\t\t├─null\n" +
                        "\t\t\t├─null\n" +
                        "\t\t├─60\n" +
                        "\t\t\t├─null\n" +
                        "\t\t\t├─null";
        assertEquals(avl2String, avl2.display(1));
        assertEquals(0, avl2.getBalanceFactor());
        System.out.println("avl2 finishes deleting 55");

        // Test case 3
        AVLTree<Integer> avl3 = new AVLTree<>(50);
        avl3 = avl3.insert(40);
        avl3 = avl3.insert(60);
        avl3 = avl3.insert(45);
        System.out.println("avl3 deletes 60");
        avl3 = (AVLTree<Integer>) avl3.delete(60);
        String avl3String = "45\n" +
                            "\t├─40\n" +
                            "\t\t├─null\n" +
                            "\t\t├─null\n" +
                            "\t├─50\n" +
                            "\t\t├─null\n" +
                            "\t\t├─null";
        assertEquals(avl3String, avl3.display(1));
        assertEquals(0, avl3.getBalanceFactor());
        System.out.println("avl3 finishes deleting 60");


        // Test empty
        System.out.println("Test empty");
        AVLTree<Integer> avl4 = new AVLTree<>(50);
        if (avl4.delete(50) instanceof EmptyTree) {
            EmptyTree<Integer> emptyTree = (EmptyTree<Integer>) avl4.delete(50);
            System.out.println("this is empty");
            System.out.println(emptyTree.display(1));
        } else {
            avl4 = (AVLTree<Integer>) avl4.delete(50);
            System.out.println("this is not empty");
            System.out.println(avl4.display(1));
        }

        // Test empty
        System.out.println("Test delete non-existing element");
        AVLTree<Integer> avl5 = new AVLTree<>(50);
        avl5 = (AVLTree<Integer>) avl5.delete(1);
        String avl5String = "50\n" +
                            "\t├─null\n" +
                            "\t├─null";
        assertEquals(avl5String, avl5.display(1));
    }
}
