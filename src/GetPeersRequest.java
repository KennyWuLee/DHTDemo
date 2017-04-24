
public class GetPeersRequest extends Request {
	String type;
	byte[] id;
	byte[] info_hash;
	
	public GetPeersRequest(byte[] id, byte[] info_hash) {
		this.type = "get_peers";
		this.id = id;
		this.info_hash = info_hash;
	}
}
