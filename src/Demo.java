import java.util.Random;

public class Demo {
	public static void main(String[] args) {
		nodeTest();
		
		
//		HashMap<String, Connection> connections = new HashMap<>();
//		for(int i = 0; i < 10; i++) {
//			PeerInfo p = new PeerInfo();
//			p.address = "address" + i;
//			p.port = 22;
//			Connection c = new Connection("127.0.0.1", 5000 + i, p);
//			connections.put("node" + i, c);
//		}
//
//		for(int i = 1; i < 10; i++) {
//			Connection c = connections.get("node" + 1);
//			c.ping("127.0.0.1", 5000 + i);
//		}
//	
////		for(int i = 1; i < 10; i++) {
////			Connection c = connections.get("node" + 1);
////			c.bootstrap();
////		}
//		
//		connections.get("node1").bootstrap();
//		connections.get("node1").info();
	}
	
	public static void nodeTest() {
		Node n = new Node("127.0.0.1", 5000);
		NodeInfo[] inserts = new NodeInfo[10];
		for(int i = 0; i < 10; i++) {
			inserts[i] =  new NodeInfo(Node.randomId(), "127.0.0.1", 5001 + i);
		}
		Random rand = new Random();
		for(int i = 0; i < 100; i++) {
			n.addNode(inserts[rand.nextInt(10)]);
		}
		n.printBuckets();
		
	}
	
}
