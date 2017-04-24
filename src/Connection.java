import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Connection {
	
	private Node node;
	private Listener listen;
	private Gson gson;
	private PeerInfo peerInfo;
	
	public Connection(String ip, int port, PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
		node = new Node(ip, port);
		listen = new Listener(port, this);
		Thread t = new Thread(listen);
		t.start();
		gson = new Gson();
	}
	
	public Response makeRequest(String ip, int port, Request request) throws IOException, InvalidResponseException {
		System.out.println("making " + request.getClass());
		Socket s = new Socket(ip, port);
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		//send request
		out.println(gson.toJson(request));
		//receive response
		String line = in.readLine();
		s.close();
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(line).getAsJsonObject();
		if (! obj.has("type")) {
			throw new InvalidResponseException();
		}
		Response r = null;
		switch (obj.get("type").getAsString()) {
		case "ping":
			r = gson.fromJson(obj, PingResponse.class);
			break;
		case "find_node":
			r = gson.fromJson(obj, FindNodeResponse.class);
			break;
		case "get_peers_nodes":
			r = gson.fromJson(obj, GetPeersNodesResponse.class);
			break;
		case "get_peers_peers":
			r = gson.fromJson(obj, GetPeersPeersResponse.class);
			break;
		case "announce_peer":
			r = gson.fromJson(obj, AnnouncePeerResponse.class);
			break;
		default:
			throw new InvalidResponseException();
		}
		return r;
	}
	
	public PingRequest createPingRequest() {
		PingRequest r = new PingRequest(node.getNodeId());
		return r;
	}
	
	public PingResponse createPingResponse(PingRequest request, String ip, int port) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, port);
		node.addNode(nodeInfo);
		PingResponse r = new PingResponse(node.getNodeId());
		return r;
	}
	
	public FindNodeRequest createFindNodeRequest(byte[] targetId) {
		FindNodeRequest r = new FindNodeRequest(node.getNodeId(), targetId);
		return r;
	}
	
	public FindNodeResponse createFindNodeResponse(FindNodeRequest request, String ip, int port) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, port);
		node.addNode(nodeInfo);
		FindNodeResponse r = new FindNodeResponse(node.getNodeId());
		List<NodeInfo> nodes = node.findNode(request.targetId);
		for (int i = 0; i < nodes.size() && i < Node.maxBucketSize; i++) {
			r.nodes[i] = nodes.get(i);
		}
		return r;
	}
	
	public GetPeersRequest createGetPeersRequest(byte[] info_hash) {
		GetPeersRequest r = new GetPeersRequest(node.getNodeId(), info_hash);
		return r;
	}
	
	public Response createGetPeersResponse(GetPeersRequest request, String ip, int port) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, port);
		node.addNode(nodeInfo);
		Response r = null;
		LinkedList<PeerInfo> peers = node.getPeers(request.info_hash);
		if (peers != null) {
			GetPeersPeersResponse peerR = new GetPeersPeersResponse(node.getNodeId());
			for(int i = 0; i < peers.size() && i < Node.maxPeerResponseCount; i++) {
				peerR.values[i] = peers.get(i);
			}
			r = peerR;
		} else {
			GetPeersNodesResponse nodesR = new GetPeersNodesResponse(node.getNodeId());
			List<NodeInfo> nodes = node.findNode(request.info_hash);
			for (int i = 0; i < nodes.size() && i < Node.maxBucketSize; i++) {
				nodesR.nodes[i] = nodes.get(i);
			}
			r = nodesR;
		}
		return r;
	}
	
	public AnnouncePeerRequest createAnnouncePeerRequest() {
		AnnouncePeerRequest r = new AnnouncePeerRequest(node.getNodeId(), peerInfo);
		return r;
	}
	
	public AnnouncePeerResponse createAnnouncePeerResponse(AnnouncePeerRequest request, String ip, int port) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, port);
		node.addNode(nodeInfo);
		AnnouncePeerResponse r = new AnnouncePeerResponse(node.getNodeId());
		return r;
	}
	
	public void info() {
		node.printBuckets();
	}
	
	public static void main(String[] args) {
		
		byte[] id = Node.randomId();
		NodeInfo n = new NodeInfo(id, "127.0.0.1", 21);
		NodeInfo n2 = new NodeInfo(id, "127.0.0.1", 21);
		
		Node node =  new Node("127.0.0.1", 25);
		System.out.println(node.addNode(n));
		System.out.println(node.addNode(n2));
		node.printBuckets();
		
		try {
			int port = 5480;
			PeerInfo peerInfo = new PeerInfo();
			peerInfo.address = "address" + port;
			peerInfo.port = 21;
			Connection con = new Connection("127.0.0.1", port, peerInfo);
			con.makeRequest("127.0.0.1", port - 1, con.createPingRequest());
			byte[] target = Node.randomId();
			FindNodeResponse r = (FindNodeResponse) con.makeRequest("127.0.0.1", port - 1, con.createFindNodeRequest(target));
			System.out.println(Arrays.toString(r.nodes));
			Response res = con.makeRequest("127.0.0.1", port - 1, con.createGetPeersRequest(target));
			System.out.println(res.getClass());
			AnnouncePeerResponse announceres = (AnnouncePeerResponse) con.makeRequest("127.0.0.1", port - 1, con.createAnnouncePeerRequest());
			System.out.println(announceres.getClass());
		} catch (IOException | InvalidResponseException e) {
			e.printStackTrace();
			System.out.println("Error sending requests");
		}
	}
}
