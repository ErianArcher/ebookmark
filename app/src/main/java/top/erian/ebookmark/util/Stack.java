package top.erian.ebookmark.util;

import java.util.LinkedList;

/**
 * Created by root on 17-4-20.
 */

public class Stack<T> {
    private LinkedList<T> ll = new LinkedList<T>();
    public void push(T v) {
        ll.addFirst(v);
    }
    public T peek() {
        return ll.getFirst();
    }
    public T pop(){
        return ll.removeFirst();
    }
    public void clear() { ll.clear(); }
    public boolean empty() {
        return ll.isEmpty();
    }
    public T[] toArray(T[] a) {return ll.toArray(a);}
    public String toString() {
        return ll.toString();
    }
    public int size() {return ll.size();}
}
