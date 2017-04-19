
public class FindNodeRequest {
	String type;
	byte[] id;
	byte[] targetId;
	
	public FindNodeRequest(byte[] id, byte[] targetId) {
		this.id = id;
		this.targetId = targetId;
		type = "find_node";
	}
}
