package edu.grinnell.csc207.blockchains;

public class Node<T> {
    private T data;
    public Node<T> next;
    public Node<T> previous;

    public Node(T data) { 
        this.data = data;
    }

    public T getData() { 
        return this.data;
    }
}
