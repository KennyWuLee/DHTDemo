
public class FindNodeResponse {
	String type;
	byte[] id;
	NodeInfo[] nodes;
	
	public FindNodeResponse(byte[] id) {
		type = "find_node";
		this.id = id;
		nodes = new NodeInfo[Node.maxBucketSize];
	}
	
}