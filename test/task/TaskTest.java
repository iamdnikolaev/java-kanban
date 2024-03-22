package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.NEW);
        assertNotNull(task, "Задача добавлена.");
    }
    @Test
    void addTask() {
        assertEquals("Задача 1", task.getName(), "Наименование сохранено успешно.");
        assertEquals("Описание задачи 1", task.getDescription(), "Описание сохранено успешно.");
        assertEquals(1, task.getId(), "Идентификатор задан успешно.");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус задан успешно.");
    }

    @Test
    void updateTask() {
        task.setName("Задача 2");
        task.setDescription("Описание задачи 2");
        task.setId(2);
        assertEquals("Задача 2", task.getName(), "Наименование изменено успешно.");
        assertEquals("Описание задачи 2", task.getDescription(), "Описание изменено успешно.");
        assertEquals(2, task.getId(), "Идентификатор изменен успешно.");
    }
}