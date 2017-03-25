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
        } catch (IOException e) {
            return;
        }
        
        
        String clientSentence = null;
        String method = null;
        boolean timeout = false;

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
//                System.out.println("received null message");
                return;
            }
//            System.out.println("Receiving HTTP request: " + method);
            String response = "";
            String header = "HTTP/1.1 200 OK\n" + 
            "Content-Type: application/json\n" + 
            "Access-Control-Allow-Origin: *\n" +
            "Connection: close\n";
            if (method.substring(0, 3).equals("GET")) {
//                System.out.println("GET method");
                
                
                /* Case 1. Received "GET ~~/?id=123" ping */
                if (method.indexOf("?id=") > 0) {
//                    System.out.println("Case 1. received GET, retrieving message from db.");
                    String queryOption = "?id=";
                    int index_i = method.indexOf(queryOption) + queryOption.length();
                    int index_f = index_i + method.substring(index_i).indexOf(" ");
                    Integer senderId = Integer.valueOf(method.substring(index_i, index_f));
                    List<Message> unsentMessages = new Vector<Message>();
                    unsentMessages = this.db.getUnsentMessages(senderId);
                    if (unsentMessages == null) {
//                      System.out.println("no unsent messages");
                        response = "{}\r\n";
                        header += "Content-Length: " + response.length() + "\n" + "\n";
//                        System.out.println("writing response: " + header + response);
                        outToClient.print(header + response);
                        outToClient.flush();
                        
                    }
                    else {
                        int len = unsentMessages.size();
                        response = "";
                        for (int i = 0; i < len; i++) {
                            Message msg = unsentMessages.get(i);
                            Integer msgSenderId = Integer.valueOf(msg.senderId);
                            String msgSenderName = this.db.getName(msgSenderId);
                            response += "{\"name\":\"" + msgSenderName + "\",\"text\":\"" + msg.text + "\"}";
                            if (i != len-1) response += ","; 
                        }
                        response = "{\"messages\":[" + response + "]}" + "\r\n";
                        header += "Content-Length: " + response.length() + "\n" + "\n";
                        response = header + response;
//                      System.out.println("writing response: " + response);
                        outToClient.print(response);
                        outToClient.flush();
                    }
                }
                /* Case 2. Received "GET ~~/?name=NAME": connect: add user to db, return id */
                else {
                    String queryOption = "?name=";
                    int index_i = method.indexOf(queryOption) + queryOption.length();
                    int index_f = index_i + method.substring(index_i).indexOf(" ");
                    String senderName = method.substring(index_i, index_f);
//                    System.out.println("Case 2. received GET, adding new user " + senderName + " to db.");
                    int id =  this.db.addUser(senderName);
//                    System.out.println("User added with id: " + this.db.getName(id));
                    response = "{\"id\":" + id + "}" + "\r\n";
                    header += "Content-Length: " + response.length() + "\n" + "\n";
                    response = header + response;
//                    System.out.println("writing response: " + response);
                    outToClient.print(response);
                    outToClient.flush();
                }
                try {
                    do{
                        clientSentence = inFromClient.readLine();
//                        System.out.println("flsh" + clientSentence);
//                        System.out.print("flsh");
                    } while (clientSentence != null);  
                } catch (NullPointerException e) {
                    
                }
                
            }
            /* Case 3. Received "POST": add message to queue */
            else if (method.substring(0,4).equals("POST")) {
//                System.out.println("Case 3. received POST, adding message to db.");
                String queryOption = "?id=";
                int index_i = method.indexOf(queryOption) + queryOption.length();
                int index_f = index_i + method.substring(index_i).indexOf(" ");
                Integer senderId = Integer.valueOf(method.substring(index_i, index_f));
                do {
                    clientSentence = inFromClient.readLine();
                } while (!clientSentence.equals(""));
                clientSentence = inFromClient.readLine();
                System.out.println("adding message to db: " + clientSentence);
                this.db.addMessage(clientSentence, senderId);
                response = "{}\r\n";
                header += "Content-Length: " + response.length() + "\n" + "\n";
                outToClient.print(header + response);
                outToClient.flush();
            }
            else {
//                System.out.println("Error: wrong method " + method);
            }
//            System.out.print("close socket");
            inFromClient.close();
            outToClient.close();
            this.socket.close();

        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }
    }
}
