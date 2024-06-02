package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.NotFoundException;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    @BeforeEach
    public void setUp() {
        manager.clearAllTasks();
        manager.clearAllSubtasks();
        manager.clearAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        Task taskInManager = manager.createTask(new Task("Задача 1 для получения",
                "Тестовая задача 1 для проверки получения", TaskStatus.NEW, 5L, LocalDateTime.now()));
        Task taskInManager2 = manager.createTask(new Task("Задача 2 для получения",
                "Тестовая задача 2 для проверки получения", TaskStatus.IN_PROGRESS, 5L, LocalDateTime.now().plusMinutes(10)));
        Task taskInManager3 = manager.createTask(new Task("Задача 3 для получения",
                "Тестовая задача 3 для проверки получения", TaskStatus.DONE, 5L, LocalDateTime.now().plusMinutes(15)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(3, parsedList.size(), "Получено неверное количесто записей о задачах");
        assertEquals(taskInManager.getId(), parsedList.getFirst().getId(), "Не совпали id первых задач");
        assertEquals(taskInManager.getName(), parsedList.getFirst().getName(), "Не совпали названия первых задач");

        assertEquals(taskInManager3.getId(), parsedList.getLast().getId(), "Не совпали id последних задач");
        assertEquals(taskInManager3.getName(), parsedList.getLast().getName(), "Не совпали названия последних задач");
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        Task taskInManager = manager.createTask(new Task("Задача 1 для получения",
                "Тестовая задача 1 для проверки получения", TaskStatus.NEW, 5L, LocalDateTime.now()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskInManager.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertNotNull(jsonObject, "В ответе не распознан JSON-объект");

        String name = jsonObject.get("name").getAsString();
        assertEquals(taskInManager.getName(), name, "Некорректное имя задачи");

        int id = jsonObject.get("id").getAsInt();
        assertEquals(taskInManager.getId(), id, "Некорректный id задачи");
    }

    @Test
    public void getTaskFail() throws IOException, InterruptedException {
        Task taskInManager = manager.createTask(new Task("Задача 1 для получения",
                "Тестовая задача 1 для проверки получения", TaskStatus.NEW, 5L, LocalDateTime.now()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + (taskInManager.getId() + 1234));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код статуса в ответе");
    }

    @Test
    public void addTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1 для добавления", "Тестовая задача 1 для проверки добавления",
                TaskStatus.NEW, 5L, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1 для добавления", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void addTaskFail() throws IOException, InterruptedException {
        Task taskInManager = manager.createTask(new Task("Задача-пятиминутка",
                "Тестовая задача для проверки пересечения времени выполнения",
                TaskStatus.NEW, 5L, LocalDateTime.now()));

        Task task = new Task("Задача с ошибкой добавления", "Тестовая задача с ошибкой по времени выполнения",
                TaskStatus.NEW, 10L, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неверный код статуса в ответе");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1 для изменения", "Тестовая задача 1 для проверки изменения",
                TaskStatus.NEW, 5L, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе после добавления");

        List<Task> tasksFromManager = manager.getAllTasks();
        int id = tasksFromManager.getFirst().getId();
        Task taskNew = new Task("Задача 1 ИЗМЕНЕНА", "Тестовая задача 1 ИЗМЕНЕНА",
                id, TaskStatus.DONE, task.getDuration().toMinutes(), task.getStartTime());
        taskJson = gson.toJson(taskNew);

        url = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе после изменения");

        tasksFromManager = manager.getAllTasks();
        assertEquals(taskNew.getName(), tasksFromManager.getFirst().getName(),
                "Неверно сохранено название");
        assertEquals(taskNew.getDescription(), tasksFromManager.getFirst().getDescription(),
                "Неверно сохранено описание");
        assertEquals(taskNew.getStatus(), tasksFromManager.getFirst().getStatus(), "Неверно сохранен статус задачи");
    }

    @Test
    public void updateTaskFail() throws IOException, InterruptedException {
        Task taskInManager = manager.createTask(new Task("Задача-пятиминутка",
                "Тестовая задача для проверки пересечения времени выполнения при изменении",
                TaskStatus.NEW, 5L, LocalDateTime.now()));

        Task taskInManager2 = manager.createTask(new Task("Задача-пятиминутка2",
                "Тестовая задача для проверки пересечения времени выполнения",
                TaskStatus.NEW, 5L, LocalDateTime.now().plusMinutes(5)));

        List<Task> tasksFromManager = manager.getAllTasks();
        int id = tasksFromManager.getLast().getId();
        Task taskLast = manager.getTask(id);
        Task taskNew = new Task(taskLast.getName(), taskLast.getDescription(),
                id, TaskStatus.DONE, taskLast.getDuration().toMinutes(), LocalDateTime.now());
        String taskJson = gson.toJson(taskNew);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неверный код статуса в ответе после изменения с пересечением дат");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task taskInManager = manager.createTask(new Task("Задача 1 для удаления",
                "Тестовая задача 1 для проверки удаления", TaskStatus.NEW, 5L, LocalDateTime.now()));
        int id = taskInManager.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");
        assertThrows(NotFoundException.class, () -> {manager.getTask(id);}, "Задача не удалена в менеджере");
    }
}