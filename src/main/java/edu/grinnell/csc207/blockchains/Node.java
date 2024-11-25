package edu.grinnell.csc207.blockchains;

/**
 * Represents a single node in a linked structure Contains pointer to next and previous connected
 * node.
 *
 * @author Harrison Zhu
 * @param <T> Type of data enclosed within the node
 */
public class Node<T> {
  /** Enclosed data. */
  private T data;

  /** Pointer to next node. */
  private Node<T> next;

  /** Pointer to previous node. */
  private Node<T> previous;

  /**
   * Create a node. Next and previous are set to null.
   *
   * @param d data
   */
  public Node(T d) {
    this.data = d;
  } // Node(T)

  /**
   * Gets the enclosed data.
   *
   * @return data
   */
  public T getData() {
    return this.data;
  } // getData

  /**
   * Gets the next node.
   *
   * @return next node
   */
  public Node<T> getNext() {
    return next;
  } // getNext

  /**
   * Sets the next node.
   *
   * @param node the node to set.
   */
  public void setNext(Node<T> node) {
    this.next = node;
  } // setNext(Node<T>)

  /**
   * Gets the previous node.
   *
   * @return previous node.
   */
  public Node<T> getPrevious() {
    return previous;
  } // getPrevious

  /**
   * Sets the previous node.
   *
   * @param node previous node.
   */
  public void setPrevious(Node<T> node) {
    this.previous = node;
  } // setPrevious
} // Node<T>
