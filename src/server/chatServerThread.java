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
        String address = this.socket.getLocalAddress() + ":" + this.socket.getLocalPort();
        System.out.println("Chat thread running on " + address);
    }

    public void run() {

        /* Set up input output */
        BufferedReader inFromClient = null;
        PrintWriter outToClient = null;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            outToClient = new PrintWriter(this.socket.getOutputStream());
            //
        } catch (IOException e) {
            return;
        }
        
        
        String clientSentence = null;
        String method = null;
        boolean timeout = false;
        //TODO implement timeout check
        if (timeout) {
            try {
                this.db.deleteUser(id);
                socket.close(); //closes after a timeout
            } catch (IOException e) {
                e.printStackTrace();
            } 
            return;
        }

        try {
            method = inFromClient.readLine();
            if (method == null || method.equals(null)) {
                System.out.println("received null message");
                return;
            }
            System.out.println("Receiving HTTP request: " + method);
            String response = "";
            String header = "HTTP/1.1 200 OK\n" + 
            "Content-Type: application/json\n" + 
            "Access-Control-Allow-Origin: *\n";
            if (method.substring(0, 3).equals("GET")) {
                System.out.println("GET method");
                int index_idi = method.indexOf("?id=");
                
                /* Case 1. Received "GET ~~/?id=123" ping */
                if (index_idi > 0) {
                    System.out.println("Case 1. received GET, retrieving message from db.");
                    int index_idf = method.substring(index_idi).indexOf(" ");
                    Integer senderId = Integer.valueOf(method.substring(index_idi, index_idf));
                    if (!id.equals(senderId)) { //if the id created on connection is different from query
                        System.out.println("ID error: " + this.id.toString() + ", " + senderId.toString());
                        return;
                    }
                    List<Message> unsentMessages = new Vector<Message>();
                    unsentMessages = this.db.getUnsentMessages(senderId);
                    if (unsentMessages == null) {
                        System.out.println("no unsent messages");
                        header += "Content-Length: 0\n" + "\n";
                        System.out.println("writing response: " + header);
                        outToClient.print(response);
                        outToClient.flush();
                    }

                    int len = unsentMessages.size();
                    response = "";
                    for (int i = 0; i < len; i++) {
                        Message msg = unsentMessages.get(i);
                        response += "{\"id\":" + msg.senderId + ",\"text\":" + msg.text + "}";
                        if (i != len) response += ","; 
                    }
                    response = "[" + response + "]" + "\n\r";
                    header += "Content-Length: " + response.length() + "\n" + "\n";
                    response = header + response;
                    System.out.println("writing response: " + response);
                    outToClient.print(response);
                    outToClient.flush();
                }
                /* Case 2. Received "GET ~~/?name=NAME": connect: add user to db, return id */
                else {
                    System.out.println("Case 2. received GET, adding new user to db.");
                    this.id = this.db.addUser();
                    response = "{\"id\":" + this.id.toString() + "}" + "\n\r";
                    header += "Content-Length: " + response.length() + "\n" + "\n";
                    response = header + response;
                    System.out.println("writing response: " + response);
                    outToClient.print(response);
                    outToClient.flush();
                }
                try {
                    do{
                        clientSentence = inFromClient.readLine();
                        System.out.println("flushing input" + clientSentence);
                    } while (clientSentence != null);  
                } catch (NullPointerException e) {
                    
                }
                
            }
            /* Case 3. Received "POST": add message to queue */
            else if (method.substring(0,4).equals("POST")) {
                System.out.println("Case 3. received POST, adding message to db.");
                do {
                    clientSentence = inFromClient.readLine();
                } while (!clientSentence.equals(""));
                clientSentence = inFromClient.readLine();
                System.out.println("adding message to db: " + clientSentence);
                this.db.addMessage(clientSentence, this.id);
                header += "Content-Length: 0\n" + "\n";
                System.out.println("writing response: " + header);
                outToClient.print(response);
                outToClient.flush();
            }
            else {
                System.out.println("Error: wrong method " + method);
            }
            inFromClient.close();
            outToClient.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
