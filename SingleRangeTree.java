import java.util.*;

//only supports integer keys at this moment.
class SingleRangeTree {
    Node n;

    static class KeyValue {
        int key;
        Object value;

        KeyValue(int key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    static class Node {
        Node left;
        Node right;
        KeyValue keyValue;
        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    SingleRangeTree(KeyValue arr[]) {
        n = construct(arr, 0, arr.length-1);
    }

    Node construct(KeyValue arr[], int start, int end) {
        Node node = new Node();

        // input is a sorted array, create a balanced tree with each array element as leaf.
        int mid = (start + end)/2;

        node.keyValue = arr[mid];

        if (start != end) {
            node.left = construct(arr, start, mid);
            node.right = construct(arr, mid+1, end);
        }

        return node;
    }

    List<KeyValue> subtree(Node n) {
        List<KeyValue> l = new ArrayList<>();

        subtree(n, l);

        return l;
    }


    void subtree(Node n, List<KeyValue> l) {
        if (n == null) {
            return;
        }
        subtree(n.left, l);
        if (n.isLeaf()) {
            l.add(n.keyValue);
        }

        subtree(n.right, l);
    }

    List<KeyValue> query(int start, int end) {
        List<KeyValue> list = new ArrayList<>();

        // traverse both start and end until, they split.
        while (!(n.isLeaf() || (n.keyValue.key >= start && n.keyValue.key < end))) {
            if (n.keyValue.key >= start) {
                n = n.left;
            } else {
                n = n.right;
            }
        }

        if (n.isLeaf()) {
            if (n.keyValue.key >= start && n.keyValue.key <= end) {
                list.add(n.keyValue);
            }
            return list;
        }

        // n is the split node.
        Node p = n.left;

        // go trace to start and output right subtree whenever you gp left.
        while (!p.isLeaf()) {
            if (p.keyValue.key >= start) {
                list.addAll(subtree(p.right));
                p = p.left;
            } else {
                p = p.right;
            }
        }

        if (p.keyValue.key >= start && p.keyValue.key <= end) {
            list.add(p.keyValue);
        }

        p = n.right;

        while (!p.isLeaf()) {
            if (p.keyValue.key < end) {
                list.addAll(subtree(p.left));
                p = p.right;
            } else {
                p = p.left;
            }
        }

        if (p.keyValue.key >= start && p.keyValue.key <= end) {
            list.add(p.keyValue);
        }

        return list;
    }

    public static void main(String args[]) {
       KeyValue kv[] = {new KeyValue(1, null), new KeyValue(100, null), new KeyValue(101, null), new KeyValue(115, null),new KeyValue(120, null), new KeyValue(220, null), new KeyValue(300, null)};

        SingleRangeTree srTree = new SingleRangeTree(kv);

        List<KeyValue> l = srTree.query(100, 120);

        for (KeyValue keyValue : l) {
            System.out.println(keyValue.key);
        }
    }
}
