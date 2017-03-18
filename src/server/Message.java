public class Message {
  public Integer messageId;
  public Integer senderId;
  public String text;

  public Message(Integer given_messageId, Integer given_senderId, String given_text) {
    messageId = given_messageId;
    senderId = given_senderId;
    text = given_text;
  }
}
