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
    void addTwoSubtasksAndDeleteOne() {
        assertNotNull(savedSubtask, "Подзадача 1_1 не создана.");
        savedSubtask = taskManager.getSubtask(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача 1_1 не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи созданная и добавленная не совпадают.");

        Subtask subtask2 = new Subtask("Подзадача 1_2", "Описание подзадачи 1_2", savedEpic.getId());
        subtask2 = taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        List<Integer> subtasksListId = savedEpic.getSubtaskList();
        assertEquals(2, subtasksListId.size(), "Неверное количество подзадач в поле эпика.");

        taskManager.removeSubtask(savedSubtask.getId());
        subtasksListId = savedEpic.getSubtaskList();
        assertEquals(1, subtasksListId.size(), "Неверное количество подзадач в поле эпика после удаления подзадачи 1_1.");
        assertEquals(subtask2.getId(), subtasksListId.getFirst(), "Неверный id оставшейся подзадачи 1_2 в поле эпика после удаления подзадачи 1_1.");
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

        assertEquals(savedEpic.getId(), history.get(0).getId(), "В истории первой идет запись об эпике.");
        assertEquals(savedTask.getId(), history.get(2).getId(), "В истории последней идет запись о задаче.");
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

    @Test
    void addGetAndClear() {
        Subtask subtask12 = new Subtask("Подзадача 1_2", "Описание подзадачи 1_2", savedEpic.getId());
        subtask12 = taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic2 = taskManager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Подзадача 2_1", "Описание подзадачи 2_1", epic2.getId());
        subtask21 = taskManager.createSubtask(subtask21);
        Subtask subtask22 = new Subtask("Подзадача 2_2", "Описание подзадачи 2_2", epic2.getId());
        subtask22 = taskManager.createSubtask(subtask22);

        Task task2 = new Task("Тестовая задача 2", "Описание тестовой задачи 2");
        task2 = taskManager.createTask(task2);

        taskManager.getEpic(savedEpic.getId());
        taskManager.getEpic(epic2.getId());
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "После просмотра эпиков история не пустая.");
        assertEquals(2, history.size(), "После просмотра эпиков в истории 2 записи.");

        taskManager.getSubtask(savedSubtask.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getSubtask(subtask21.getId());
        taskManager.getSubtask(subtask22.getId());
        history = taskManager.getHistory();
        assertEquals(6, history.size(), "После просмотра эпиков и подзадач в истории 6 записей.");

        taskManager.getTask(savedTask.getId());
        taskManager.getTask(task2.getId());
        history = taskManager.getHistory();
        assertEquals(8, history.size(), "После просмотра эпиков, подзадач, задач в истории 8 записи.");

        assertEquals(savedEpic.getId(), history.get(0).getId(), "В истории первой идет запись об эпике 1.");
        assertEquals(task2.getId(), history.get(7).getId(), "В истории последней идет запись о задаче 2.");

        taskManager.clearAllTasks();
        history = taskManager.getHistory();
        assertEquals(6, history.size(), "После удаления задач в истории 6 записей.");
        assertEquals(savedEpic.getId(), history.get(0).getId(), "В истории первой идет запись об эпике 1.");
        assertEquals(subtask22.getId(), history.get(5).getId(), "В истории последней идет запись о подзадаче 2_2.");

        taskManager.clearAllSubtasks();
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "После удаления подзадач в истории 2 записи.");
        assertEquals(savedEpic.getId(), history.get(0).getId(), "В истории первой идет запись об эпике 1.");
        assertEquals(epic2.getId(), history.get(1).getId(), "В истории последней идет запись об эпике 2.");

        taskManager.clearAllEpics();
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "После удаления эпиков в истории 0 записей.");
    }
}