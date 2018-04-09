import java.util.*;

class IntervalTree {
	static class Interval {
		int start;
		int end;

		Interval(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	static class Node {
		Interval [] iMid;
		int xMid;
		Node left;
		Node right;
	}

	Node node;

	IntervalTree(Interval arr[]) {
		node = construct(arr);
	}

	int getMedianX(Interval arr[]) {
		int x[] = new int[arr.length*2];
		int j = 0;
		for (Interval i : arr) {
			x[j++] = i.start;
			x[j++] = i.end;
		}

		Arrays.sort(x);
		return x[arr.length-1];
	}


	// create a ds that can efficiently find the intervals containing a point.
	Node construct(Interval arr[]) {
		// find the median of the end points for interval.
		// split the intervals into Ileft, Imid and Iright.
		// store Imid with the node.
		if (arr.length == 0) {
			return null;
		}

		int xMid = getMedianX(arr);

		List<Interval> iLeft = new ArrayList<>();
		List<Interval> iRight = new ArrayList<>();
		List<Interval> iMid = new ArrayList<>();

		for (Interval i : arr) {
			if (i.end < xMid) {
				iLeft.add(i);
			} else if (i.start > xMid) {
				iRight.add(i);
			} else {
				iMid.add(i);
			}
		}

		Node node = new Node();
		node.iMid = iMid.toArray(new Interval[0]);
		node.xMid = xMid;
		node.left = construct(iLeft.toArray(new Interval[0]));
		node.right = construct(iRight.toArray(new Interval[0]));

		return node;
	}

	List<Interval> filterLeft(Interval arr[], int x) {
		List<Interval> filterList = new ArrayList<>();
		for (Interval i : arr) {
			if (i.start <= x) {
				filterList.add(i);
			}
		}

		return filterList;
	}

	List<Interval> filterRight(Interval arr[], int x) {
		List<Interval> filterList = new ArrayList<>();
		for (Interval i : arr) {
			if (i.end >= x) {
				filterList.add(i);
			}
		}

		return filterList;
	}

	Interval[] query(int x) {
		List<Interval> list = new ArrayList<>();
		Node node = this.node;

		// given a x coordinate find all intervals containing it.
		while (node != null) {
			if (x <= node.xMid) {
				list.addAll(filterLeft(node.iMid, x));
				node = node.left;
			} else {
				list.addAll(filterRight(node.iMid, x));
				node = node.right;
			}
		}

		return list.toArray(new Interval[0]);
	}

	public static void main(String args[]) {
		Interval arr[] = {new Interval(1, 5), new Interval(10, 15), new Interval(20, 25)};
		IntervalTree tree = new IntervalTree(arr);

		Interval iArr[] = tree.query(12);
		for (Interval i : iArr) {
			System.out.println("(" + i.start + "," + i.end + ")");
		}
	}
}
