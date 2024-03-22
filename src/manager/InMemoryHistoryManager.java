package manager;

import task.Task;
import java.util.ArrayList;

/**
 * Менеджер истории просмотра объектов учета (задач, подзадач, эпиков) - реализация интерфейса {@link HistoryManager}
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> taskHistory = new ArrayList<>();

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "taskHistory=" + taskHistory +
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
            if (taskHistory.size() == TASK_HISTORY_DEPTH) {
                taskHistory.remove(0);
            }
            taskHistory.add(task);
        }
    }

    /**
     * Метод получения истории просмотра
     *
     * @return ArrayList<Task> список задач (подзадач, эпиков) в истории в порядке обращения к ним
     */
    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
