package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Менеджер задач с автосохранением в файл и восстановлением из него - потомок {@link InMemoryTaskManager}
 * с реализацией интерфейса {@link TaskManager}.
 *
 * @author Николаев Д.В.
 * @version 1.1
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    /**
     * Поле файл резервной копии.
     */
    private final File file;

    /**
     * Конструктор менеджера с возможностью автосохранения/восстановления.
     *
     * @param file файл резервной копии
     */
    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    /**
     * Метод сохранения текущего состояния менеджера в {@link FileBackedTaskManager#file}.
     */
    private void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String format = "id,type,name,status,description,epic,duration,startTime\n";
            fileWriter.write(format);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи формата файла.");
        }

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            for (Epic epic : getAllEpics()) {
                String epicLine = toString(epic);
                fileWriter.write(epicLine);
            }

            for (Subtask subtask : getAllSubtasks()) {
                String subtaskLine = toString(subtask);
                fileWriter.write(subtaskLine);
            }

            for (Task task : getAllTasks()) {
                String taskLine = toString(task);
                fileWriter.write(taskLine);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    /**
     * Метод формирования строки в CSV-формате с указанием значений полей задач/подзадач/эпиков.
     *
     * @param task объект для сохранения, учитывая полиморфизм подтипов {@link Task}
     * @return String строка в CSV
     */
    private String toString(Task task) {
        String commonAttr = task.getName() + "," + task.getStatus() + "," + task.getDescription();
        String timeAttr = "," + ((task.getDuration() != null) ? task.getDuration().toMinutes() : "") + "," +
                ((task.getStartTime() != null) ? task.getStartTime() : "");

        String result = "";
        switch (task.getClass().getName()) {
            case "task.Task":
                result = task.getId() + "," + TaskType.TASK + "," + commonAttr + "," + timeAttr + "\n";
                break;
            case "task.Subtask":
                Subtask subtask = (Subtask) task;
                result = task.getId() + "," + TaskType.SUBTASK + "," + commonAttr + "," + subtask.getEpicId() +
                        timeAttr + "\n";
                break;
            case "task.Epic":
                timeAttr = ",,";
                result = task.getId() + "," + TaskType.EPIC + "," + commonAttr + "," + timeAttr + "\n";
        }
        return result;
    }

    /**
     * Метод создания объекта задачи/подзадачи/эпика на основе переданной строки в CSV-формате.
     *
     * @param value строка в CSV со значениями полей
     * @return {@link Task} созданный объект
     */
    private Task fromString(String value) {
        String[] attrFromString = value.split(",");
        TaskStatus status = TaskStatus.valueOf(attrFromString[3]);
        TaskType type = TaskType.valueOf(attrFromString[1]);
        int id = Integer.parseInt(attrFromString[0]);

        Long duration = null;
        LocalDateTime startTime = null;
        Task result;
        switch (type) {
            case TaskType.TASK:
                if (attrFromString.length > 5) { //только непустые значения между ,, дают элемент массива
                    duration = Long.parseLong(attrFromString[6]);
                    startTime = LocalDateTime.parse(attrFromString[7]);
                }
                result = super.createTask(new Task(attrFromString[2], attrFromString[4], id, status, duration, startTime), true);
                break;
            case TaskType.SUBTASK:
                if (attrFromString.length > 6) {
                    duration = Long.parseLong(attrFromString[6]);
                    startTime = LocalDateTime.parse(attrFromString[7]);
                }
                result = super.createSubtask(new Subtask(attrFromString[2], attrFromString[4], id, status,
                        Integer.parseInt(attrFromString[5]), duration, startTime), true);
                break;
            case TaskType.EPIC:
                result = super.createEpic(new Epic(attrFromString[2], attrFromString[4], id), true);
                break;
            default:
                result = null;
        }
        return result;
    }

    /**
     * Метод создания объекта менеджера путем загрузки из заданного CSV-файла.
     *
     * @param file CSV-файл со значениями полей объектов менеджера
     * @return {@link FileBackedTaskManager} созданный менеджер
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            if (fileReader.ready()) {
                fileReader.readLine(); // Прочитали первую строку с форматом полей CSV
            }
            int idMax = 0;
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                Task task = fileBackedTaskManager.fromString(line);
                idMax = Integer.max(idMax, task.getId());
            }
            fileBackedTaskManager.idCounter = idMax;
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            e.printStackTrace();
        }

        return fileBackedTaskManager;
    }

    @Override
    public Task createTask(Task task) {
        Task taskResult = super.createTask(task);
        save();
        return taskResult;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask subtaskResult = super.createSubtask(subtask);
        save();
        return subtaskResult;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic epicResult = super.createEpic(epic);
        save();
        return epicResult;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    public static void main(String[] args) {
        try {
            File testFileTasks = File.createTempFile("testFileTasks", null);

            FileBackedTaskManager managerBacked = new FileBackedTaskManager(testFileTasks);
            // Проверяем создание менеджера из пустого файла
            FileBackedTaskManager managerBacked2 = FileBackedTaskManager.loadFromFile(testFileTasks);

            Task task1 = new Task("Задача 1", "Описание задачи 1");
            managerBacked.createTask(task1);
            Task task2 = new Task("Задача 2", "Описание задачи 2");
            managerBacked.createTask(task2);

            Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
            epic1 = managerBacked.createEpic(epic1);
            Subtask subtask11 = new Subtask("Подзадача 1_1", "Описание подзадачи 1_1", epic1.getId());
            managerBacked.createSubtask(subtask11);
            Subtask subtask12 = new Subtask("Подзадача 1_2", "Описание подзадачи 1_2", epic1.getId());
            managerBacked.createSubtask(subtask12);

            Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
            epic2 = managerBacked.createEpic(epic2);
            Subtask subtask21 = new Subtask("Подзадача 2_1", "Описание подзадачи 2_1", epic2.getId());
            subtask21 = managerBacked.createSubtask(subtask21);

            managerBacked.updateTask(new Task(task1.getName(), task1.getDescription(), task1.getId(), TaskStatus.IN_PROGRESS));
            managerBacked.updateTask(new Task(task2.getName(), task2.getDescription(), task2.getId(), TaskStatus.DONE));
            managerBacked.updateSubtask(new Subtask(subtask12.getName(), subtask12.getDescription(), subtask12.getId(), TaskStatus.IN_PROGRESS, subtask12.getEpicId()));
            managerBacked.updateSubtask(new Subtask(subtask21.getName(), subtask21.getDescription(), subtask21.getId(), TaskStatus.DONE, subtask21.getEpicId()));

            Task task3 = new Task("Задача 3", "Описание задачи 3", 5L,
                    LocalDateTime.of(2024, 5, 20, 19, 0));
            managerBacked.createTask(task3);

            Task task4 = new Task("Задача 4", "Описание задачи 4", 44L,
                    LocalDateTime.of(2024, 5, 20, 19, 5));
            managerBacked.createTask(task4);

            Subtask subtask15 = new Subtask("Подзадача 1_5", "Описание подзадачи 1_5", epic1.getId(),
                    10L, LocalDateTime.of(2024, 5, 20, 21, 50));
            managerBacked.createSubtask(subtask15);

            Subtask subtask14 = new Subtask("Подзадача 1_4", "Описание подзадачи 1_4", epic1.getId(),
                    20L, LocalDateTime.of(2024, 5, 20, 21, 15));
            managerBacked.createSubtask(subtask14);

            Subtask subtask13 = new Subtask("Подзадача 1_3", "Описание подзадачи 1_3", epic1.getId(),
                    15L, LocalDateTime.of(2024, 5, 20, 21, 0));
            managerBacked.createSubtask(subtask13);

            managerBacked.updateTask(new Task(task4.getName(), task4.getDescription(), task4.getId(), TaskStatus.DONE,
                    task4.getDuration().toMinutes(), task4.getStartTime()));

            managerBacked.updateTask(new Task(task3.getName(), task3.getDescription(), task3.getId(), task3.getStatus(),
                    null, null));

            managerBacked2 = FileBackedTaskManager.loadFromFile(testFileTasks);
            List<Epic> managerBackedEpics = managerBacked.getAllEpics();
            List<Epic> managerBacked2Epics = managerBacked2.getAllEpics();

            List<Subtask> managerBackedSubtasks = managerBacked.getAllSubtasks();
            List<Subtask> managerBacked2Subtasks = managerBacked2.getAllSubtasks();

            List<Task> managerBackedTasks = managerBacked.getAllTasks();
            List<Task> managerBacked2Tasks = managerBacked2.getAllTasks();

            System.out.println();
            if (managerBacked2Epics.containsAll(managerBackedEpics)) {
                System.out.println("Эпики менеджера 1 есть в списке эпиков менеджера 2.");
            } else {
                System.out.println("Есть несовпадение между эпиками менеджеров 1 и 2.");
            }

            if (managerBacked2Subtasks.containsAll(managerBackedSubtasks)) {
                System.out.println("Подзадачи менеджера 1 есть в списке подзадач менеджера 2.");
            } else {
                System.out.println("Есть несовпадение между подзадачами менеджеров 1 и 2.");
            }

            if (managerBacked2Tasks.containsAll(managerBackedTasks)) {
                System.out.println("Задачи менеджера 1 есть в списке задач менеджера 2.");
            } else {
                System.out.println("Есть несовпадение между задачами менеджеров 1 и 2.");
            }

            Task task5 = new Task("Задача 5", "Описание задачи 5", 5L,
                    LocalDateTime.of(2024, 5, 20, 19, 3));
            task5 = managerBacked.createTask(task5);

            Task task6 = new Task("Задача 6", "Описание задачи 6", 44L,
                    LocalDateTime.of(2024, 5, 20, 19, 5));
            task6 = managerBacked.createTask(task6);

            Subtask subtask16 = new Subtask("Подзадача 1_6", "Описание подзадачи 1_6", epic1.getId(),
                    10L, LocalDateTime.of(2024, 5, 20, 21, 55));
            subtask16 = managerBacked.createSubtask(subtask16);

        } catch (IOException e) {
            System.out.println("Произошла ошибка создания временного файла.");
            e.printStackTrace();
        }
    }
}