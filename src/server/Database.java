import java.util.*;

public class Database {
  private Integer nextUserId = 1;
  private Integer nextMessageId = 1;
  private Map<Integer, UserInfo> userData = new HashMap<Integer, UserInfo>();
  private LinkedList<Message> messageBuffer = new LinkedList<Message>();
  // Counter used to time garbage collection
  private Integer messageCounter = 0;

  public Integer addUser() {
    Integer assigned_id = nextUserId++;
    Node<Message> lastMessageNode = messageBuffer.getTail();
    userData.put(assigned_id, new UserInfo(assigned_id, lastMessageNode));
    return assigned_id;
  }

  public void deleteUser(Integer userId) {
    userData.remove(userId);
  }

  public List<Message> getUnsentMessages(Integer userId) {
    UserInfo user = userData.get(userId);
    if (user == null || messageBuffer.isEmpty()) {
      return null;
    }
    List<Message> unsentMessages = new Vector<Message>();
    Integer tailId = messageBuffer.getTail().getValue().messageId;
    Node<Message> currentMessageNode = user.getLastMessage();
    if (currentMessageNode == null) {
      currentMessageNode = messageBuffer.getHead();
      unsentMessages.add(currentMessageNode.getValue());
    }
    while (currentMessageNode.getValue().messageId != tailId) {
      currentMessageNode = currentMessageNode.getNextRef();
      unsentMessages.add(currentMessageNode.getValue());
    }
    user.setLastMessage(currentMessageNode);
    return unsentMessages;
  }

  public void addMessage(String message, Integer senderId) {
    Message messageObject = new Message(nextMessageId++, senderId, message);
    messageBuffer.add(messageObject);
    if ((++messageCounter) == 1000) {
      this.garbageCollect();
      messageCounter = 0;
    }
  }

  private void garbageCollect() {
    Iterator it = userData.entrySet().iterator();
    System.out.println("Garbage collection starting");
    Integer minMessageId = null;
    while (it.hasNext()) {
      Map.Entry<Integer, UserInfo> pair = (Map.Entry<Integer, UserInfo>)it.next();
      System.out.println(pair.getKey());
      Integer lastMessageId = pair.getValue().getLastMessage().getValue().messageId;
      if (minMessageId == null) {
        minMessageId = lastMessageId;
      }
      else {
        minMessageId = Math.min(minMessageId, lastMessageId);
      }
    }
    if (minMessageId == null) {
      return;
    }
    while (messageBuffer.getHead().getValue().messageId != minMessageId) {
      messageBuffer.deleteFront();
    }
  }
}

class UserInfo {
  private Node<Message> lastMessageReceivedNode;
  private Integer id;

  public UserInfo(Integer userId, Node<Message> listTail) {
    id = userId;
    lastMessageReceivedNode = listTail;
  }

  public Node<Message> getLastMessage() {
    return lastMessageReceivedNode;
  }

  public void setLastMessage(Node<Message> updatedLastMessage) {
    lastMessageReceivedNode = updatedLastMessage;
  }

  public Integer getUserId() {
    return id;
  }
}

