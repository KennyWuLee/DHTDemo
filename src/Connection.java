import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class Connection {
	
	private Node node;
	private Listener listen;
	private Gson gson;
	
	public Connection(String ip, int port) {
		node = new Node(ip, port);
		listen = new Listener(port, this);
		Thread t = new Thread(listen);
		t.start();
		gson = new Gson();
	}
	
	public Response makeRequest(String ip, int port, Request request) throws UnknownHostException, IOException {
		System.out.println("making " + request.getClass());
		Socket s = new Socket(ip, port);
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out.println(gson.toJson(request));
		String line = in.readLine();
		
		
		
		PingResponse res = gson.fromJson(line, PingResponse.class);
		s.close();
	}
	
	public void makePingRequest(String ip, int port) {
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
	
	public void makeFindNodeRequest(String ip, int port, byte[] target) {
		try {
			System.out.println("making find_node request");
			Socket s = new Socket(ip, port);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			FindNodeRequest req = createFindNodeRequest(target);
			out.println(gson.toJson(req));
			String line = in.readLine();
			FindNodeResponse res = gson.fromJson(line, FindNodeResponse.class);
			s.close();
		} catch (IOException e) {
			System.out.println("error sending find_node");
		}
	}
	
	public void makeGetPeersRequest(String ip, int port, byte[] info_hash) {
		try {
			System.out.println("making get_peers request");
			Socket s = new Socket(ip, port);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			GetPeersRequest req = createGetPeersRequest(info_hash);
			out.println(gson.toJson(req));
			String line = in.readLine();
			
			
			
			FindNodeResponse res = gson.fromJson(line, FindNodeResponse.class);
			s.close();
		} catch (IOException e) {
			System.out.println("error sending get_peers");
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
	
	public FindNodeRequest createFindNodeRequest(byte[] targetId) {
		FindNodeRequest r = new FindNodeRequest(node.getNodeId(), targetId);
		return r;
	}
	
	public FindNodeResponse createFindNodeResponse(byte[] targetId) {
		FindNodeResponse r = new FindNodeResponse(node.getNodeId());
		List<NodeInfo> nodes = node.findNode(targetId);
		for(int i = 0; i < nodes.size() && i < 8; i++) {
			r.nodes[i] = nodes.get(i);
		}
		return r;
	}
	
	public GetPeersRequest createGetPeersRequest(byte[] info_hash) {
		GetPeersRequest r = new GetPeersRequest(node.getNodeId(), info_hash);
		return r;
	}
	
	public static void main(String[] args) {
		int port = 5474;
		Connection con = new Connection("127.0.0.1", port);
		con.makePingRequest("127.0.0.1", port - 1);
		byte[] target = Node.randomId();
		con.makeFindNodeRequest("127.0.0.1", port - 1, target);
	}
}
