
public class GetPeersNodesResponse {
	String type;
	byte[] id;
	NodeInfo[] nodes;
	
	public GetPeersNodesResponse(byte[] id) {
		type = "get_peers_nodes";
		this.id = id;
		nodes = new NodeInfo[Node.maxBucketSize];
	}
}
