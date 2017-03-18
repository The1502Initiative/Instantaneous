import java.io.*;
import java.net.*;

public class chatServerThread extends Thread {
    protected Socket socket;

    public chatServerThread(Socket connectionSocket) {
        this.socket = connectionSocket;
    }

    public void run() {
        //set up input/output
        BufferedReader inFromClient = null;
        DataOutputStream outToClient = null;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            outToClient = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        
        String clientSentence;
        boolean timeout = false;
        //TODO implement timeout check
        while (true) {
            try {
                clientSentence = inFromClient.readLine();
                if ((clientSentence == null) || clientSentence.equalsIgnoreCase("QUIT") || timeout) {
                    socket.close();
                    return;
                } else {
                    System.out.println("received message: " + clientSentence);
                    outToClient.writeBytes(clientSentence + "\n\r");
                    outToClient.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
