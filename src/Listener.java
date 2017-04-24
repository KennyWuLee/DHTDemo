import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Listener implements Runnable {
	
	private int port;
	private Gson gson;
	private Connection con;
	
	public Listener(int port, Connection con) {
		this.port = port;
		this.con = con;
		gson = new Gson();
	}
	
	public void handleRequest(Socket clientSocket) throws IOException, InvalidRequestException {
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
		String s = in.readLine();
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(s).getAsJsonObject();
		if (! obj.has("type")) {
			throw new InvalidRequestException();
		}
		switch (obj.get("type").getAsString()) {
		case "ping":
			try {
				System.out.println("recieved ping request");
				PingRequest req = gson.fromJson(obj, PingRequest.class);
				PingResponse res = con.createPingResponse();
				out.println(gson.toJson(res));
			} catch (JsonSyntaxException e) {
				throw new InvalidRequestException();
			}
			break;
		case "find_node":
			try {
				System.out.println("recieved find_node request");
				FindNodeRequest req = gson.fromJson(obj, FindNodeRequest.class);
				FindNodeResponse res = con.createFindNodeResponse(req.targetId);
				out.println(gson.toJson(res));
			} catch (JsonSyntaxException e) {
				throw new InvalidRequestException();
			}
			break;
		default:
			throw new InvalidRequestException();
		}
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("connection");
				
				handleRequest(clientSocket);
				
				clientSocket.close();
				System.out.println("closed connection");
			}
		} catch (IOException e) {
			System.out.println("ioexception");
		} catch (InvalidRequestException e) {
			System.out.println("invalid request");
			e.printStackTrace();
		}
	}
	
}
