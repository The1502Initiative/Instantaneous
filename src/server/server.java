import java.io.*;
import java.net.*;

public class server {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(0);
		System.out.println(welcomeSocket.getLocalPort());
		while(true) { //run loop to wait for new connections
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			while(true) { //run to wait for new input
				clientSentence = inFromClient.readLine();
				capitalizedSentence = clientSentence.toUpperCase() + '\n';
				System.out.println(capitalizedSentence);
				outToClient.writeBytes(capitalizedSentence);	
			}
			
		}
//		welcomeSocket.close();
	}
}
