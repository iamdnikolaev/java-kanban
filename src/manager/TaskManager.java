package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import java.util.List;

/**
 * Интерфейс менеджеров работы с объектами учета (задачами, подзадачами, эпиками)
 *
 * @author Николаев Д.В.
 * @version 1.2
 */
public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void clearAllTasks();

    void clearAllSubtasks();

    void clearAllEpics();

    Task getTask(int taskId);

    Subtask getSubtask(int subtaskId);

    Epic getEpic(int epicId);

    Task createTask(Task task);

    Subtask createSubtask(Subtask subtask);

    Epic createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTask(int taskId);

    void removeSubtask(int subtaskId);

    void removeEpic(int epicId);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Subtask> getSubtasksByEpic(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isValid(Task task);

    void clearHistory();
}
