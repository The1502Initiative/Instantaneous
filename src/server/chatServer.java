import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class chatServer {

	public static void main(String[] args) throws Exception {
		Database database = new Database();
		ServerSocket serverSocket = new ServerSocket(3000);
		System.out.println("Chat server running on " + serverSocket.getInetAddress() + serverSocket.getLocalPort());
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			  @Override
			  public void run() {
			     try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
			}, 60*1000);
        Socket connectionSocket = null;

		while(true) { //run loop to wait for new connections
			connectionSocket = serverSocket.accept();
			new chatServerThread(connectionSocket, database).start();
		}
	}
}