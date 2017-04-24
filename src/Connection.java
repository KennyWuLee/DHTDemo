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
import com.google.gson.JsonSyntaxException;

public class Connection {

	private Node node;
	private Listener listen;
	private Gson gson;
	private PeerInfo peerInfo;
	private int port;

	public Connection(String ip, int port, PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
		this.port = port;
		node = new Node(ip, port);
		listen = new Listener(port, this);
		Thread t = new Thread(listen);
		t.start();
		gson = new Gson();
	}

	public Response makeRequest(String ip, int port, Request request) throws IOException, InvalidResponseException {
		log("making " + request.getClass());
		Socket s = new Socket(ip, port);
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

		//send request
		out.println(gson.toJson(request));
		//receive response
		String line = in.readLine();
		s.close();
		JsonParser parser = new JsonParser();
		Response r = null;
		try {
			JsonObject obj = parser.parse(line).getAsJsonObject();
			if (! obj.has("type")) {
				throw new InvalidResponseException();
			}
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
			NodeInfo nodeinfo = new NodeInfo(r.id, ip, port);
			node.addNode(nodeinfo);
		} catch (JsonSyntaxException e) {
			throw new InvalidResponseException();
		}
		return r;
	}

	public PingRequest createPingRequest() {
		PingRequest r = new PingRequest(node.getNodeId(), port);
		return r;
	}

	public PingResponse createPingResponse(PingRequest request, String ip, int remotePort) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, remotePort);
		node.addNode(nodeInfo);
		PingResponse r = new PingResponse(node.getNodeId());
		return r;
	}

	public FindNodeRequest createFindNodeRequest(byte[] targetId) {
		FindNodeRequest r = new FindNodeRequest(node.getNodeId(), targetId, port);
		return r;
	}

	public FindNodeResponse createFindNodeResponse(FindNodeRequest request, String ip, int remotePort) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, remotePort);
		node.addNode(nodeInfo);
		FindNodeResponse r = new FindNodeResponse(node.getNodeId());
		List<NodeInfo> nodes = node.findNode(request.targetId);
		for (int i = 0; i < nodes.size() && i < Node.maxBucketSize; i++) {
			r.nodes[i] = nodes.get(i);
		}
		return r;
	}

	public GetPeersRequest createGetPeersRequest(byte[] info_hash) {
		GetPeersRequest r = new GetPeersRequest(node.getNodeId(), info_hash, port);
		return r;
	}

	public Response createGetPeersResponse(GetPeersRequest request, String ip, int remotePort) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, remotePort);
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
		AnnouncePeerRequest r = new AnnouncePeerRequest(node.getNodeId(), peerInfo, port);
		return r;
	}

	public AnnouncePeerResponse createAnnouncePeerResponse(AnnouncePeerRequest request, String ip, int remotePort) {
		NodeInfo nodeInfo = new NodeInfo(request.id, ip, remotePort);
		node.addNode(nodeInfo);
		AnnouncePeerResponse r = new AnnouncePeerResponse(node.getNodeId());
		return r;
	}

	public void info() {
		node.printBuckets();
	}
	
	public void ping(String ip, int port) {
		PingRequest r = createPingRequest();
		try {
			makeRequest(ip, port, r);
		} catch (IOException | InvalidResponseException e) {
			log("error sending ping");
		}
	}
	
	public void bootstrap() {
		LinkedList<NodeInfo> queue = node.findNode(node.getNodeId());
		while(! queue.isEmpty()) {
			NodeInfo info = queue.removeFirst();
			if(! info.equals(node.getNodeInfo())) {
				try {
					log("request to " + info.ip + ":" + info.port);
					FindNodeRequest r = createFindNodeRequest(node.getNodeId());
					FindNodeResponse res = (FindNodeResponse) makeRequest(info.ip, info.port, r);
					for (NodeInfo result : res.nodes) {
						if (result != null && node.addNode(result)) {
							queue.addLast(result);
						}
					}
				} catch (IOException | InvalidResponseException e) {
					log("error sending find_node");
				}
			}
		}
	}

	public void log(String s) {
		System.out.println(port + ": " + s);
	}
}
