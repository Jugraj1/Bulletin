package com.example.app_2100.search;

import java.util.ArrayList;
import java.util.List;

/**
 * This class builds upon codes from lab exercises and introduces additional functionalities such as the 'delete()' and 'searchByRange()' methods.
 * These new methods enhance the class by providing capabilities to remove elements and search for items within a specified range.
 */
public class AVLTree<T extends Comparable<T>> extends BinarySearchTree<T> {
    /*
        As a result of inheritance by using 'extends BinarySearchTree<T>,
        all class fields within BinarySearchTree are also present here.
        So while not explicitly written here, this class has:
            - value
            - leftNode
            - rightNode
     */

    public AVLTree(T value) {
        super(value);
        // Set left and right children to be of EmptyAVL as opposed to EmptyBST.
        this.leftNode = new EmptyAVL<>();
        this.rightNode = new EmptyAVL<>();
    }

    public AVLTree(T value, Tree<T> leftNode, Tree<T> rightNode) {
        super(value, leftNode, rightNode);
    }

    /**
     * @return balance factor of the current node.
     */
    public int getBalanceFactor() {
        /*
             Note:
             Calculating the balance factor and height each time they are needed is less efficient than
             simply storing the height and balance factor as fields within each tree node (as some
             implementations of the AVLTree do). However, although it is inefficient, it is easier to implement.
         */
        return leftNode.getHeight() - rightNode.getHeight();
    }

    /**
     * Insert element into the tree
     *
     * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
     * @param element element to be inserted
     * @return Tree after insertion
     */
    @Override
    public AVLTree<T> insert(T element) {
        // Ensure input is not null.
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");

        AVLTree<T> tree;
        if (element.compareTo(value) > 0) {
            tree = new AVLTree<>(value, leftNode, rightNode.insert(element));
        } else if (element.compareTo(value) < 0) {
            tree = new AVLTree<>(value, leftNode.insert(element), rightNode);
        } else {
            tree = new AVLTree<>(value, leftNode, rightNode);
        }
        if (Math.abs(tree.getBalanceFactor()) > 1) {
            tree = reStructure(tree);
        }
        return tree;
    }

    /**
     * Restructure the tree if the tree is imbalanced.
     *
     * @author Jinzheng Ren (u7641234) and Jugraj Singh (u7614074)
     * @param treeToBeChecked tree to be restructured
     * @return Tree after restructuring
     */
    public AVLTree<T> reStructure(Tree<T> treeToBeChecked) {

        if ( ((AVLTree<T> ) treeToBeChecked).getBalanceFactor() < - 1) {
            if ( ((AVLTree<T>) (treeToBeChecked.rightNode)).getBalanceFactor() <= - 1) {
                return ((AVLTree<T>) treeToBeChecked).leftRotate();
            } else if (((AVLTree<T>) (treeToBeChecked.rightNode)).getBalanceFactor() >= 1) {
                AVLTree<T> newTree = new AVLTree<T>(treeToBeChecked.value,
                        treeToBeChecked.leftNode,
                        ((AVLTree<T>) (treeToBeChecked.rightNode)).rightRotate());
                return newTree.leftRotate();
            } else {
                return ((AVLTree<T>) treeToBeChecked).leftRotate();
            }
        } else if (((AVLTree<T> ) treeToBeChecked).getBalanceFactor() > 1) {
            if ( ((AVLTree<T>) (treeToBeChecked.leftNode)).getBalanceFactor() >= 1) {
                return ((AVLTree<T>) treeToBeChecked).rightRotate();
            } else if (((AVLTree<T>) (treeToBeChecked.leftNode)).getBalanceFactor() <= -1) {
                AVLTree<T> newTree = new AVLTree<T>(treeToBeChecked.value,
                        ((AVLTree<T>) (treeToBeChecked.leftNode)).leftRotate(),
                        treeToBeChecked.rightNode);
                return newTree.rightRotate();
            } else {
                return ((AVLTree<T>) treeToBeChecked).rightRotate();
            }
        }

        return new AVLTree<>(treeToBeChecked.value, treeToBeChecked.leftNode, treeToBeChecked.rightNode);
    }


