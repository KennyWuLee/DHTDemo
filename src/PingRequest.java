
public class PingRequest extends Request {
	String type;
	byte[] id;
	
	public PingRequest(byte[] id) {
		this.id = id;
		type = "ping";
	}
}
