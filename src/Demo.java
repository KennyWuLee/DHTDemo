import java.util.HashMap;

public class Demo {
	public static void main(String[] args) {
		HashMap<String, Node> nodes = new HashMap<String, Node>();
		for(int i = 0; i < 100; i++) {
			String address = "address" + i;
			nodes.put(address, new Node(address, nodes));
		}
		for(int i = 1; i < 100; i++) {
			Node n = nodes.get("address" + i);
			nodes.get("address0").addNode(n.getNodeId(), n.getAddress());
		}
		Node n1 = nodes.get("address0");
		System.out.println("id: " + Node.arrayToBigIntUnsigned(n1.getNodeId()));
		n1.printBuckets();
		byte[] target = Node.randomId();
		System.out.println("target id: " + Node.arrayToBigIntUnsigned(target));
		for(NodeInfo i : n1.findNode(target)) {
			System.out.println(Node.arrayToBigIntUnsigned(i.id) + " " + i.address);
			System.out.println(Node.arrayToBigIntUnsigned(target).and(Node.arrayToBigIntUnsigned(i.id).abs()));
		}
	}
}
