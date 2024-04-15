package manager;

import task.Task;
import java.util.List;

/**
 * Интерфейс менеджеров истории просмотра объектов учета (задач, подзадач, эпиков)
 *
 * @author Николаев Д.В.
 * @version 2.0
 */
public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}
