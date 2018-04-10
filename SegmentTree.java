import java.util.*;

class SegmentTree {
    static class Interval {
        int start;
        int end;

        Interval(int start, int end) {
            this.start = start; 
            this.end = end;
        }

        boolean contains(Interval i) {
            return start <= i.start && end >= i.end;
        }
    }

    static class Node {
        Interval i;
        Set<Interval> canonicalSet = new HashSet<>();
        Node left;
        Node right;

        Node(Interval i) {
            this.i = i;
        }

        Node(Node left, Node right) {
            if (right == null) {
                i = new Interval(left.i.start, left.i.end);
            } else {
                if (right.i.start != left.i.end+1) {
                    System.out.println("Bad Inputs");
                }
                i = new Interval(left.i.start, right.i.end);
            }
            this.left = left;
            this.right = right;
        }

        void addInterval(Interval i) {
            canonicalSet.add(i);
        }
    }

    Node node;

    SegmentTree(Interval arr[]) {
        node = construct(arr);
        for (Interval i : arr) {
            insertInterval(node, i);
        }
    }

    void preOrder(Node node) {
        if (node == null) {
            return;
        }

        System.out.print(node.i.start + ":" + node.i.end + "--");
        for (Interval i : node.canonicalSet) {
            System.out.print(i.start + ":" + i.end + "-");
        }
        System.out.println();
        preOrder(node.left);
        preOrder(node.right);
    }

    void insertInterval(Node node, Interval i) {
        if (node == null) {
            return;
        }
        // this will be inserted in the highest region that can be consumed by it on left or right.
        if (i.contains(node.i)) {
            node.addInterval(i);
            return;
        }
        insertInterval(node.left, i);
        insertInterval(node.right, i);
    }

    Interval[] getElementaryIntervals(Interval arr[]) {
        List<Interval> list = new ArrayList<>();
        int x[] = new int[arr.length*2];

        for (int i = 0; i < arr.length; i++) {
            x[i*2] = arr[i].start;
            x[i*2+1] = arr[i].end;
        }

        Arrays.sort(x);

        int start = Integer.MIN_VALUE;
        int end = x[0];
        int i = 1;
        do {
            if (start+1 <= end-1) {
                list.add(new Interval(start+1, end-1));
            }
            list.add(new Interval(end, end));
            start = end;
            end = x[i++];
        } while (i < x.length);

        list.add(new Interval(start+1, end-1));
        list.add(new Interval(end, end));
        list.add(new Interval(end+1, Integer.MAX_VALUE));

        return list.toArray(new Interval[0]);
    }

    Interval[] query(int queryX) {
        Set<Interval> set = new HashSet<>();

        query(node, queryX, set);

        return set.toArray(new Interval[0]);
    }

    private void query(Node node, int queryX, Set<Interval> set) {
        if (node == null || (node.i.start > queryX || node.i.end < queryX)) {
            return;
        }

        set.addAll(node.canonicalSet);
        query(node.left, queryX, set);
        query(node.right, queryX, set);
    }

    Node construct(Interval arr[]) {
        Interval eleArr[] = getElementaryIntervals(arr);
        Deque<Node> currQueue = new LinkedList<>();
        Deque<Node> nextQueue = new LinkedList<>();

        for (Interval i : eleArr) {
            Node node = new Node(i);
        // start of these as leaf nodes and build trees level by level until root.
            currQueue.add(node);
        }

        // pretty much a level order traversal list built up, until root is built.
        // have 2 queues currQueue thats processed and nextQueue and then swap their refs, stop when nextQueue is singleton.
        while (currQueue.size() > 1) {
            while(currQueue.peek() != null) {
                Node node1 = currQueue.remove();
                Node node2 = null;
                if (currQueue.peek() != null) {
                    node2 = currQueue.remove();
                }
            
                // create a parent node with node1 and node2 as children.
                Node parent = new Node(node1, node2);

                nextQueue.add(parent);
            }
            Deque<Node> temp = currQueue;
            currQueue = nextQueue;
            nextQueue = temp;
        }

        return currQueue.remove();
    }

    public static void main(String args[]) {
        Interval arr[] = {new Interval(1,5), new Interval(6, 10), new Interval (12, 15)};
        SegmentTree tree = new SegmentTree(arr);

        Interval query[] = tree.query(13);
        for (Interval i : query) {
            System.out.println(i.start + ":" + i.end);
        }
    }
}
