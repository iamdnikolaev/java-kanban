package manager;

import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    @Test
    void addNewTaskAndGet() {
        Task task1 = new Task("Задача для добавления", "Описание задачи для добавления");
        Task savedTask1 = taskManager.createTask(task1);

        assertNotNull(savedTask1, "Задача у менеджера не создана.");
        savedTask1 = taskManager.getTask(task1.getId());
        assertNotNull(savedTask1, "Задача не найдена у менеджера.");
        assertEquals(task1, savedTask1, "Задача в переменной и созданная у менеджера не совпадают.");

        assertNotNull(taskManager.getAllTasks(), "Задачи менеджера не возвращаются.");
        assertEquals(task1.getName(), taskManager.getTask(task1.getId()).getName(),
                "Задача в переменной и созданная у менеджера не совпадают по наименованию.");
        assertEquals(task1.getDescription(), taskManager.getTask(task1.getId()).getDescription(),
                "Задача в переменной и созданная у менеджера не совпадают по описанию.");
        assertEquals(task1.getStatus(), taskManager.getTask(task1.getId()).getStatus(),
                "Задача в переменной и созданная у менеджера не совпадают по статусу.");
    }

    @Test
    void addNewEpicAndGet() {
        Epic epic1 = new Epic("Эпик для добавления", "Описание эпика для добавления");
        Epic savedEpic1 = taskManager.createEpic(epic1);

        assertNotNull(savedEpic1, "Эпик у менеджера не создан.");
        savedEpic1 = taskManager.getEpic(epic1.getId());
        assertNotNull(savedEpic1, "Эпик у менеджера не найден.");
        assertEquals(epic1, savedEpic1, "Эпик в переменной и созданный у менеджера не совпадают.");

        assertNotNull(taskManager.getAllEpics(), "Эпики менеджера не возвращаются.");
        assertEquals(epic1.getName(), taskManager.getEpic(epic1.getId()).getName(),
                "Эпики в переменной и созданный у менеджера не совпадают по наименованию.");
        assertEquals(epic1.getDescription(), taskManager.getEpic(epic1.getId()).getDescription(),
                "Эпики в переменной и созданный у менеджера не совпадают по описанию.");
        assertEquals(epic1.getStatus(), taskManager.getEpic(epic1.getId()).getStatus(),
                "Эпики в переменной и созданный у менеджера не совпадают по статусу.");
    }

    @Test
    void addNewSubtaskAndGet() {
        Epic epic1 = new Epic("Эпик для добавления подзадачи",
                "Описание эпика для добавления подзадачи");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача для добавления",
                "Описание подзадачи для добавления", epic1.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);

        assertNotNull(savedSubtask1, "Подзадача у менеджера не создана.");
        savedSubtask1 = taskManager.getSubtask(subtask1.getId());
        assertNotNull(savedSubtask1, "Подзадача у менеджера не найдена.");
        assertEquals(subtask1, savedSubtask1,
                "Подзадача в переменной и созданная у менеджера не совпадают.");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи менеджера не возвращаются.");
        assertEquals(subtask1.getName(), taskManager.getSubtask(subtask1.getId()).getName(),
                "Подзадача в переменной и созданная у менеджера не совпадают по наименованию.");
        assertEquals(subtask1.getDescription(), taskManager.getSubtask(subtask1.getId()).getDescription(),
                "Подзадача в переменной и созданная у менеджера не совпадают по описанию.");
        assertEquals(subtask1.getStatus(), taskManager.getSubtask(subtask1.getId()).getStatus(),
                "Подзадача в переменной и созданная у менеджера не совпадают по статусу.");
    }

    @Test
    void addGetAndClearAllWithHistory() {
        taskManager.clearHistory(); // Очищаем историю, чтоб не мешали прошлые вызовы геттеров у одиночки taskManager

        Epic epic1 = new Epic("Эпик 1 для проверки полной очистки и отражения в истории",
                "Описание эпика 1 для проверки полной очистки и отражения в истории");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1_1 для проверки полной очистки и отражения в истории",
                "Описание подзадачи 1_1 для проверки полной очистки и отражения в истории", epic1.getId());
        subtask11 = taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2 для проверки полной очистки и отражения в истории",
                "Описание подзадачи 1_2 для проверки полной очистки и отражения в истории", epic1.getId());
        subtask12 = taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Эпик 2 для проверки полной очистки и отражения в истории",
                "Описание эпика 2 для проверки полной очистки и отражения в истории");
        epic2 = taskManager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Подзадача 2_1 для проверки полной очистки и отражения в истории",
                "Описание подзадачи 2_1 для проверки полной очистки и отражения в истории", epic2.getId());
        subtask21 = taskManager.createSubtask(subtask21);
        Subtask subtask22 = new Subtask("Подзадача 2_2 для проверки полной очистки и отражения в истории",
                "Описание подзадачи 2_2 для проверки полной очистки и отражения в истории", epic2.getId());
        subtask22 = taskManager.createSubtask(subtask22);

        Task task1 = new Task("Задача 1 для проверки полной очистки и отражения в истории",
                "Описание задачи 1 для проверки полной очистки и отражения в истории");
        task1 = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2 для проверки полной очистки и отражения в истории",
                "Описание задачи 2 для проверки полной очистки и отражения в истории");
        task2 = taskManager.createTask(task2);

        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "После просмотра эпиков история не пустая.");
        assertEquals(2, history.size(), "После просмотра эпиков в истории 2 записи.");

        taskManager.getSubtask(subtask11.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getSubtask(subtask21.getId());
        taskManager.getSubtask(subtask22.getId());
        history = taskManager.getHistory();
        assertEquals(6, history.size(), "После просмотра эпиков и подзадач в истории 6 записей.");

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        history = taskManager.getHistory();
        assertEquals(8, history.size(), "После просмотра эпиков, подзадач, задач в истории 8 записей.");

        assertEquals(epic1.getId(), history.get(0).getId(), "В истории первой идет запись об эпике 1.");
        assertEquals(task2.getId(), history.get(7).getId(), "В истории последней идет запись о задаче 2.");

        taskManager.clearAllTasks();
        history = taskManager.getHistory();
        assertEquals(6, history.size(), "После удаления задач в истории 6 записей.");
        assertEquals(epic1.getId(), history.get(0).getId(), "В истории первой идет запись об эпике 1.");
        assertEquals(subtask22.getId(), history.get(5).getId(), "В истории последней идет запись о подзадаче 2_2.");

        taskManager.clearAllSubtasks();
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "После удаления подзадач в истории 2 записи.");
        assertEquals(epic1.getId(), history.get(0).getId(), "В истории первой идет запись об эпике 1.");
        assertEquals(epic2.getId(), history.get(1).getId(), "В истории последней идет запись об эпике 2.");

        taskManager.clearAllEpics();
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "После удаления эпиков в истории 0 записей.");
    }

    @Test
    void checkUpdateDelete() {
        Epic epic1 = new Epic("Эпик 1 для проверки добавления и удаления",
                "Описание эпика 1 для проверки добавления и удаления");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1_1 для проверки добавления и удаления",
                "Описание подзадачи 1_1 для проверки добавления и удаления", epic1.getId(),
                35L,
                LocalDateTime.of(2024, 5, 19, 23, 5));
        subtask11 = taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2 для проверки добавления и удаления",
                "Описание подзадачи 1_2 для проверки добавления и удаления", epic1.getId(),
                20L,
                LocalDateTime.of(2024, 5, 19, 23, 40));
        subtask12 = taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Эпик 2 для проверки добавления и удаления",
                "Описание эпика 2 для проверки добавления и удаления");
        epic2 = taskManager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Подзадача 2_1 для проверки добавления и удаления",
                "Описание подзадачи 2_1 для проверки добавления и удаления", epic2.getId(),
                25L,
                LocalDateTime.of(2024, 5, 20, 8, 0));
        subtask21 = taskManager.createSubtask(subtask21);
        Subtask subtask22 = new Subtask("Подзадача 2_2 для проверки добавления и удаления",
                "Описание подзадачи 2_2 для проверки добавления и удаления", epic2.getId(),
                15L,
                LocalDateTime.of(2024, 5, 20, 8, 25));
        subtask22 = taskManager.createSubtask(subtask22);

        Task task1 = new Task("Задача 1 для проверки добавления и удаления",
                "Описание задачи 1 для проверки добавления и удаления",
                5L,
                LocalDateTime.of(2024, 5, 20, 8, 50));
        task1 = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2 для проверки добавления и удаления",
                "Описание задачи 2 для проверки добавления и удаления",
                5L,
                LocalDateTime.of(2024, 5, 20, 8, 55));
        task2 = taskManager.createTask(task2);

        taskManager.updateTask(new Task(task2.getName(), task2.getDescription(), task2.getId(), 10L,
                task2.getStartTime()));
        assertEquals(Duration.ofMinutes(10), taskManager.getTask(task2.getId()).getDuration(), "Продолжительность задачи 2 в менеджере изменена неверно.");

        taskManager.updateSubtask(new Subtask(subtask22.getName(), subtask22.getDescription(), subtask22.getId(),
                subtask22.getEpicId(), 20L, subtask22.getStartTime()));
        assertEquals(Duration.ofMinutes(20), taskManager.getSubtask(subtask22.getId()).getDuration(), "Продолжительность подзадачи 2_2 в менеджере изменена неверно.");

        taskManager.updateEpic(new Epic(epic2.getName(), epic2.getDescription() + " изменено", epic2.getId()));
        assertEquals("Описание эпика 2 для проверки добавления и удаления изменено", taskManager.getEpic(epic2.getId()).getDescription(), "Описание эпика 2 в менеджере изменено неверно.");

        int id = task2.getId();
        taskManager.removeTask(id);
        int finalId = id;
        assertThrows(NotFoundException.class, () -> {taskManager.getTask(finalId);}, "Задача 2 не удалена");

        id = subtask22.getId();
        taskManager.removeSubtask(id);
        int finalId22 = id;
        assertThrows(NotFoundException.class, () -> {taskManager.getSubtask(finalId22);}, "Подзадача 2_2 не удалена");

        id = epic2.getId();
        taskManager.removeEpic(id);
        int finalId2 = id;
        assertThrows(NotFoundException.class, () -> {taskManager.getEpic(finalId2);}, "Эпик 2 не удален");
    }

    @Test
    void getSubtasksByEpic() {
        Epic epic1 = new Epic("Эпик 1 для проверки методов getSubtasksByEpic",
                "Описание эпика 1 для проверки методов getSubtasksByEpic");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1_1 для проверки методов getSubtasksByEpic",
                "Описание подзадачи 1_1 для проверки методов getSubtasksByEpic", epic1.getId(),
                35L,
                LocalDateTime.of(2024, 5, 19, 23, 5));
        subtask11 = taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2 для проверки методов getSubtasksByEpic",
                "Описание подзадачи 1_2 для проверки методов getSubtasksByEpic", epic1.getId(),
                20L,
                LocalDateTime.of(2024, 5, 19, 23, 40));
        subtask12 = taskManager.createSubtask(subtask12);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic1.getId());
        assertNotNull(subtasks, "Менеджер не вернул список подзадач через getSubtasksByEpicId");

        List<Subtask> subtasks2 = taskManager.getSubtasksByEpic(epic1);
        assertNotNull(subtasks2, "Менеджер не вернул список подзадач через getSubtasksByEpic");
    }

    @Test
    void getPrioritizedTasks() {
        Epic epic1 = new Epic("Эпик 1 для проверки приоритетов по StartTime",
                "Описание эпика 1 для проверки приоритетов по StartTime");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1_1 для проверки приоритетов по StartTime",
                "Описание подзадачи 1_1 для проверки приоритетов по StartTime", epic1.getId(),
                35L,
                LocalDateTime.of(2024, 5, 21, 9, 35));
        subtask11 = taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2 для проверки приоритетов по StartTime",
                "Описание подзадачи 1_2 для проверки приоритетов по StartTime", epic1.getId(),
                15L,
                LocalDateTime.of(2024, 5, 20, 9, 0));
        subtask12 = taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Эпик 2 для проверки приоритетов по StartTime",
                "Описание эпика 2 для проверки приоритетов по StartTime");
        epic2 = taskManager.createEpic(epic2);

        Subtask subtask21 = new Subtask("Подзадача 2_1 для проверки приоритетов по StartTime",
                "Описание подзадачи 2_1 для проверки приоритетов по StartTime", epic2.getId(),
                5L,
                LocalDateTime.of(2024, 5, 20, 9, 30));
        subtask21 = taskManager.createSubtask(subtask21);
        Subtask subtask22 = new Subtask("Подзадача 2_2 для проверки приоритетов по StartTime",
                "Описание подзадачи 2_2 для проверки приоритетов по StartTime", epic2.getId(),
                5L,
                LocalDateTime.of(2024, 5, 20, 9, 15));
        subtask22 = taskManager.createSubtask(subtask22);

        Task task1 = new Task("Задача 1 для проверки приоритетов по StartTime",
                "Описание задачи 1 для проверки приоритетов по StartTime",
                5L,
                LocalDateTime.of(2024, 5, 20, 9, 25));
        task1 = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2 для проверки приоритетов по StartTime",
                "Описание задачи 2 для проверки приоритетов по StartTime",
                5L,
                LocalDateTime.of(2024, 5, 20, 9, 20));
        task2 = taskManager.createTask(task2);

        List<Task> allTasks = taskManager.getPrioritizedTasks();
        assertEquals("Подзадача 1_2 для проверки приоритетов по StartTime", allTasks.getFirst().getName(), "Первой в списке задач по приоритетам должна быть 'Подзадача 1_2'");
        assertEquals("Подзадача 1_1 для проверки приоритетов по StartTime", allTasks.getLast().getName(), "Последней в списке задач по приоритетам должна быть 'Подзадача 1_1'");
    }

    @Test
    void checkValid() {
        Epic epic1 = new Epic("Эпик 1 для проверки валидации",
                "Описание эпика 1 для проверки валидации");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1_1 для проверки валидации",
                "Описание подзадачи 1_1 для проверки валидации", epic1.getId(),
                25L,
                LocalDateTime.of(2024, 5, 21, 10, 30));
        subtask11 = taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2 для проверки валидации",
                "Описание подзадачи 1_2 для проверки валидации", epic1.getId(),
                15L,
                LocalDateTime.of(2024, 5, 21, 10, 40));
        Subtask subtask13 = new Subtask("Подзадача 1_3 для проверки валидации",
                "Описание подзадачи 1_3 для проверки валидации", epic1.getId(),
                5L,
                LocalDateTime.of(2024, 5, 21, 10, 55));

        assertDoesNotThrow(() -> {taskManager.isValid(subtask13);},
                "Пересечения подзадач 1_1 и 1_3 по времени исполнения нет");
        assertThrows(InvalidTaskException.class, () -> {taskManager.isValid(subtask12);},
                "Пересечение подзадач 1_1 и 1_2 по времени исполнения");
    }

    @Test
    void clearHistory() {
        Epic epic1 = new Epic("Эпик 1 для проверки истории",
                "Описание эпика 1 для проверки истории");
        taskManager.createEpic(epic1);
        taskManager.getEpic(epic1.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.size() > 0, "Непустая история просмотра задач до очистки");
        taskManager.clearHistory();
        history = taskManager.getHistory();
        assertTrue(history.size() == 0, "Пустая история просмотра задач после очистки");
    }

    @Test
    void checkEpicStatusBySubtaskStatus() {
        Epic epic1 = new Epic("Эпик для проверки расчета статуса",
                "Проверяем смену статуса эпика согласно статусу его подзадач");
        taskManager.createEpic(epic1);

        Subtask subtask1  = new Subtask("Подзадача 1 для проверки расчета статуса",
                "Описание подзадачи 1", epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        assertEquals(subtask1.getEpicId(), epic1.getId(), "У добавленной подзадачи неверный id эпика");

        Subtask subtask2 = new Subtask("Подзадача 2 для проверки расчета статуса",
                "Описание подзадачи 2", epic1.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        assertEquals(subtask2.getEpicId(), epic1.getId(), "У добавленной подзадачи неверный id эпика");
        assertEquals(TaskStatus.NEW, epic1.getStatus(),
                "Статус эпика не соответствует его подзадачам: все подзадачи со статусом NEW");

        taskManager.updateSubtask(new Subtask(subtask1.getName(), subtask1.getDescription(), subtask1.getId(),
                TaskStatus.DONE, subtask1.getEpicId()));
        taskManager.updateSubtask(new Subtask(subtask2.getName(), subtask2.getDescription(), subtask2.getId(),
                TaskStatus.DONE, subtask2.getEpicId()));
        assertEquals(TaskStatus.DONE, epic1.getStatus(),
                "Статус эпика не соответствует его подзадачам: все подзадачи со статусом DONE");

        taskManager.updateSubtask(new Subtask(subtask1.getName(), subtask1.getDescription(), subtask1.getId(),
                TaskStatus.NEW, subtask1.getEpicId()));
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(),
                "Статус эпика не соответствует его подзадачам: подзадачи со статусами NEW и DONE");

        taskManager.updateSubtask(new Subtask(subtask1.getName(), subtask1.getDescription(), subtask1.getId(),
                TaskStatus.IN_PROGRESS, subtask1.getEpicId()));
        taskManager.updateSubtask(new Subtask(subtask2.getName(), subtask2.getDescription(), subtask2.getId(),
                TaskStatus.IN_PROGRESS, subtask2.getEpicId()));
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(),
                "Статус эпика не соответствует его подзадачам: подзадачи со статусом IN_PROGRESS");

    }

    @Test
    void checkSubtaskTimeCrossingThrowException() {
        Epic epic1 = new Epic("Эпик для проверки времени с генерацией исключения",
                "Эпик для проверки пересечения времени выполнения подзадач с генерацией исключения");
        taskManager.createEpic(epic1);

        Subtask subtask1  = new Subtask("Подзадача 1 для проверки времени с генерацией исключения",
                "Подзадача 1 для проверки пересечения времени выполнения с генерацией исключения",
                epic1.getId(),
                40L,
                LocalDateTime.of(2024, 5, 18, 9, 20));
        subtask1 = taskManager.createSubtask(subtask1);

        Subtask subtask2Err  = new Subtask("Подзадача 2 (с ошибкой) для проверки времени с генерацией исключения",
                "Подзадача 2 (с ошибкой) для проверки пересечения времени выполнения с генерацией исключения",
                epic1.getId(),
                5L,
                LocalDateTime.of(2024, 5, 18, 9, 35));

        assertThrows(InvalidTaskException.class, () -> {taskManager.createSubtask(subtask2Err);},
                "Пересечение подзадач 1 и 2 по времени исполнения");
    }

    @Test
    void checkTimeCrossingThrowException() {
        Epic epic2 = new Epic("Эпик 2 для проверки времени с генерацией исключения",
                "Эпик 2 для проверки пересечения времени выполнения подзадач с генерацией исключения");
        taskManager.createEpic(epic2);

        Subtask subtask21  = new Subtask("Подзадача 2_1 для проверки времени с генерацией исключения",
                "Подзадача 2_1 для проверки пересечения времени выполнения с генерацией исключения",
                epic2.getId(),
                40L,
                LocalDateTime.of(2024, 5, 18, 9, 20));
        subtask21 = taskManager.createSubtask(subtask21);

        Subtask subtask22  = new Subtask("Подзадача 2_2 для проверки времени с генерацией исключения",
                "Подзадача 2_2 для проверки пересечения времени выполнения с генерацией исключения",
                epic2.getId(),
                5L,
                LocalDateTime.of(2024, 5, 18, 10, 0));
        subtask22 = taskManager.createSubtask(subtask22);

        Task task1Err = new Task("Задача 1 (с ошибкой) для проверки времени с генерацией исключения",
                "Задача 1 (с ошибкой) для проверки пересечения времени выполнения с генерацией исключения",
                15L,
                LocalDateTime.of(2024, 5, 18, 10, 3));
        
        assertThrows(InvalidTaskException.class, () -> {taskManager.createTask(task1Err);},
                "Пересечение подзадачи 2_2 с задачей 1 по времени исполнения");
    }
}
