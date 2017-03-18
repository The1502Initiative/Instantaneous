import java.net.*;

public class chatServer {

	public static void main(String[] args) throws Exception {
		Database database = new Database();
		ServerSocket serverSocket = new ServerSocket(0);
		System.out.println("Chat server running on port " + serverSocket.getLocalPort());
        Socket connectionSocket = null;

		while(true) { //run loop to wait for new connections
			connectionSocket = serverSocket.accept();
			new chatServerThread(connectionSocket, database).start();
		}
	}
}