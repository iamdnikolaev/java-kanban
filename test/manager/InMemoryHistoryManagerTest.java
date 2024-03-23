package manager;

import org.junit.jupiter.api.Test;
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
}