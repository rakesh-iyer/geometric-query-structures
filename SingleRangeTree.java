import java.util.*;

class SingleRangeTree {
    Node n;
    static class Node {
        Node left;
        Node right;
        Integer key;
        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    SingleRangeTree(int arr[]) {
        n = construct(arr, 0, arr.length-1);
    }

    Node construct(int arr[], int start, int end) {
        Node node = new Node();

        // input is a sorted array, create a balanced tree with each array element as leaf.
        int mid = (start + end)/2;

        node.key = arr[mid];

        if (start != end) {
            node.left = construct(arr, start, mid);
            node.right = construct(arr, mid+1, end);
        }
        
        return node; 
    }

    List<Integer> subtree(Node n) {
        List<Integer> l = new ArrayList<>();

        subtree(n, l);

        return l;
    }


    void subtree(Node n, List<Integer> l) {
        if (n == null) {
            return;
        }
        subtree(n.left, l);
        if (n.isLeaf()) {
            l.add(n.key);
        }

        subtree(n.right, l);
    }

    List<Integer> query(int start, int end) {
        List<Integer> list = new ArrayList<>();

        // traverse both start and end until, they split.
        while (!(n.isLeaf() || (n.key >= start && n.key < end))) {
            if (n.key >= start) {
                n = n.left;
            } else {
                n = n.right;
            }
        }

        if (n.isLeaf()) {
            if (n.key >= start && n.key <= end) {
                list.add(n.key);
            }
            return list;
        }

        // n is the split node.
        Node p = n.left; 

        // go trace to start and output right subtree whenever you gp left.
        while (!p.isLeaf()) {
            if (p.key >= start) {
                list.addAll(subtree(p.right));
                p = p.left;
            } else {
                p = p.right;
            }
        }

        if (p.key >= start && p.key <= end) {
            list.add(p.key);
        }

        p = n.right;

        while (!p.isLeaf()) {
            if (p.key < end) {
                list.addAll(subtree(p.left));
                p = p.right;
            } else {
                p = p.left;
            }
        }

        if (p.key >= start && p.key <= end) {
            list.add(p.key);
        }

        return list;
    }

    public static void main(String args[]) {
        int arr[] = {1, 100, 101, 115, 120, 220, 300};

        SingleRangeTree srTree = new SingleRangeTree(arr);

        List<Integer> l = srTree.query(100, 120);

        for (int i : l) {
            System.out.println(i);
        }    
    }
} 
