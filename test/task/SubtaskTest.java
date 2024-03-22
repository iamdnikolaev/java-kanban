package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", 1, TaskStatus.NEW, 3);
        assertNotNull(subtask, "Задача добавлена.");
    }
    @Test
    void addTask() {
        assertEquals("Подзадача 1", subtask.getName(), "Наименование сохранено успешно.");
        assertEquals("Описание подзадачи 1", subtask.getDescription(), "Описание сохранено успешно.");
        assertEquals(1, subtask.getId(), "Идентификатор задан успешно.");
        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Статус задан успешно.");
        assertEquals(3, subtask.getEpicId(), "Идентификатор эпика задан успешно.");
    }

    @Test
    void updateSubtask() {
        subtask.setName("Подзадача 2");
        subtask.setDescription("Описание подзадачи 2");
        subtask.setId(2);
        assertEquals("Подзадача 2", subtask.getName(), "Наименование изменено успешно.");
        assertEquals("Описание подзадачи 2", subtask.getDescription(), "Описание изменено успешно.");
        assertEquals(2, subtask.getId(), "Идентификатор изменен успешно.");
    }
}