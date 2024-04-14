import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1 = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask11 = new Subtask("Подзадача 1_1", "Описание подзадачи 1_1", epic1.getId());
        taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2", "Описание подзадачи 1_2", epic1.getId());
        subtask12 = taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic2 = taskManager.createEpic(epic2);
        Subtask subtask21 = new Subtask("Подзадача 2_1", "Описание подзадачи 2_1", epic2.getId());
        subtask21 = taskManager.createSubtask(subtask21);

        System.out.println("\n==== Исходное состояние объектов ====");
        printAllTasks(taskManager);

        taskManager.updateTask(new Task(task1.getName(), task1.getDescription(), task1.getId(), TaskStatus.IN_PROGRESS));
        taskManager.updateTask(new Task(task2.getName(), task2.getDescription(), task2.getId(), TaskStatus.DONE));
        taskManager.updateSubtask(new Subtask(subtask12.getName(), subtask12.getDescription(), subtask12.getId(), TaskStatus.IN_PROGRESS, subtask12.getEpicId()));
        taskManager.updateSubtask(new Subtask(subtask21.getName(), subtask21.getDescription(), subtask21.getId(), TaskStatus.DONE, subtask21.getEpicId()));

        System.out.println("\n==== Состояние объектов после смены статусов ====");
        printAllTasks(taskManager);

        taskManager.getEpic(epic2.getId()); // Просмотр ради записи в историю

        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic2.getId());

        System.out.println("\n==== Состояние объектов после удаления ====");
        printAllTasks(taskManager);

        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getSubtask(subtask12.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask12.getId());

        System.out.println("\n==== Состояние объектов после просмотра еще 11 раз ====");
        printAllTasks(taskManager);

        System.out.println("\n\n==== Дополнительное задание ====");
        taskManager = Managers.getDefault();

        Task task3 = new Task("Задача 3", "Описание задачи 3");
        task3 = taskManager.createTask(task3);
        Task task4 = new Task("Задача 4", "Описание задачи 4");
        task4 = taskManager.createTask(task4);

        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        epic3 = taskManager.createEpic(epic3);
        Subtask subtask31 = new Subtask("Подзадача 3_1", "Описание подзадачи 3_1", epic3.getId());
        subtask31 = taskManager.createSubtask(subtask31);
        Subtask subtask32 = new Subtask("Подзадача 3_2", "Описание подзадачи 3_2", epic3.getId());
        subtask32 = taskManager.createSubtask(subtask32);
        Subtask subtask33 = new Subtask("Подзадача 3_3", "Описание подзадачи 3_3", epic3.getId());
        subtask33 = taskManager.createSubtask(subtask33);

        Epic epic4 = new Epic("Эпик 4 без подзадач", "Описание эпика 4 без подзадач");
        epic4 = taskManager.createEpic(epic4);

        System.out.println("Запрос task3");
        taskManager.getTask(task3.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос task4");
        taskManager.getTask(task4.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос task3 снова");
        taskManager.getTask(task3.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос task4 снова");
        taskManager.getTask(task4.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос epic3");
        taskManager.getEpic(epic3.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос epic4");
        taskManager.getEpic(epic4.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос epic3 снова");
        taskManager.getEpic(epic3.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос subtask31");
        taskManager.getSubtask(subtask31.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос subtask32");
        taskManager.getSubtask(subtask32.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nЗапрос subtask33");
        taskManager.getSubtask(subtask33.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nУдалили задачу 3");
        taskManager.removeTask(task3.getId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("\nУдалили эпик 3");
        taskManager.removeEpic(epic3.getId());
        System.out.println("История: " + taskManager.getHistory());
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}
