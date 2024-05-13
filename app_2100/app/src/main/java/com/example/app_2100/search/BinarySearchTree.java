package com.example.app_2100.search;

/**
 * An AVL tree is actually an extension of a Binary Search Tree
 * with self balancing properties. Hence, our AVL trees will 'extend'
 * this Binary Search tree data structure.
 */
public class BinarySearchTree<T extends Comparable<T>> extends Tree<T> {

    public BinarySearchTree(T value) {
        super(value);
        this.leftNode = new EmptyBST<>();
        this.rightNode = new EmptyBST<>();
    }

    public BinarySearchTree(T value, Tree<T> leftNode, Tree<T> rightNode) {
        super(value, leftNode, rightNode);
    }

    public BinarySearchTree() {
        super();
    }

    @Override
    public T min() {
        return (leftNode instanceof EmptyTree) ? value : leftNode.min();
    }

    @Override
    public T max() {
        return (rightNode instanceof EmptyTree) ? value : rightNode.max();
    }

    @Override
    public Tree<T> find(T element) {
        /*
            Left is less, right is greater in this implementation.
            compareTo returns 0 if both elements are equal.
            compareTo returns < 0 if the element is less than the node.
            compareTo returns > 0 if the element is greater than the node.
         */

        // Ensure input is not null.
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");

        if (element.compareTo(value) == 0) {
            return this;
        } else if (element.compareTo(value) < 0) {
            return leftNode.find(element);
        } else {
            return rightNode.find(element);
        }
    }

    @Override
    public BinarySearchTree<T> insert(T element) {
        // Ensure input is not null.
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");

        // If the two values are equal, in this implementation we want to insert to the left.
        if (element.compareTo(value) > 0) {
            return new BinarySearchTree<>(value, leftNode, rightNode.insert(element));
        } else {
            return new BinarySearchTree<>(value, leftNode.insert(element), rightNode);
        }
    }

    @Override
    public Tree<T> delete(T element) {
        // This return either an empty tree or a binary search tree.
        // Return the tree if the input is null.
        if (element == null) return this;

        if (element.compareTo(value) > 0) {
            return new BinarySearchTree<>(value, leftNode, rightNode.delete(element));
        } else if (element.compareTo(value) < 0) {
            return new BinarySearchTree<>(value, leftNode.delete(element), rightNode);
        } else {
            // 1. Leaf node.
            if ((leftNode instanceof EmptyTree) && rightNode instanceof EmptyTree) {
                return new EmptyBST<T>();
            }
            // 2. Only one child.
            if (leftNode instanceof EmptyTree) {
                return rightNode;
            }
            if (rightNode instanceof EmptyTree) {
                return leftNode;
            }
            // 3. Two children
            return new BinarySearchTree<>(rightNode.min(), leftNode, rightNode.delete(rightNode.min()));
        }

    }

    /**
     * Note that this is not within a file of its own... WHY?
     * The answer is: this is just a design decision. 'insert' here will return something specific
     * to the parent class inheriting Tree. In this case a BinarySearchTree.
     */
    public static class EmptyBST<T extends Comparable<T>> extends EmptyTree<T> {
        @Override
        public Tree<T> insert(T element) {
            // The creation of a new Tree, hence, return tree.
            return new BinarySearchTree<T>(element);
        }

        @Override
        public Tree<T> delete(T element) {
            return new EmptyBST<T>();
        }
    }
}
