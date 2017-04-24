
public class AnnouncePeerResponse extends Response {
	String type;
	byte[] id;
	
	public AnnouncePeerResponse(byte[] id) {
		type = "announce_peer";
		this.id = id;
	}
}