    /**
     * Delete an element from the tree.
     *
     * @author Jinzheng Ren (u7641234)
     * @param element Element to remove
     * @return Tree after deletion
     */
    public Tree<T> delete(T element) {
        // This return either an empty tree or a binary search tree.
        // Return the tree if the input is null.
        if (element == null) return this;
        Tree<T> tree = null;

        if (element.compareTo(value) > 0) {
            tree = new AVLTree<>(value, leftNode, rightNode.delete(element));
        } else if (element.compareTo(value) < 0) {
            tree = new AVLTree<>(value, leftNode.delete(element), rightNode);
        } else {
            // 1. Leaf node.
            if ((leftNode instanceof EmptyTree) && rightNode instanceof EmptyTree) {
                tree = new EmptyAVL<>();
            }
            // 2. Only one child.
            if (leftNode instanceof EmptyTree) {
                tree = rightNode;
            }
            // 2. Only one child.
            if (rightNode instanceof EmptyTree) {
                tree = leftNode;
            }

            // 3. Two children
            if (!(leftNode instanceof EmptyTree) && !(rightNode instanceof EmptyTree)) {
                tree = new AVLTree<>(rightNode.min(), leftNode, rightNode.delete(rightNode.min()));
            }
        }

        if (! (tree instanceof EmptyTree) ) {
            if (Math.abs((new AVLTree<>(tree.value, tree.leftNode, tree.rightNode)).getBalanceFactor()) > 1) {
                return reStructure(tree);
            }
        }

        return tree;
    }

    /**
     * Conducts a left rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public AVLTree<T> leftRotate() {
        rightNode.leftNode = new AVLTree<T>(value, this.leftNode, this.rightNode.leftNode);
        return (AVLTree<T>) rightNode;
// Change to return something different
    }

    /**
     * Conducts a right rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public AVLTree<T> rightRotate() {
        leftNode.rightNode = new AVLTree<T>(value, this.leftNode.rightNode, this.rightNode);
        return (AVLTree<T>) leftNode;
        // Change to return something different
    }

    /**
     * Note that this is not within a file of its own... WHY?
     * The answer is: this is just a design decision. 'insert' here will return something specific
     * to the parent class inheriting Tree from BinarySearchTree. In this case an AVL tree.
     */
    public static class EmptyAVL<T extends Comparable<T>> extends EmptyTree<T> {
        @Override
        public Tree<T> insert(T element) {
            // The creation of a new Tree, hence, return tree.
            return new AVLTree<T>(element);
        }

        @Override
        public Tree<T> delete(T element) {
            return new EmptyAVL<T>();
        }
    }

    /**
     * Return a series of elements that are within a given range
     *
     * @author Jinzheng Ren (u7641234)
     * @param start start range
     * @param end end range
     * @return A list of elements fell within the given range in the tree
     */
    public List<T> searchByRange(T start, T end) {
        if (start.compareTo(end) >= 0) {
            throw new IllegalArgumentException("!!! Start cannot be greater than end");
        }
        List<T> result = new ArrayList<>();
        searchByRangeHelper(this, start, end, result);
        return result;
    }

    private void searchByRangeHelper(Tree<T> tree, T start, T end, List<T> result) {
        if (tree instanceof EmptyTree) {
            return;
        }

        if (tree.value.compareTo(start) >= 0 && tree.value.compareTo(end) <= 0) {
            result.add(tree.value);
        }

        if (tree.value.compareTo(start) > 0) {
            searchByRangeHelper(tree.leftNode, start, end, result);
        }

        if (tree.value.compareTo(end) < 0) {
            searchByRangeHelper(tree.rightNode, start, end, result);
        }

    }
}
