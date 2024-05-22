package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
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
        assertNotEquals(task.getId(), savedTask.getId(), "Добавленная задача совпала по id с уже имеющейся.");
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
}