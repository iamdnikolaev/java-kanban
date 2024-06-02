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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerEpicsTest {

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
    public void getEpics() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения",
                "Тестовый эпик 1 для проверки получения"));
        Epic epicInManager2 = manager.createEpic(new Epic("Эпик 2 для получения",
                "Тестовый эпик 2 для проверки получения"));
        Epic epicInManager3 = manager.createEpic(new Epic("Эпик 3 для получения",
                "Тестовый эпик 3 для проверки получения"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(3, parsedList.size(), "Получено неверное количесто записей об эпиках");
        assertEquals(epicInManager.getId(), parsedList.getFirst().getId(), "Не совпали id первых эпиков");
        assertEquals(epicInManager.getName(), parsedList.getFirst().getName(), "Не совпали названия первых эпиков");

        assertEquals(epicInManager3.getId(), parsedList.getLast().getId(), "Не совпали id последних эпиков");
        assertEquals(epicInManager3.getName(), parsedList.getLast().getName(), "Не совпали названия последних эпиков");
    }

    @Test
    public void getEpic() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения",
                "Тестовый эпик 1 для проверки получения"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicInManager.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertNotNull(jsonObject, "В ответе не распознан JSON-объект");

        String name = jsonObject.get("name").getAsString();
        assertEquals(epicInManager.getName(), name, "Некорректное имя эпика");

        int id = jsonObject.get("id").getAsInt();
        assertEquals(epicInManager.getId(), id, "Некорректный id эпика");
    }

    @Test
    public void getEpicFail() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения",
                "Тестовый эпик 1 для проверки получения"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + (epicInManager.getId() + 1234));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код статуса в ответе");
    }

    @Test
    public void getEpicSubtasks() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения подзадач",
                "Тестовый эпик 1 для проверки получения подзадач"));

        Subtask subtask = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(),
                5L, LocalDateTime.now()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2 для получения",
                "Тестовая подзадача 2 для проверки получения", epicInManager.getId(),
                5L, LocalDateTime.now().minusMinutes(15)));
        Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача 3 для получения",
                "Тестовая подзадача 3 для проверки получения", epicInManager.getId(),
                5L, LocalDateTime.now().minusMinutes(9)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicInManager.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(3, parsedList.size(), "Получено неверное количесто записей о подзадачах");
        assertEquals(subtask.getId(), parsedList.getFirst().getId(), "Не совпали id первых подзадач");
        assertEquals(subtask.getName(), parsedList.getFirst().getName(), "Не совпали названия первых подзадач");

        assertEquals(subtask3.getId(), parsedList.getLast().getId(), "Не совпали id последних подзадач");
        assertEquals(subtask3.getName(), parsedList.getLast().getName(), "Не совпали названия последних подзадач");

        assertEquals(subtask2.getStartTime(), epicInManager.getStartTime(),
                "Неверная дата/время начала эпика - должно взяться с подзадачи 2");
        assertEquals(15, epicInManager.getDuration().toMinutes(),
                "Неверная продолжительность эпика - должно взяться в сумме с подзадач (15 минут)");
        assertEquals(subtask.getEndTime(), epicInManager.getEndTime(),
                "Неверная дата/время окончания обработки эпика - должно взяться с подзадачи 1");
    }

    @Test
    public void getEpicSubtasksFail() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для получения подзадач",
                "Тестовый эпик 1 для проверки получения подзадач"));

        Subtask subtask = manager.createSubtask(new Subtask("Подзадача 1 для получения",
                "Тестовая подзадача 1 для проверки получения", epicInManager.getId(),
                5L, LocalDateTime.now()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + (epicInManager.getId() + 1234) + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код статуса в ответе");
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1 для добавления",
                "Тестовый эпик 1 для проверки добавления");

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик 1 для добавления", epicsFromManager.getFirst().getName(),
                "Некорректное имя эпика");
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1 для изменения",
                "Тестовый эпик 1 для проверки изменения");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе после добавления");

        List<Epic> epicsFromManager = manager.getAllEpics();
        int id = epicsFromManager.getFirst().getId();
        Epic epicNew = new Epic("Эпик 1 ИЗМЕНЕН", "Тестовый эпик 1 ИЗМЕНЕН", id);
        epicJson = gson.toJson(epicNew);

        url = URI.create("http://localhost:8080/epics/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код статуса в ответе после изменения");

        epicsFromManager = manager.getAllEpics();
        assertEquals(epicNew.getName(), epicsFromManager.getFirst().getName(),
                "Неверно сохранено название");
        assertEquals(epicNew.getDescription(), epicsFromManager.getFirst().getDescription(),
                "Неверно сохранено описание");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик 1 для удаления",
                "Тестовый эпик 1 для проверки удаления"));
        int id = epicInManager.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");
        assertThrows(NotFoundException.class, () -> {manager.getEpic(id);}, "Эпик не удален в менеджере");
    }
}