package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFileTasks;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    private Subtask subtask11;
    private Subtask subtask12;
    private Subtask subtask13;
    private Subtask subtask14;
    private Subtask subtask15;
    private Subtask subtask21;
    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void beforeEach() {
        try {
            testFileTasks = File.createTempFile("testFileTasks", null);
            taskManager = new FileBackedTaskManager(testFileTasks);

            assertDoesNotThrow(() -> {epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));},
                    "Ошибки записи в файл не возникло");
            epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));
            subtask11 = taskManager.createSubtask(new Subtask("Подзадача 1_1", "Описание подзадачи 1_1", epic1.getId()));
            subtask12 = taskManager.createSubtask(new Subtask("Подзадача 1_2", "Описание подзадачи 1_2", epic1.getId()));
            subtask15 = taskManager.createSubtask(new Subtask("Подзадача 1_5", "Описание подзадачи 1_5", epic1.getId(),
                    10L, LocalDateTime.of(2024, 5, 20, 21, 50)));
            subtask14 = taskManager.createSubtask(new Subtask("Подзадача 1_4", "Описание подзадачи 1_4", epic1.getId(),
                    20L, LocalDateTime.of(2024, 5, 20, 21, 15)));
            subtask13 = taskManager.createSubtask(new Subtask("Подзадача 1_3", "Описание подзадачи 1_3", epic1.getId(),
                    15L, LocalDateTime.of(2024, 5, 20, 21, 0)));
            subtask21 = taskManager.createSubtask(new Subtask("Подзадача 2_1", "Описание подзадачи 2_1", epic2.getId()));

            task1 = taskManager.createTask(new Task("Тестовая задача 1", "Описание тестовой задачи 1"));
            task2 = taskManager.createTask(new Task("Тестовая задача 2", "Описание тестовой задачи 2"));
            task3 = taskManager.createTask(new Task("Тестовая задача 3", "Описание тестовой задачи 3", 5L,
                    LocalDateTime.of(2024, 5, 20, 19, 0)));
            task4 = taskManager.createTask(new Task("Тестовая задача 4", "Описание тестовой задачи 4", 44L,
                    LocalDateTime.of(2024, 5, 20, 19, 5)));

            taskManager.updateTask(new Task(task1.getName(), task1.getDescription(), task1.getId(), TaskStatus.IN_PROGRESS));
            task1 = taskManager.getTask(task1.getId());
            taskManager.updateTask(new Task(task2.getName(), task2.getDescription(), task2.getId(), TaskStatus.DONE));
            task2 = taskManager.getTask(task2.getId());
            taskManager.updateTask(new Task(task4.getName(), task4.getDescription(), task4.getId(), TaskStatus.DONE,
                    task4.getDuration().toMinutes(), task4.getStartTime()));
            task4 = taskManager.getTask(task4.getId());
            taskManager.updateSubtask(new Subtask(subtask12.getName(), subtask12.getDescription(), subtask12.getId(), TaskStatus.IN_PROGRESS, subtask12.getEpicId()));
            subtask12 = taskManager.getSubtask(subtask12.getId());
            taskManager.updateSubtask(new Subtask(subtask21.getName(), subtask21.getDescription(), subtask21.getId(), TaskStatus.DONE, subtask21.getEpicId()));
            subtask21 = taskManager.getSubtask(subtask21.getId());

        } catch (IOException e) {
            System.out.println("Произошла ошибка создания временного файла.");
            e.printStackTrace();
        }
    }

    @Test
    void checkFileLineByLine() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(testFileTasks, StandardCharsets.UTF_8))) {
            int counter = 0;
            if (fileReader.ready()) {
                String line = fileReader.readLine();
                assertEquals("id,type,name,status,description,epic,duration,startTime", line, "Неверный формат CSV в начале файла.");
                counter++;
            }
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                counter++;
                String[] attrFromString = line.split(",");
                TaskStatus status = TaskStatus.valueOf(attrFromString[3]);
                TaskType type = TaskType.valueOf(attrFromString[1]);
                int id = Integer.parseInt(attrFromString[0]);

                switch (counter) {
                    case 2:
                        assertEquals(epic1.getId(), id, "Не верно сохранен id эпика 1.");
                        assertEquals(TaskType.EPIC, type, "Не верно сохранен тип эпика 1.");
                        assertEquals(epic1.getName(), attrFromString[2], "Не верно сохранено наименование эпика 1.");
                        assertEquals(epic1.getStatus(), status, "Не верно сохранен статус эпика 1.");
                        assertEquals(epic1.getDescription(), attrFromString[4], "Не верно сохранено описание эпика 1.");
                        break;
                    case 3:
                        assertEquals(epic2.getId(), id, "Не верно сохранен id эпика 2.");
                        assertEquals(TaskType.EPIC, type, "Не верно сохранен тип эпика 2.");
                        assertEquals(epic2.getName(), attrFromString[2], "Не верно сохранено наименование эпика 2.");
                        assertEquals(epic2.getStatus(), status, "Не верно сохранен статус эпика 2.");
                        assertEquals(epic2.getDescription(), attrFromString[4], "Не верно сохранено описание эпика 2.");
                        break;
                    case 4:
                        assertEquals(subtask11.getId(), id, "Не верно сохранен id подзадачи 1_1.");
                        assertEquals(TaskType.SUBTASK, type, "Не верно сохранен тип подзадачи 1_1.");
                        assertEquals(subtask11.getName(), attrFromString[2], "Не верно сохранено наименование подзадачи 1_1.");
                        assertEquals(subtask11.getStatus(), status, "Не верно сохранен статус подзадачи 1_1.");
                        assertEquals(subtask11.getDescription(), attrFromString[4], "Не верно сохранено описание подзадачи 1_1.");
                        assertEquals(subtask11.getEpicId(), Integer.parseInt(attrFromString[5]), "Не верно сохранен id эпика у подзадачи 1_1.");
                        break;
                    case 5:
                        assertEquals(subtask12.getId(), id, "Не верно сохранен id подзадачи 1_2.");
                        assertEquals(TaskType.SUBTASK, type, "Не верно сохранен тип подзадачи 1_2.");
                        assertEquals(subtask12.getName(), attrFromString[2], "Не верно сохранено наименование подзадачи 1_2.");
                        assertEquals(subtask12.getStatus(), status, "Не верно сохранен статус подзадачи 1_2.");
                        assertEquals(subtask12.getDescription(), attrFromString[4], "Не верно сохранено описание подзадачи 1_2.");
                        assertEquals(subtask12.getEpicId(), Integer.parseInt(attrFromString[5]), "Не верно сохранен id эпика у подзадачи 1_2.");
                        break;
                    case 6:
                        assertEquals(subtask15.getId(), id, "Не верно сохранен id подзадачи 1_5.");
                        assertEquals(TaskType.SUBTASK, type, "Не верно сохранен тип подзадачи 1_5.");
                        assertEquals(subtask15.getName(), attrFromString[2], "Не верно сохранено наименование подзадачи 1_5.");
                        assertEquals(subtask15.getStatus(), status, "Не верно сохранен статус подзадачи 1_5.");
                        assertEquals(subtask15.getDescription(), attrFromString[4], "Не верно сохранено описание подзадачи 1_5.");
                        assertEquals(subtask15.getEpicId(), Integer.parseInt(attrFromString[5]), "Не верно сохранен id эпика у подзадачи 1_5.");
                        assertEquals(subtask15.getDuration().toMinutes(), Integer.parseInt(attrFromString[6]), "Не верно сохранена продолжительность у подзадачи 1_5.");
                        assertEquals(subtask15.getStartTime(), LocalDateTime.parse(attrFromString[7]), "Не верно сохранены дата и время начала у подзадачи 1_5.");
                        break;
                    case 7:
                        assertEquals(subtask14.getId(), id, "Не верно сохранен id подзадачи 1_4.");
                        assertEquals(TaskType.SUBTASK, type, "Не верно сохранен тип подзадачи 1_4.");
                        assertEquals(subtask14.getName(), attrFromString[2], "Не верно сохранено наименование подзадачи 1_4.");
                        assertEquals(subtask14.getStatus(), status, "Не верно сохранен статус подзадачи 1_4.");
                        assertEquals(subtask14.getDescription(), attrFromString[4], "Не верно сохранено описание подзадачи 1_4.");
                        assertEquals(subtask14.getEpicId(), Integer.parseInt(attrFromString[5]), "Не верно сохранен id эпика у подзадачи 1_4.");
                        assertEquals(subtask14.getDuration().toMinutes(), Integer.parseInt(attrFromString[6]), "Не верно сохранена продолжительность у подзадачи 1_4.");
                        assertEquals(subtask14.getStartTime(), LocalDateTime.parse(attrFromString[7]), "Не верно сохранены дата и время начала у подзадачи 1_4.");
                        break;
                    case 8:
                        assertEquals(subtask13.getId(), id, "Не верно сохранен id подзадачи 1_3.");
                        assertEquals(TaskType.SUBTASK, type, "Не верно сохранен тип подзадачи 1_3.");
                        assertEquals(subtask13.getName(), attrFromString[2], "Не верно сохранено наименование подзадачи 1_3.");
                        assertEquals(subtask13.getStatus(), status, "Не верно сохранен статус подзадачи 1_3.");
                        assertEquals(subtask13.getDescription(), attrFromString[4], "Не верно сохранено описание подзадачи 1_3.");
                        assertEquals(subtask13.getEpicId(), Integer.parseInt(attrFromString[5]), "Не верно сохранен id эпика у подзадачи 1_3.");
                        assertEquals(subtask13.getDuration().toMinutes(), Integer.parseInt(attrFromString[6]), "Не верно сохранена продолжительность у подзадачи 1_3.");
                        assertEquals(subtask13.getStartTime(), LocalDateTime.parse(attrFromString[7]), "Не верно сохранены дата и время начала у подзадачи 1_3.");
                        break;
                    case 9:
                        assertEquals(subtask21.getId(), id, "Не верно сохранен id подзадачи 2_1.");
                        assertEquals(TaskType.SUBTASK, type, "Не верно сохранен тип подзадачи 2_1.");
                        assertEquals(subtask21.getName(), attrFromString[2], "Не верно сохранено наименование подзадачи 2_1.");
                        assertEquals(subtask21.getStatus(), status, "Не верно сохранен статус подзадачи 2_1.");
                        assertEquals(subtask21.getDescription(), attrFromString[4], "Не верно сохранено описание подзадачи 2_1.");
                        assertEquals(subtask21.getEpicId(), Integer.parseInt(attrFromString[5]), "Не верно сохранен id эпика у подзадачи 2_1.");
                        break;
                    case 10:
                        assertEquals(task1.getId(), id, "Не верно сохранен id задачи 1.");
                        assertEquals(TaskType.TASK, type, "Не верно сохранен тип задачи 1.");
                        assertEquals(task1.getName(), attrFromString[2], "Не верно сохранено наименование задачи 1.");
                        assertEquals(task1.getStatus(), status, "Не верно сохранен статус задачи 1.");
                        assertEquals(task1.getDescription(), attrFromString[4], "Не верно сохранено описание задачи 1.");
                        break;
                    case 11:
                        assertEquals(task2.getId(), id, "Не верно сохранен id задачи 2.");
                        assertEquals(TaskType.TASK, type, "Не верно сохранен тип задачи 2.");
                        assertEquals(task2.getName(), attrFromString[2], "Не верно сохранено наименование задачи 2.");
                        assertEquals(task2.getStatus(), status, "Не верно сохранен статус задачи 2.");
                        assertEquals(task2.getDescription(), attrFromString[4], "Не верно сохранено описание задачи 2.");
                        break;
                    case 12:
                        assertEquals(task3.getId(), id, "Не верно сохранен id задачи 3.");
                        assertEquals(TaskType.TASK, type, "Не верно сохранен тип задачи 3.");
                        assertEquals(task3.getName(), attrFromString[2], "Не верно сохранено наименование задачи 3.");
                        assertEquals(task3.getStatus(), status, "Не верно сохранен статус задачи 3.");
                        assertEquals(task3.getDescription(), attrFromString[4], "Не верно сохранено описание задачи 3.");
                        assertEquals(task3.getDuration().toMinutes(), Integer.parseInt(attrFromString[6]), "Не верно сохранена продолжительность у задачи 3.");
                        assertEquals(task3.getStartTime(), LocalDateTime.parse(attrFromString[7]), "Не верно сохранены дата и время начала у задачи 3.");
                        break;
                    case 13:
                        assertEquals(task4.getId(), id, "Не верно сохранен id задачи 4.");
                        assertEquals(TaskType.TASK, type, "Не верно сохранен тип задачи 4.");
                        assertEquals(task4.getName(), attrFromString[2], "Не верно сохранено наименование задачи 4.");
                        assertEquals(task4.getStatus(), status, "Не верно сохранен статус задачи 4.");
                        assertEquals(task4.getDescription(), attrFromString[4], "Не верно сохранено описание задачи 4.");
                        assertEquals(task4.getDuration().toMinutes(), Integer.parseInt(attrFromString[6]), "Не верно сохранена продолжительность у задачи 4.");
                        assertEquals(task4.getStartTime(), LocalDateTime.parse(attrFromString[7]), "Не верно сохранены дата и время начала у задачи 4.");
                        break;
                }
            }
            assertEquals(13, counter, "В файле неверное количество заполненных строк.");
            assertEquals(LocalDateTime.of(2024, 5, 20, 21, 0), epic1.getStartTime(), "Неверное значение расчетной даты начала выполнения эпика 1.");
            assertEquals(LocalDateTime.of(2024, 5, 20, 22, 0), epic1.getEndTime(), "Неверное значение расчетной даты окончания выполнения эпика 1.");
            assertEquals(60, epic1.getDuration().toMinutes(), "Неверное значение расчетной продолжительности выполнения эпика 1.");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            e.printStackTrace();
        }
    }

    @Test
    void checkLoadFromFile() {
        FileBackedTaskManager managerBacked2 = FileBackedTaskManager.loadFromFile(testFileTasks);
        List<Epic> managerBackedEpics = taskManager.getAllEpics();
        List<Epic> managerBacked2Epics = managerBacked2.getAllEpics();

        List<Subtask> managerBackedSubtasks = taskManager.getAllSubtasks();
        List<Subtask> managerBacked2Subtasks = managerBacked2.getAllSubtasks();

        List<Task> managerBackedTasks = taskManager.getAllTasks();
        List<Task> managerBacked2Tasks = managerBacked2.getAllTasks();

        assertTrue(managerBacked2Epics.containsAll(managerBackedEpics), "Эпик(и) менеджера 1 не найдены в списке эпиков менеджера 2");
        assertTrue(managerBacked2Subtasks.containsAll(managerBackedSubtasks), "Подзадача(и) менеджера 1 не найдена(ы) в списке подзадач менеджера 2");
        assertTrue(managerBacked2Tasks.containsAll(managerBackedTasks), "Задача(и) менеджера 1 не найдена(ы) в списке задач менеджера 2");

        Epic epic1test = managerBacked2.getEpic(epic1.getId());
        assertEquals(epic1.getId(), epic1test.getId(), "Не верно загружен id эпика 1.");
        assertEquals(epic1.getName(), epic1test.getName(), "Не верно загружено наименование эпика 1.");
        assertEquals(epic1.getStatus(), epic1test.getStatus(), "Не верно загружен статус эпика 1.");
        assertEquals(epic1.getDescription(), epic1test.getDescription(), "Не верно загружено описание эпика 1.");
        assertTrue(epic1.getSubtaskList().containsAll(epic1test.getSubtaskList()), "Подзадача(и) эпика 1 менеджера 2 не найдена(ы) в списке подзадач эпика 1 менеджера 1");

        Epic epic2test = managerBacked2.getEpic(epic2.getId());
        assertEquals(epic2.getId(), epic2test.getId(), "Не верно загружен id эпика 2.");
        assertEquals(epic2.getName(), epic2test.getName(), "Не верно загружено наименование эпика 2.");
        assertEquals(epic2.getStatus(), epic2test.getStatus(), "Не верно загружен статус эпика 2.");
        assertEquals(epic2.getDescription(), epic2test.getDescription(), "Не верно загружено описание эпика 2.");
        assertTrue(epic2.getSubtaskList().containsAll(epic2test.getSubtaskList()), "Подзадача(и) эпика 2 менеджера 2 не найдена(ы) в списке подзадач эпика 2 менеджера 1");

        Subtask subtask11test = managerBacked2.getSubtask(subtask11.getId());
        assertEquals(subtask11.getId(), subtask11test.getId(), "Не верно загружен id подзадачи 1_1.");
        assertEquals(subtask11.getName(), subtask11test.getName(), "Не верно загружено наименование подзадачи 1_1.");
        assertEquals(subtask11.getStatus(), subtask11test.getStatus(), "Не верно загружен статус подзадачи 1_1.");
        assertEquals(subtask11.getDescription(), subtask11test.getDescription(), "Не верно загружено описание подзадачи 1_1.");
        assertEquals(subtask11.getEpicId(), subtask11test.getEpicId(), "Не верно загружен id эпика подзадачи 1_1.");

        Subtask subtask12test = managerBacked2.getSubtask(subtask12.getId());
        assertEquals(subtask12.getId(), subtask12test.getId(), "Не верно загружен id подзадачи 1_2.");
        assertEquals(subtask12.getName(), subtask12test.getName(), "Не верно загружено наименование подзадачи 1_2.");
        assertEquals(subtask12.getStatus(), subtask12test.getStatus(), "Не верно загружен статус подзадачи 1_2.");
        assertEquals(subtask12.getDescription(), subtask12test.getDescription(), "Не верно загружено описание подзадачи 1_2.");
        assertEquals(subtask12.getEpicId(), subtask12test.getEpicId(), "Не верно загружен id эпика подзадачи 1_2.");

        Subtask subtask13test = managerBacked2.getSubtask(subtask13.getId());
        assertEquals(subtask13.getId(), subtask13test.getId(), "Не верно загружен id подзадачи 1_3.");
        assertEquals(subtask13.getName(), subtask13test.getName(), "Не верно загружено наименование подзадачи 1_3.");
        assertEquals(subtask13.getStatus(), subtask13test.getStatus(), "Не верно загружен статус подзадачи 1_3.");
        assertEquals(subtask13.getDescription(), subtask13test.getDescription(), "Не верно загружено описание подзадачи 1_3.");
        assertEquals(subtask13.getEpicId(), subtask13test.getEpicId(), "Не верно загружен id эпика подзадачи 1_3.");

        Subtask subtask14test = managerBacked2.getSubtask(subtask14.getId());
        assertEquals(subtask14.getId(), subtask14test.getId(), "Не верно загружен id подзадачи 1_4.");
        assertEquals(subtask14.getName(), subtask14test.getName(), "Не верно загружено наименование подзадачи 1_4.");
        assertEquals(subtask14.getStatus(), subtask14test.getStatus(), "Не верно загружен статус подзадачи 1_4.");
        assertEquals(subtask14.getDescription(), subtask14test.getDescription(), "Не верно загружено описание подзадачи 1_4.");
        assertEquals(subtask14.getEpicId(), subtask14test.getEpicId(), "Не верно загружен id эпика подзадачи 1_4.");

        Subtask subtask15test = managerBacked2.getSubtask(subtask15.getId());
        assertEquals(subtask15.getId(), subtask15test.getId(), "Не верно загружен id подзадачи 1_5.");
        assertEquals(subtask15.getName(), subtask15test.getName(), "Не верно загружено наименование подзадачи 1_5.");
        assertEquals(subtask15.getStatus(), subtask15test.getStatus(), "Не верно загружен статус подзадачи 1_5.");
        assertEquals(subtask15.getDescription(), subtask15test.getDescription(), "Не верно загружено описание подзадачи 1_5.");
        assertEquals(subtask15.getEpicId(), subtask15test.getEpicId(), "Не верно загружен id эпика подзадачи 1_5.");

        Subtask subtask21test = managerBacked2.getSubtask(subtask21.getId());
        assertEquals(subtask21.getId(), subtask21test.getId(), "Не верно загружен id подзадачи 2_1.");
        assertEquals(subtask21.getName(), subtask21test.getName(), "Не верно загружено наименование подзадачи 2_1.");
        assertEquals(subtask21.getStatus(), subtask21test.getStatus(), "Не верно загружен статус подзадачи 2_1.");
        assertEquals(subtask21.getDescription(), subtask21test.getDescription(), "Не верно загружено описание подзадачи 2_1.");
        assertEquals(subtask21.getEpicId(), subtask21test.getEpicId(), "Не верно загружен id эпика подзадачи 2_1.");

        Task task1test = managerBacked2.getTask(task1.getId());
        assertEquals(task1.getId(), task1test.getId(), "Не верно загружен id задачи 1.");
        assertEquals(task1.getName(), task1test.getName(), "Не верно загружено наименование задачи 1.");
        assertEquals(task1.getStatus(), task1test.getStatus(), "Не верно загружен статус задачи 1.");
        assertEquals(task1.getDescription(), task1test.getDescription(), "Не верно загружено описание задачи 1.");

        Task task2test = managerBacked2.getTask(task2.getId());
        assertEquals(task2.getId(), task2test.getId(), "Не верно загружен id задачи 2.");
        assertEquals(task2.getName(), task2test.getName(), "Не верно загружено наименование задачи 2.");
        assertEquals(task2.getStatus(), task2test.getStatus(), "Не верно загружен статус задачи 2.");
        assertEquals(task2.getDescription(), task2test.getDescription(), "Не верно загружено описание задачи 2.");

        Task task3test = managerBacked2.getTask(task3.getId());
        assertEquals(task3.getId(), task3test.getId(), "Не верно загружен id задачи 3.");
        assertEquals(task3.getName(), task3test.getName(), "Не верно загружено наименование задачи 3.");
        assertEquals(task3.getStatus(), task3test.getStatus(), "Не верно загружен статус задачи 3.");
        assertEquals(task3.getDescription(), task3test.getDescription(), "Не верно загружено описание задачи 3.");

        Task task4test = managerBacked2.getTask(task4.getId());
        assertEquals(task4.getId(), task4test.getId(), "Не верно загружен id задачи 4.");
        assertEquals(task4.getName(), task4test.getName(), "Не верно загружено наименование задачи 4.");
        assertEquals(task4.getStatus(), task4test.getStatus(), "Не верно загружен статус задачи 4.");
        assertEquals(task4.getDescription(), task4test.getDescription(), "Не верно загружено описание задачи 4.");
    }
}
