public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", taskManager.getNextId());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", taskManager.getNextId());
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", taskManager.getNextId());
        taskManager.createEpic(epic1);
        Subtask subtask11 = new Subtask("Подзадача 1_1", "Описание подзадачи 1_1", taskManager.getNextId(), epic1.getId());
        taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Подзадача 1_2", "Описание подзадачи 1_2", taskManager.getNextId(), epic1.getId());
        taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", taskManager.getNextId());
        taskManager.createEpic(epic2);
        Subtask subtask21 = new Subtask("Подзадача 2_1", "Описание подзадачи 2_1", taskManager.getNextId(), epic2.getId());
        taskManager.createSubtask(subtask21);

        System.out.println("\nИсходное состояние объектов:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        taskManager.updateTask(new Task(task1.name, task1.description, task1.getId(), TaskStatus.IN_PROGRESS));
        taskManager.updateTask(new Task(task2.name, task2.description, task2.getId(), TaskStatus.DONE));
        taskManager.updateSubtask(new Subtask(subtask12.name, subtask12.description, subtask12.getId(), TaskStatus.IN_PROGRESS, subtask12.getEpicId()));
        taskManager.updateSubtask(new Subtask(subtask21.name, subtask21.description, subtask21.getId(), TaskStatus.DONE, subtask21.getEpicId()));

        System.out.println("\nСостояние объектов после смены статусов:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic2.getId());

        System.out.println("\nСостояние объектов после удаления:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
    }
}
