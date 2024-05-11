package com.example.app_2100;

import java.util.*;

class Node {
    String value;
    List<Node> children;

    public Node(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }
}

