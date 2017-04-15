import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

public class Node {
	
	final byte[] max = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	final byte[] zero = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	private final int maxBucketSize = 8;
	private byte[] nodeId;
	private NodeInfo nodeInfo;
	private String address;
	private ArrayList<Bucket> buckets;
	private HashMap<String, Node> otherNodes;
	
	public Node(String address, HashMap<String, Node> otherNodes) {
		this.otherNodes = otherNodes;
		this.address = address;
		this.nodeId = randomId();
		buckets = new ArrayList<Bucket>();
		Bucket b = new Bucket(arrayToBigIntUnsigned(zero), arrayToBigIntUnsigned(max));
		nodeInfo = new NodeInfo(this.nodeId, address);
		b.nodes.add(this.nodeInfo);
		buckets.add(b);
	}
	
	public static byte[] randomId() {
		Random rand = new Random();
		byte[] temp = new byte[20];
		for (int i = 0; i < 20; i++) {
			temp[i] = (byte) rand.nextInt(256);
		}
		return temp;
	}
	
	public static BigInteger arrayToBigIntUnsigned(byte[] num) {
		byte[] temp = new byte[num.length + 1]; 
		temp[0] = 0;
		for (int i = 0; i < num.length; i++) {
			temp[i+1] = num[i];
		}
		return new BigInteger(temp);
	}
	
	public void addNode(byte[] id, String address) {
		BigInteger idValue = arrayToBigIntUnsigned(id);
		int bucketIndex =  findBucket(idValue, 0, buckets.size());
		Bucket b = buckets.get(bucketIndex);
		if(b.size() < maxBucketSize) {
			b.nodes.add(new NodeInfo(id, address));
		} else {
			if(b.nodes.contains(this.nodeInfo)) {
				BigInteger middle = b.start.add(b.end).divide(BigInteger.valueOf(2));
				Bucket b2 = new Bucket(middle.add(BigInteger.ONE), b.end);
				b.end = middle;
				for (int i = b.size() - 1; i >= 0; i--) {
					if (arrayToBigIntUnsigned(b.nodes.get(i).id).compareTo(middle) > 0) {
						b2.nodes.add(b.nodes.remove(i));
					}
				}
				buckets.add(bucketIndex + 1, b2);
				addNode(id, address);
			}
		}
	}
	
	public byte[] getNodeId() {
		return this.nodeId;
	}
	
	public String getAddress() {
		return this.address;
	}

	//binary search 
	//start inclusive, end exclusive
	private int findBucket(BigInteger id, int startIndex, int endIndex) {
		int middleIndex = (startIndex + endIndex) / 2;
		Bucket b = buckets.get(middleIndex);
		if (id.compareTo(b.start) < 0) {
			return findBucket(id, startIndex, middleIndex);
		}
		if (id.compareTo(b.end) > 0) {
			return findBucket(id, middleIndex + 1, endIndex);
		}
		return middleIndex;
	}
	
	public void printBuckets() {
		for(int i = 0; i < buckets.size(); i++) {
			Bucket b = buckets.get(i);
			System.out.println("bucket" + i + "(" + b.start + ":" + b.end + ")");
		}
	}
	
	public LinkedList<NodeInfo> findNode(byte[] findId) {
		NodeInfoComparator comp = new NodeInfoComparator(arrayToBigIntUnsigned(findId));
		PriorityQueue<NodeInfo> closestNodes = new PriorityQueue<NodeInfo>(10, comp);
		
		for (Bucket b : buckets) {
			for (NodeInfo i : b.nodes) {
				closestNodes.add(i);
			}
		}
	
		LinkedList<NodeInfo> results = new LinkedList<>();
		for(int i = 0; i < 8 && ! closestNodes.isEmpty(); i++) {
			results.add(closestNodes.poll());
		}
		return results;
	}
}
