
public class Demo {
	public static void main(String[] args) {
		Node n = new Node("original address");
		for(int i = 0; i < 100; i++) {
			Node temp = new Node("address" + i);
			n.addNode(temp.getNodeId(), temp.getAddress());
		}
		System.out.println("id: " + Node.arrayToBigIntUnsigned(n.getNodeId()));
		n.printBuckets();
	}
}
