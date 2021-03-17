package softwaredesign.core;

public class DoublyLinkedList {
    public class Node {
        public Player item;
        public Node previous;
        public Node next;

        public Node(Player item) {
            this.item = item;
        }
    }

    public Node head, tail = null;

    public void addNode(Player item) {
        Node newNode = new Node(item);

        if (head == null) {
            head = tail = newNode;
            head.previous = tail;
            head.next = tail;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
            tail.next = head;
        }
    }

    public void deleteHeadNode(){
        Node temp = head.next;
        while(temp != head){
            if(temp.previous == head )
                temp.previous = head.previous;
            if(temp.next == head) {
                temp.next = head.next;
                break;
            }
            temp = temp.next;
        }
        head = temp;
    }
}