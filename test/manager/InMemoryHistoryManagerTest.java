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
    void emptyHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        assertNotNull(historyManager, "Менеджер истории не создан.");

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Менеджер не возвращает список истории просмотров.");
        assertEquals(0, history.size(), "В истории 0 записей.");
    }

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

    @Test
    void deleteBeginMiddleEnd() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        taskManager.getEpic(epic.getId());

        Subtask subtask1 = new Subtask("Подзадача № 1", "Описание подзадачи № 1", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.getSubtask(subtask1.getId());
        Subtask subtask2 = new Subtask("Подзадача № 2", "Описание подзадачи № 2", epic.getId());
        taskManager.createSubtask(subtask2);
        taskManager.getSubtask(subtask2.getId());

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task1);
        taskManager.getTask(task1.getId());
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task2);
        taskManager.getTask(task2.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "Менеджер задач не возвращает список истории просмотров.");
        assertEquals(5, history.size(), "В истории 5 записей.");

        taskManager.removeTask(task2.getId());
        history = taskManager.getHistory();
        assertEquals(4, history.size(), "После удаления последней записи в истории 4 строки.");
        assertEquals(epic.getId(), history.getFirst().getId(), "В истории первой идет запись об эпике.");
        assertEquals(task1.getId(), history.getLast().getId(), "В истории последней идет запись о задаче 1.");

        taskManager.removeSubtask(subtask2.getId());
        history = taskManager.getHistory();
        assertEquals(3, history.size(), "После удаления записи в середине (о подзадаче 2) в истории 3 строки.");
        assertEquals(epic.getId(), history.getFirst().getId(), "В истории первой идет запись об эпике.");
        assertEquals(task1.getId(), history.getLast().getId(), "В истории последней идет запись о задаче 1.");

        taskManager.removeEpic(epic.getId());
        history = taskManager.getHistory();
        assertEquals(1, history.size(), "После удаления последней записи в истории 1 строка.");
        assertEquals(task1.getId(), history.getFirst().getId(), "В истории первой идет запись о задаче 1.");
        assertEquals(task1.getId(), history.getLast().getId(), "В истории последней идет запись о задаче 1.");
    }
}