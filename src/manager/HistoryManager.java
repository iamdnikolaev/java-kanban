package manager;

import task.Task;
import java.util.ArrayList;

/**
 * Интерфейс менеджеров истории просмотра объектов учета (задач, подзадач, эпиков)
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public interface HistoryManager {
    /**
     * Константа количества записей в истории просмотра - глубины сохранения.
     */
    int TASK_HISTORY_DEPTH = 10;

    void add(Task task);

    ArrayList<Task> getHistory();
}
