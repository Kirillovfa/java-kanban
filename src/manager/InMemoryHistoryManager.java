package manager;

import task.Task;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {



    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodes = new HashMap<>();

    private static class Node {
        Task task;
        Node previous;
        Node next;

        Node(Task task, Node previous, Node next) {
            this.task = task;
            this.previous = previous;
            this.next = next;
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task, tail, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        nodes.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node.previous != null) {
            node.previous.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            tail = node.previous;
        }
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        // Если задача уже есть в истории, удалить старое
        remove(task.getId());
        // Добавлить в конец
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodes.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;

        while (current != null) {
            result.add(current.task);
            current = current.next;
        }

        return result;
    }
}