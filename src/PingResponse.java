
public class PingResponse {
	String type;
	byte[] id;
	
	public PingResponse(byte[] id) {
		this.id = id;
		type = "ping";
	}
}
