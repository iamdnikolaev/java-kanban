package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import java.util.ArrayList;

/**
 * Интерфейс менеджеров работы с объектами учета (задачами, подзадачами, эпиками)
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

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

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    ArrayList<Subtask> getSubtasksByEpic(Epic epic);

    ArrayList<Task> getHistory();
}
