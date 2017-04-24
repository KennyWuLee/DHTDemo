
public class FindNodeRequest extends Request {
	String type;
	byte[] id;
	byte[] targetId;
	
	public FindNodeRequest(byte[] id, byte[] targetId) {
		this.id = id;
		this.targetId = targetId;
		type = "find_node";
	}
}
