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

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerSubtasksTest {

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
    public void getSubtasks() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения",
                "Тестовый эпик 1 для проверки получения"));
        Subtask subtaskInManager = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(), 5L, LocalDateTime.now()));
        Subtask subtaskInManager2 = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(), 5L,
                LocalDateTime.now().plusMinutes(10)));
        Subtask subtaskInManager3 = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(), 5L,
                LocalDateTime.now().plusMinutes(15)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(3, parsedList.size(), "Получено неверное количесто записей о подзадачах");
        assertEquals(subtaskInManager.getId(), parsedList.getFirst().getId(), "Не совпали id первых подзадач");
        assertEquals(subtaskInManager.getName(), parsedList.getFirst().getName(), "Не совпали названия первых подзадач");

        assertEquals(subtaskInManager3.getId(), parsedList.getLast().getId(), "Не совпали id последних подзадач");
        assertEquals(subtaskInManager3.getName(), parsedList.getLast().getName(), "Не совпали названия последних подзадач");
    }

    @Test
    public void getSubtask() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения",
                "Тестовый эпик 1 для проверки получения"));
        Subtask subtaskInManager = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(), 5L, LocalDateTime.now()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskInManager.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertNotNull(jsonObject, "В ответе не распознан JSON-объект");

        String name = jsonObject.get("name").getAsString();
        assertEquals(subtaskInManager.getName(), name, "Некорректное имя подзадачи");

        int id = jsonObject.get("id").getAsInt();
        assertEquals(subtaskInManager.getId(), id, "Некорректный id подзадачи");
    }

    @Test
    public void getSubtaskFail() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения",
                "Тестовый эпик 1 для проверки получения"));
        Subtask subtaskInManager = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(), 5L, LocalDateTime.now()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + (subtaskInManager.getId() + 1234));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код статуса в ответе");
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для добавления",
                "Тестовый эпик 1 для проверки добавления"));
        Subtask subtask = new Subtask("Подзадача 1 для добавления",
                "Тестовая подзадача 1 для проверки добавления", epicInManager.getId(),
                5L, LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе");

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1 для добавления", subtasksFromManager.getFirst().getName(),
                "Некорректное имя подзадачи");
    }

    @Test
    public void addSubtaskFail() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для добавления",
                "Тестовый эпик 1 для проверки добавления"));
        Subtask subtaskInManager = manager.createSubtask(new Subtask("Подзадача 1 добавлена",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(), 5L, LocalDateTime.now()));

        Subtask subtask = new Subtask("Подзадача с ошибкой добавления", "Тестовая подзадача с ошибкой по времени выполнения",
                epicInManager.getId(), 10L, LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неверный код статуса в ответе");
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для изменения",
                "Тестовый эпик 1 для проверки изменения"));
        Subtask subtask = new Subtask("Подзадача 1 для изменения", "Тестовая подзадача 1 для проверки изменения",
                epicInManager.getId(), 5L, LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе после добавления");

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        int id = subtasksFromManager.getFirst().getId();
        Subtask subtaskNew = new Subtask("Подзадача 1 ИЗМЕНЕНА", "Тестовая подзадача 1 ИЗМЕНЕНА",
                id, TaskStatus.DONE, epicInManager.getId(), subtask.getDuration().toMinutes(), subtask.getStartTime());
        subtaskJson = gson.toJson(subtaskNew);

        url = URI.create("http://localhost:8080/subtasks/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе после изменения");

        subtasksFromManager = manager.getAllSubtasks();
        assertEquals(subtaskNew.getName(), subtasksFromManager.getFirst().getName(),
                "Неверно сохранено название");
        assertEquals(subtaskNew.getDescription(), subtasksFromManager.getFirst().getDescription(),
                "Неверно сохранено описание");
        assertEquals(subtaskNew.getStatus(), subtasksFromManager.getFirst().getStatus(), "Неверно сохранен статус подзадачи");
    }

    @Test
    public void updateSubtaskFail() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для изменения",
                "Тестовый эпик 1 для проверки изменения"));
        Subtask subtaskInManager = manager.createSubtask(new Subtask("Подзадача-пятиминутка",
                "Тестовая подзадача для проверки пересечения времени выполнения при изменении",
                epicInManager.getId(), 5L, LocalDateTime.now()));

        Subtask subtaskInManager2 = manager.createSubtask(new Subtask("Подзадача-пятиминутка2",
                "Тестовая подзадача для проверки пересечения времени выполнения",
                epicInManager.getId(), 5L, LocalDateTime.now().plusMinutes(5)));

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        int id = subtasksFromManager.getLast().getId();
        Subtask subtaskLast = manager.getSubtask(id);
        Subtask subtaskNew = new Subtask(subtaskLast.getName(), subtaskLast.getDescription(),
                id, TaskStatus.DONE, epicInManager.getId(), subtaskLast.getDuration().toMinutes(), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtaskNew);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неверный код статуса в ответе после изменения с пересечением дат");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для удаления",
                "Тестовый эпик 1 для проверки удаления"));
        Subtask subtaskInManager = manager.createSubtask(new Subtask("Подзадача 1 для удаления",
                "Тестовая подзадача 1 для проверки удаления", epicInManager.getId(), 5L, LocalDateTime.now()));
        int id = subtaskInManager.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");
        assertThrows(NotFoundException.class, () -> {manager.getSubtask(id);}, "Подзадача не удалена в менеджере");
    }
}