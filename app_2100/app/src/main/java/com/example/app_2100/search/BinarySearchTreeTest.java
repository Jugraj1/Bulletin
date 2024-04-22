import org.junit.Test;

import static org.junit.Assert.*;

public class BinarySearchTreeTest {
    @Test(timeout = 1000)
    public void deleteTest1() {
        // more complex check if the implementation is immutable, relies on insert() implementation
        BinarySearchTree<Integer> bst = new BinarySearchTree<>(50);
        bst = bst.insert(30);
        bst = bst.insert(70);
        bst = bst.insert(20);
        bst = bst.insert(40);
        bst = bst.insert(60);
        bst = bst.insert(80);
        // System.out.printf(bst.leftNode.toString());
        System.out.printf(bst.display(1));

        System.out.printf("\n");
        bst = (BinarySearchTree<Integer>) bst.delete(50);
        System.out.printf(bst.display(1));

        // https://www.geeksforgeeks.org/deletion-in-binary-search-tree/ (above tree follows from the above link)

        System.out.printf("\n");
        BinarySearchTree<Integer> bst2 = new BinarySearchTree<>(50);
        if (bst2.delete(50) instanceof EmptyTree<Integer>) {
            EmptyTree<Integer> emptyTree = (EmptyTree<Integer>) bst2.delete(50);
            System.out.println("this is empty");
            System.out.println(emptyTree.display(1));
        } else {
            bst2 = ( BinarySearchTree<Integer>) bst2.delete(50);
            System.out.println("this is not empty");
            System.out.println(bst2.display(1));
        }

    }
}
