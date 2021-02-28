package softwaredesign;

class DoublyLinkedList {
    class Node {
        Player item;
        Node previous;
        Node next;

        public Node(Player item) {
            this.item = item;
        }
    }

    Node head, tail = null;

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
}