import java.util.Scanner;

public class Demo {
	public static void main(String[] args) {
		//int port = Integer.parseInt(args[0]);
		int port = 5234;
		PeerInfo peerInfo = new PeerInfo();
		//peerInfo.address = args[1];
		peerInfo.address = "testaddress";
		peerInfo.port = 1234;
		
		Connection con = new Connection("127.0.0.1", port, peerInfo);
		
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()) {
			switch(scan.nextLine()) {
			case "info":
				con.info();
				break;
			default:
				break;
			}
		}
	}
}
