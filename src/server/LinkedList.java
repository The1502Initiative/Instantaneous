// Slightly based on implementation from http://www.java2novice.com/data-structures-in-java/linked-list/singly-linked-list/#sthash.aRwx6HJI.dpuf

public class LinkedList<T> {

  private Node<T> head;
  private Node<T> tail;

  public void add(T element) {
    Node<T> nd = new Node<T>();
    nd.setValue(element);
    /**
     * check if the list is empty
     */
    if(head == null) {
      //since there is only one element, both head and
      //tail points to the same object.
      head = nd;
      tail = nd;
    } else {
      //set current tail next link to new node
      tail.setNextRef(nd);
      //set tail as newly created node
      tail = nd;
    }
  }

  public void deleteFront() {
    Node<T> tmp = head;
    // Emil says: I assume this will be garbagecollected
    // because this was what was in the original implementation
    // though this is my first code in Java
    head = tmp.getNextRef();
    if(head == null){
      tail = null;
    }
  }

  public Node<T> getHead() {
    return head;
  }

  public Node<T> getTail() {
    return tail;
  }

  public boolean isEmpty() {
    return head == null;
  }
}

