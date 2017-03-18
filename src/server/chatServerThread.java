import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Vector;

public class chatServerThread extends Thread {
    protected Socket socket;
    protected Database db;
    Integer id;

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
                    db.deleteUser(id);
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
                        if (!id.equals(senderId)) { //if the id created on connection is different from query
                            System.out.println("ID error: " + id.toString() + ", " + senderId.toString());
                            return;
                        }
                        List<Message> unsentMessages = new Vector<Message>();
                        unsentMessages = db.getUnsentMessages(senderId);
                        int len = unsentMessages.size();
                    
                        String response = "";
                        for (int i = 0; i < len; i++) {
                            Message msg = unsentMessages.get(i);
                            response += "{'id':" + msg.senderId + ",'text':" + msg.text + "}";
                            if (i != len) response += ","; 
                        }
                        response = "[" + response + "]" + "\n\r";
                        outToClient.writeBytes(response);
                        outToClient.flush();
                    }
                    else { //Received "GET /" connect: add user to db, return id
                        id = db.addUser();
                        String response = "{id:" + id.toString() + "}" + "\n\r";
                        outToClient.writeBytes(response);
                        outToClient.flush();
                    }
                }
                else if (method.substring(0,4).equals("POST")) { //Received "POST" add message to queue
                    do {
                        clientSentence = inFromClient.readLine();
                    } while (!clientSentence.equals(""));
                    clientSentence = inFromClient.readLine();
                    System.out.println("received message: " + clientSentence);
                    db.addMessage(clientSentence, id);
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
