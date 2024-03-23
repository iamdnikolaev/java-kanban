package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    private Task task;
    private Task savedTask;

    private Subtask subtask;
    private Subtask savedSubtask;
    private Epic epic;
    private Epic savedEpic;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();

        task = new Task("Тестовая задача 1", "Описание тестовой задачи 1");
        savedTask = taskManager.createTask(task);

        epic = new Epic("Эпик 1", "Описание эпика 1");
        savedEpic = taskManager.createEpic(epic);

        subtask = new Subtask("Подзадача 1_1", "Описание подзадачи 1_1", savedEpic.getId());
        savedSubtask = taskManager.createSubtask(subtask);
    }

    @Test
    void addNewTask() {
        assertNotNull(savedTask, "Задача не создана.");
        savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.getId(), tasks.getFirst().getId(), "Задачи не совпадают по id.");
        assertEquals(task.getName(), tasks.getFirst().getName(), "Задачи не совпадают по наименованию.");
        assertEquals(task.getDescription(), tasks.getFirst().getDescription(), "Задачи не совпадают по описанию.");
        assertEquals(task.getStatus(), tasks.getFirst().getStatus(), "Задачи не совпадают по статусу.");
    }

    @Test
    void addNewEpic() {
        assertNotNull(savedEpic, "Эпик не создан.");
        savedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic.getId(), epics.getFirst().getId(), "Эпики не совпадают по id.");
        assertEquals(epic.getName(), epics.getFirst().getName(), "Эпики не совпадают по наименованию.");
        assertEquals(epic.getDescription(), epics.getFirst().getDescription(), "Эпики не совпадают по описанию.");
        assertEquals(epic.getStatus(), epics.getFirst().getStatus(), "Эпики не совпадают по статусу.");
    }

    @Test
    void addNewSubtask() {
        assertNotNull(savedSubtask, "Подзадача не создана.");
        savedSubtask = taskManager.getSubtask(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask.getId(), subtasks.getFirst().getId(), "Подзадачи не совпадают по id.");
        assertEquals(subtask.getName(), subtasks.getFirst().getName(), "Подзадачи не совпадают по наименованию.");
        assertEquals(subtask.getDescription(), subtasks.getFirst().getDescription(), "Подзадачи не совпадают по описанию.");
        assertEquals(subtask.getStatus(), subtasks.getFirst().getStatus(), "Подзадачи не совпадают по статусу.");
    }

    @Test
    void addNewTaskSameId() {
        Task task = new Task("Тестовая задача 2", "Описание тестовой задачи 2", savedTask.getId());
        taskManager.createTask(task);
        assertNotEquals(task.getId(), savedTask.getId(), "Добавленная задача совпала по id с уже имещейся.");
    }

    @Test
    void getHistory() {
        taskManager.getEpic(savedEpic.getId());
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "После просмотра эпика история не пустая.");
        assertEquals(1, history.size(), "После просмотра эпика в истории 1 запись.");

        taskManager.getSubtask(savedSubtask.getId());
        history = taskManager.getHistory();
        assertNotNull(history, "После просмотра подзадачи история не пустая.");
        assertEquals(2, history.size(), "После просмотра эпика и подзадачи в истории 2 записи.");

        taskManager.getTask(savedTask.getId());
        history = taskManager.getHistory();
        assertNotNull(history, "После просмотра задачи история не пустая.");
        assertEquals(3, history.size(), "После просмотра эпика, подзадачи, задачи в истории 3 записи.");
    }

    @Test
    void createSubtaskLikeHerEpic() {
        Subtask subtaskLikeEpic = new Subtask(savedEpic.getName(), savedEpic.getDescription(), savedEpic.getId(), savedEpic.getId());
        taskManager.createSubtask(subtaskLikeEpic);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(savedEpic.getId());
        for (Subtask subtaskByEpic : subtasks) {
            assertNotEquals(subtaskByEpic.getId(), savedEpic.getId(), "Эпик добавлен к себе в подзадачу.");
        }
    }

    @Test
    void updateSubtaskBecomesHerEpic() {
        subtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getId());
        taskManager.updateSubtask(subtask);

        Subtask subtaskAfterUpd = taskManager.getSubtask(subtask.getId());
        assertNotEquals(subtaskAfterUpd.getId(), subtaskAfterUpd.getEpicId(), "Подзадача стала своим эпиком.");
    }
}