import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> nodesMap = new HashMap<>();
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        Node<Task> node = new Node<>(null, task, null);
        if (nodesMap.containsKey(task.getTaskId())) {
            history.removeNode(nodesMap.get(task.getTaskId()));
        }
        history.linkLast(node);
        nodesMap.put(task.getTaskId(), node);
    }

    @Override
    public void remove(int id) {
        if (nodesMap.containsKey(id)) {
            history.removeNode(nodesMap.get(id));
            nodesMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class Node<T> {
        public T task;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T task, Node<T> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private static class CustomLinkedList<T> {

        private Node<T> head;
        private Node<T> tail;

        void linkLast(Node<T> newNode) {
            final Node<T> oldTail = tail;
            newNode.prev = oldTail;
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }

        List<Task> getTasks() {
            List<Task> historyTasks = new ArrayList<>();
            Node<T> node = head;
            while (node != null) {
                historyTasks.add((Task) node.task);
                node = node.next;
            }
            return historyTasks;
        }

        void removeNode(Node<T> node) {
            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;
            if (prevNode == null && nextNode == null) {
                head = null;
                tail = null;
            } else if (prevNode == null) {
                head = nextNode;
                nextNode.prev = null;
            } else if (nextNode == null) {
                tail = prevNode;
                prevNode.next = null;
            } else {
                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            }
        }
    }
}
