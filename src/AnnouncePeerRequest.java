
public class AnnouncePeerRequest extends Request {
	PeerInfo peer;
	
	public AnnouncePeerRequest(byte[] id, PeerInfo peer, int port) {
		super("announce_peer", id, port);
		this.peer = peer;
	}
}
