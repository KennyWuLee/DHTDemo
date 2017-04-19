import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.google.gson.Gson;

public class Connection {
	
	private Node node;
	private Listener listen;
	private Gson gson;
	
	public Connection(InetAddress ip, int port) {
		node = new Node(ip, port);
		listen = new Listener(port, this);
		Thread t = new Thread(listen);
		t.start();
		gson = new Gson();
	}
	
	public void makePingRequest(InetAddress ip, int port) {
		try {
			System.out.println("making ping request");
			Socket s = new Socket(ip, port);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PingRequest req = createPingRequest();
			out.println(gson.toJson(req));
			String line = in.readLine();
			PingResponse res = gson.fromJson(line, PingResponse.class);
			s.close();
		} catch (IOException e) {
			System.out.println("error sending ping");
		}
	}
	
	public PingRequest createPingRequest() {
		PingRequest r = new PingRequest(node.getNodeId());
		return r;
	
	}
	public PingResponse createPingResponse() {
		PingResponse r = new PingResponse(node.getNodeId());
		return r;
	}
	
	public static void main(String[] args) {
		try {
			int port = 5469;
			Connection con = new Connection(InetAddress.getByName("127.0.0.1"), port);
			con.makePingRequest(InetAddress.getByName("127.0.0.1"), port - 1);
		} catch (UnknownHostException e) {
			System.out.println("unknown host exception");
		}
	}
}
