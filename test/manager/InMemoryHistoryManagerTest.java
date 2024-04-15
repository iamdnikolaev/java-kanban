package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        assertNotNull(historyManager, "Менеджер истории не создан.");

        Task task = new Task("Тестовая задача 1", "Описание тестовой задачи 1");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Менеджер не возвращает список истории просмотров.");
        assertEquals(1, history.size(), "В истории 1 запись.");
    }

    @Test
    void addTaskGetTwiceAndDelete() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Тестовая задача 1", "Описание тестовой задачи 1");
        taskManager.createTask(task);

        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "Менеджер задач не возвращает список истории просмотров.");
        assertEquals(1, history.size(), "В истории 1 запись после двух просмотров одной и той же задачи.");
        taskManager.removeTask(task.getId());
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "В истории нет записей после удаления просмотренной задачи.");
    }

    @Test
    void addEpicGetSubtasksAndDeleteEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача № 1", "Описание подзадачи № 1", epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача № 2", "Описание подзадачи № 2", epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);

        taskManager.getSubtask(subtask2.getId());
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "Менеджер задач не возвращает список истории просмотров.");
        assertEquals(1, history.size(), "В истории 1 запись после просмотра подзадачи № 2.");

        taskManager.getSubtask(subtask1.getId());
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "В истории 2 записи после просмотра подзадачи № 1.");

        assertEquals(subtask2.getId(), history.get(0).getId(), "В истории первой идет запись о подзадаче № 2.");
        assertEquals(subtask1.getId(), history.get(1).getId(), "В истории второй идет запись о подзадаче № 1.");

        taskManager.removeEpic(epic.getId());
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "В истории нет записей о просмотрах подзадач после удаления эпика.");
    }
}