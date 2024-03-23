package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер задач не создан.");

        taskManager.clearAllTasks();
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Менеджер не возвращает список задач.");
        assertEquals(0, tasks.size(), "Менеджер не готов к работе.");

        taskManager.clearAllSubtasks();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Менеджер не возвращает список подзадач.");
        assertEquals(0, subtasks.size(), "Менеджер не готов к работе.");

        taskManager.clearAllEpics();
        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Менеджер не возвращает список эпиков.");
        assertEquals(0, epics.size(), "Менеджер не готов к работе.");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Менеджер истории не создан.");
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Менеджер не возвращает список истории просмотров.");
        assertEquals(0, history.size(), "Менеджер не готов к работе.");
    }
}