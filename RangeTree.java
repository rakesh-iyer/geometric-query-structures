import java.util.*;

class RangeTree {
    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    SingleRangeTree tree;
    Map<SingleRangeTree.Node, SingleRangeTree> canonicalMap =  new HashMap<>();

    static class PointXComparator implements Comparator<Point> {
        public int compare(Point x, Point y) {
            return x.x - y.x;
        }
    }

    static class PointYComparator implements Comparator<Point> {
        public int compare(Point x, Point y) {
            return x.y - y.y;
        }
    }

    RangeTree(Point points[]) {
        tree = construct(points);
    }

    Point[] sortOnX(Point points[]) {
        Point[] x = Arrays.copyOf(points, points.length);
        Arrays.sort(x, new PointXComparator());

        return x;
    }

    Point[] sortOnY(Point points[]) {
        Point[] y = Arrays.copyOf(points, points.length);
        Arrays.sort(y, new PointYComparator());

        return y;
    }

    SingleRangeTree construct(Point points[]) {
        Point x[] = sortOnX(points);
        Point y[] = sortOnX(points);

        SingleRangeTree.KeyValue kv[] = new SingleRangeTree.KeyValue[x.length];
        for (int i = 0; i < x.length; i++) {
            kv[i] = new SingleRangeTree.KeyValue(x[i].x, x[i]);
        }

        SingleRangeTree tree = new SingleRangeTree(kv);

        // do a preorder and then construct canonical sets for all of them.
        constructCanonicalMap(tree.node, x, y);

        return tree;
    }

    Point [] getLeftHalf(Point arr[]) {
        return Arrays.copyOfRange(arr, 0, (arr.length+1)/2);
    }

    Point [] getRightHalf(Point arr[]) {
        return Arrays.copyOfRange(arr, (arr.length+1)/2, arr.length);
    }

    Point [] filter(Point arr[], Point filt[]) {
        Map<Point, Boolean> map = new HashMap<>();
        for (Point p : filt) {
            map.put(p, true);
        }

        List<Point> list = new ArrayList<>();
        for (Point p : arr) {
            if (map.get(p) == (Boolean)true) {
                list.add(p);
            }
        }

        return list.toArray(new Point[0]);
    }

    void constructCanonicalMap(SingleRangeTree.Node node, Point x[], Point y[]) {
        if (node == null) {
            return;
        }

        SingleRangeTree.KeyValue kv[] = new SingleRangeTree.KeyValue[y.length];
        for (int i = 0; i < y.length; i++) {
            kv[i] = new SingleRangeTree.KeyValue(y[i].y, y[i]);
        }
        SingleRangeTree tree = new SingleRangeTree(kv);
        canonicalMap.put(node, tree);

        Point xleft[] = getLeftHalf(x);
        Point yleft[] = filter(y, xleft);
 
        constructCanonicalMap(node.left, xleft, yleft);

        Point xright[] = getRightHalf(x);
        Point yright[] = filter(y, xright);

        constructCanonicalMap(node.right, xright, yright);
    }

    void preOrder(SingleRangeTree.Node node) {
        if (node == null) {
            return;
        }

        Point point = (Point)node.keyValue.value;
        System.out.println(node.keyValue.key + ":" + point.x + ":" + point.y);
        preOrder(node.left);
        preOrder(node.right);
    }

    boolean pointInRange(SingleRangeTree.Node node, int xstart, int xend, int ystart, int yend) {
        Point point = (Point)node.keyValue.value;

        return point.x >= xstart && point.x <= xend && point.y >= ystart && point.y <= yend;
    }

    List<SingleRangeTree.KeyValue> query(int xstart, int xend, int ystart, int yend) {
        // find split node for x.
        SingleRangeTree.Node node = tree.node;
        List<SingleRangeTree.KeyValue> list = new ArrayList<>();

        while (!(node.isLeaf() || (node.keyValue.key >= xstart && node.keyValue.key < xend))) {
            if (node.keyValue.key >= xstart) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        SingleRangeTree.Node split = node;
        if (split.isLeaf()) {
            if (pointInRange(split, xstart, xend, ystart, yend)) {
                list.add(split.keyValue);
            }
            return list;
        }

        SingleRangeTree.Node p = split.left;
        while (!p.isLeaf()) {
            if (p.keyValue.key >= xstart) {
                // do range search in the can map of the right subtree node.
                list.addAll(canonicalMap.get(p.right).query(ystart, yend));
                p = p.left;
            } else {
                p = p.right;
            }
        }

        if (pointInRange(p, xstart, xend, ystart, yend)) {
            list.add(p.keyValue);
        }

        p = split.right;
        while (!p.isLeaf()) {
            if (p.keyValue.key < xend) {
                // do range search in the can map of the left subtree node.
                list.addAll(canonicalMap.get(p.left).query(ystart, yend));
                p = p.right;
            } else {
                p = p.left;
            }
        }

        if (pointInRange(p, xstart, xend, ystart, yend)) {
            list.add(p.keyValue);
        }

        return list;
    }

    public static void main(String args[]) {
        Point points[] = {new Point(10, 15), new Point(1, 100), new Point(200, 3), new Point(500, 500), new Point(20, 40)};
        RangeTree tree = new RangeTree(points);

        List<SingleRangeTree.KeyValue> list = tree.query(1, 200, 40, 200);
        for (SingleRangeTree.KeyValue kv : list) {
            Point point = (Point)kv.value;
            System.out.println(point.x + ":" + point.y);
        }
    }
}
