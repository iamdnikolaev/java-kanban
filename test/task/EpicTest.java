package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("Эпик 1", "Описание эпика 1", 1);
        assertNotNull(epic, "Эпик добавлен.");
    }

    @Test
    void addEpic() {
        assertEquals("Эпик 1", epic.getName(), "Наименование сохранено успешно.");
        assertEquals("Описание эпика 1", epic.getDescription(), "Описание сохранено успешно.");
        assertEquals(1, epic.getId(), "Идентификатор задан успешно.");
    }

    @Test
    void updateEpic() {
        epic.setName("Эпик 2");
        epic.setDescription("Описание эпика 2");
        epic.setId(2);
        assertEquals("Эпик 2", epic.getName(), "Наименование изменено успешно.");
        assertEquals("Описание эпика 2", epic.getDescription(), "Описание изменено успешно.");
        assertEquals(2, epic.getId(), "Идентификатор изменен успешно.");
    }

    @Test
    void addSubtask() {
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", 3, epic.getId());
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask, subtasks);
        ArrayList<Integer> subtaskList = epic.getSubtaskList();
        assertNotNull(subtaskList, "Создан список подзадач.");
        assertTrue(subtaskList.contains(subtask.getId()), "В списке подзадач есть добавленная подзадача");
    }

    @Test
    void getSubtaskList() {
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", 4, epic.getId());
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask, subtasks);
        ArrayList<Integer> subtaskList = epic.getSubtaskList();
        assertNotNull(subtaskList, "Создан список подзадач.");
    }

    @Test
    void removeSubtask() {
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", 5, epic.getId());
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask, subtasks);
        ArrayList<Integer> subtaskList = epic.getSubtaskList();
        assertNotNull(subtaskList, "Создан список подзадач.");
        assertEquals(1, subtaskList.size(), "В списке 1 подзадача.");
        epic.removeSubtask(subtask.getId(), subtasks);
        subtaskList = epic.getSubtaskList();
        assertEquals(0, subtaskList.size(), "В списке 0 подзадач.");
    }

    @Test
    void clearSubtasks() {
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", 6, epic.getId());
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask, subtasks);
        ArrayList<Integer> subtaskList = epic.getSubtaskList();
        assertNotNull(subtaskList, "Создан список подзадач.");
        assertEquals(1, subtaskList.size(), "В списке 1 подзадача.");
        epic.clearSubtasks();
        subtaskList = epic.getSubtaskList();
        assertEquals(0, subtaskList.size(), "В списке 0 подзадач.");
    }

}