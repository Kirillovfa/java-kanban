package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getId()); // удалить старую запись если есть
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node == null) return;
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        nodeMap.clear();
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        if (tail != null) {
            tail.next = node;
            node.prev = tail;
            tail = node;
        } else {
            head = node;
            tail = node;
        }
        nodeMap.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}