package cfg;

import java.util.Vector;

// a tree to record the inherit relationship between class.
// we have an empty "Object" class as the root.
public class InheritTree {
    public static class Node {
        public String className;
        public Vector<Node> children;

        public Node(String className, Vector<Node> children) {
            this.className = className;
            this.children = children;
        }
    }
    // end of Node

    public Vector<Node> nodes;
    public Node root;

    // constructor and methods
    public InheritTree() {
        this.nodes = new Vector<>();
        this.root = null;
    }

    public void addClass(String className) {
        Node n = new Node(className, new Vector<>());
        this.nodes.add(n);
        if (className.equals("Object")) {
            this.root = n;
        }
    }

    public Node searchClass(String className) {
        for (Node n : this.nodes) {
            if (n.className.equals(className)) {
                return n;
            }
        }
        return null;
    }

    public void addChild(String parent, String child) {
        Node p = searchClass(parent);
        Node c = searchClass(child);
        p.children.add(c);
    }
}

