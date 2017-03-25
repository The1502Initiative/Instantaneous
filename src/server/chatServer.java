import java.net.*;

public class chatServer {

	public static void main(String[] args) throws Exception {
		Database database = new Database();
		int port = 3000;
		InetAddress address = InetAddress.getLocalHost();
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println(serverSocket.getLocalSocketAddress());
		System.out.println("Chat server listening on " + address + ":" + port);
        Socket connectionSocket = null;

		while(true) { //run loop to wait for new connections
			connectionSocket = serverSocket.accept();
			new chatServerThread(connectionSocket, database).start();
		}
	}
}