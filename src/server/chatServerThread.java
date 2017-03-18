import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Vector;

public class chatServerThread extends Thread {
    protected Socket socket;
    protected Database db;

    public chatServerThread(Socket connectionSocket, Database database) {
        this.socket = connectionSocket;
        this.db = database; //this object should be shared accross all threads
    }

    public void run() {

        /* Set up input output */
        BufferedReader inFromClient = null;
        DataOutputStream outToClient = null;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            outToClient = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        
        
        String clientSentence = null;
        String method = null;
        boolean timeout = false;
        //TODO implement timeout check
        while (true) {
            if (timeout) {
                try {
                    //deleteUser(Id)
                    socket.close(); //closes after a timeout
                } catch (IOException e) {
                    e.printStackTrace();
                } 
                return;
            }

            try {
                method = inFromClient.readLine();
                System.out.println("Receiving HTTP request: " + method);
                if (method.substring(0, 3).equals("GET")) {
                    System.out.println("GET method");
                    
                    int index_idi = method.indexOf("?id=");
                    
                    if (index_idi > 0) { //Received "GET ~~/?id=123"
                        int index_idf = method.substring(index_idi).indexOf(" ");
                        Integer senderId = Integer.valueOf(method.substring(index_idi, index_idf));
                        List<Message> unsentMessages = new Vector<Message>();
                        unsentMessages = db.getUnsentMessages(senderId);
                        int len = unsentMessages.size();
                    
                        String response = "";
                        for (int i = 0; i < len; i++) {
                            Message msg = unsentMessages.get(i);
                            response += "{'id':" + msg.senderId + ",'text':" + msg.text + "}";
                            if (i != len) response += ","; 
                        }
                        response = "[" + response + "]";
                        outToClient.writeBytes(response);
                        outToClient.flush();
                    }
                    else { //Received "GET /" connect: add user to db, return id
                        Integer user_id = db.addUser();
                        String response = "{id:" + user_id.toString() + "}";
                        outToClient.writeBytes(response);
                        outToClient.flush();
                    }
                    
                }
                else if (method.substring(0,4).equals("POST")) {
                    do {
                        clientSentence = inFromClient.readLine();
                    } while (!clientSentence.equals(""));
                    clientSentence = inFromClient.readLine();
                    System.out.println("received message: " + clientSentence);
                    //add message
                    outToClient.writeBytes(clientSentence + "\n\r");
                    outToClient.flush();
                }
                else {
                    System.out.println("Error: wrong method " + method);
                }
                
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
