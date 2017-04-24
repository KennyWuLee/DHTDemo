
public class AnnouncePeerRequest extends Request {
	String type;
	byte[] id;
	PeerInfo peer;
	
	public AnnouncePeerRequest(byte[] id, PeerInfo peer) {
		type = "announce_peer";
		this.id = id;
		this.peer = peer;
	}
}
