package manager;

import task.Task;


import java.util.*;

/**
 * Менеджер истории просмотра объектов учета (задач, подзадач, эпиков) - реализация интерфейса {@link HistoryManager}
 *
 * @author Николаев Д.В.
 * @version 2.0
 */
public class InMemoryHistoryManager implements HistoryManager {
    /**
     * Поле первого узла в списке просмотренных задач.
     */
    private Node first;
    /**
     * Поле последнего узла в списке просмотренных задач.
     */
    private Node last;
    /**
     * Поле таблица истории просмотренных задач, выстроенной согласно порядку обращения к ним через get-методы.
     * Ключ - id задачи; значение - узел в двусвязном списке просмотренных задач.
     */
    private Map<Integer, Node> taskHistory = new HashMap<>();

    /**
     * Класс узлов двусвязного списка просмотра задач
     */
    private class Node {
        /**
         * Поле задачи, зафиксированной в списке.
         */
        public Task task;
        /**
         * Поле-ссылка на следующий узел списка.
         */
        public Node next;
        /**
         * Поле-ссылка на предыдущий узел списка.
         */
        public Node prev;

        /** Конструктор узла списка
         * @param task просмотренная задача (подзадача, эпик)
         */
        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task + ';' +
                    "prev Node.task.id=" + (prev == null ? "null" : prev.task.getId()) + ';' +
                    "next Node.task.id=" + (next == null ? "null" : next.task.getId()) + ';' +
                    '}';
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Node node = (Node) object;
            return task.getId() == node.task.getId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(task.getId());
        }
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
               "taskHistory=" + getHistory() +
               '}';
    }

    /**
     * Метод добавления задачи в историю просмотра
     *
     * @param task задача (подзадача, эпик) для сохранения
     */
    @Override
    public void add(Task task) {
        if (task != null) {
            Integer taskId = task.getId();
            Node node = taskHistory.get(taskId);
            removeNode(node);
            linkLast(task);
            taskHistory.put(taskId, last);
        }
    }

    /**
     * Метод получения истории просмотра
     *
     * @return ArrayList<Task> список задач (подзадач, эпиков) в истории в порядке обращения к ним
     */
    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        if (!taskHistory.isEmpty()) {
            Node nextNode = first;
            while (nextNode != null) {
                result.add(nextNode.task);
                nextNode = nextNode.next;
            }
        }
        return result;
    }

    /**
     * Метод добавления задачи в конец списка истории просмотра
     *
     * @param task задача для добавления
     */
    private void linkLast(Task task) {
        if (task != null) {
            Node newNode = new Node(task);
            if (last != null) {
                last.next = newNode;
                newNode.prev = last;
            }
            last = newNode;
            if (first == null) {
                first = newNode;
            }
        }
    }

    /**
     * Метод удаления узла из двусвязного списка
     *
     * @param node узел для удаления
     */
    private void removeNode(Node node) {
        if (node != null) {
            if (node.prev != null) {
                Node prevNode = node.prev;
                prevNode.next = node.next;
                if (prevNode.next == null) {
                    last = prevNode;
                }
            } else if (first == node) {
                first = null;
            }
            if (node.next != null) {
                Node nextNode = node.next;
                nextNode.prev = node.prev;
                if (nextNode.prev == null) {
                    first = nextNode;
                }
            } else if (last == node) {
                last = node.prev;
            }
        }
    }

    /**
     * Метод удаления задачи из истории просмотра
     *
     * @param id задачи (подзадачи, эпика) для удаления
     */
    public void remove(int id) {
        if (!taskHistory.isEmpty()) {
            Node node = taskHistory.get(id);
            removeNode(node);
            taskHistory.remove(id);
        }
    }
}
