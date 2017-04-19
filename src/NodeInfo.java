import java.net.InetAddress;

public class NodeInfo {

	public byte[] id;
	//public String address;
	public String ip;
	public int port;


	public NodeInfo(byte[] id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
	}
}
