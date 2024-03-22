package manager;

import org.junit.jupiter.api.Test;
import task.Task;
import task.Subtask;
import task.Epic;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер задач не создан.");

        taskManager.clearAllTasks();
        ArrayList<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Менеджер не возвращает список задач.");
        assertEquals(0, tasks.size(), "Менеджер не готов к работе.");

        taskManager.clearAllSubtasks();
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Менеджер не возвращает список подзадач.");
        assertEquals(0, subtasks.size(), "Менеджер не готов к работе.");

        taskManager.clearAllEpics();
        ArrayList<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Менеджер не возвращает список эпиков.");
        assertEquals(0, epics.size(), "Менеджер не готов к работе.");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Менеджер истории не создан.");
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "Менеджер не возвращает список истории просмотров.");
        assertEquals(0, history.size(), "Менеджер не готов к работе.");
    }
}