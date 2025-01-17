import java.util.*;

class LFUCache {

    class Node {
        int key;
        int value;
        int freq;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    class DLList {
        Node head, tail;
        int size;

        DLList() {
            head = new Node(0, 0);
            tail = new Node(0, 0);

            head.next = tail;
            tail.prev = head;
        }

        void add(Node node) {
            head.next.prev = node;
            node.next = head.next;
            node.prev = head;
            head.next = node;

            size++;
        }

        void remove(Node node) {
            Node savedPrev = node.prev;
            Node savedNext = node.next;

            savedPrev.next = savedNext;
            savedNext.prev = savedPrev;

            size--;
        }

        Node removeLast() {
            if (size > 0) {
                Node node = tail.prev;
                remove(node);
                return node;
            }
            else {
                return null;
            }
        }
    }

    int capacity, size, min;
    Map<Integer, Node> nodeMap;
    Map<Integer, DLList> countMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        nodeMap = new HashMap<>();
        countMap = new HashMap<>();
    }

    public int get(int key) {
        Node node = nodeMap.get(key);

        if (node == null)
            return -1;

        update(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0)
            return;

        Node node = nodeMap.get(key);

        if (node != null) {
            node.value = value;
            update(node);
        }
        else {
            node = new Node(key, value);
            nodeMap.put(key, node);

            if (size == capacity) {
                DLList lastList = countMap.get(min);
                nodeMap.remove(lastList.removeLast().key);
                size--;
            }

            size++;
            min = 1;
            DLList newList = countMap.getOrDefault(node.freq, new DLList());
            newList.add(node);
            countMap.put(node.freq, newList);

        }
    }

    private void update(Node node) {
        DLList oldList = countMap.get(node.freq);
        oldList.remove(node);

        if (node.freq == min && oldList.size == 0)
            min++;

        node.freq += 1;

        DLList newList = countMap.getOrDefault(node.freq, new DLList());
        newList.add(node);
        countMap.put(node.freq, newList);
    }
}