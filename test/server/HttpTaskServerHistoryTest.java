package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerHistoryTest {
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
    public void getHistory() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task("Задача 1 для истории",
                "Тестовая задача 1 для проверки истории"));
        Task task2 = manager.createTask(new Task("Задача 2 для получения",
                "Тестовая задача 2 для проверки истории"));

        Epic epic1 = manager.createEpic(new Epic("Эпик 1 для истории",
                "Тестовый эпик 1 для проверки истории"));

        Subtask subtask11 = manager.createSubtask(new Subtask("Подзадача 1_1 для истории",
                "Тестовая подзадача 1_1 для проверки истории", epic1.getId()));
        Subtask subtask12 = manager.createSubtask(new Subtask("Подзадача 1_2 для истории",
                "Тестовая подзадача 1_2 для проверки истории", epic1.getId()));

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask11.getId());
        manager.getSubtask(subtask12.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(5, parsedList.size(),
                "Получено неверное количесто записей в линейном просмотре истории");
        assertEquals(task1.getId(), parsedList.getFirst().getId(),
                "Первым просмотренным элементом должна быть Задача 1");
        assertEquals(task1.getName(), parsedList.getFirst().getName(),
                "Неверное наименование первого просмотренного элемента");

        assertEquals(subtask12.getId(), parsedList.getLast().getId(),
                "Не совпали id последней в просмотре в истории Подзадачи 1_2");
        assertEquals(subtask12.getName(), parsedList.getLast().getName(),
                "Не совпали название последней в истории Подзадачи 1_2");

        // Запросим(Просмотрим) еще раз для нелинейности в истории
        manager.getTask(task1.getId()); // Первым просмотренным элементом становится Задача 2
        manager.getEpic(epic1.getId()); // Последним просмотренным элементом становится Эпик 1

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(5, parsedList.size(),
                "Получено неверное количесто записей в нелинейном просмотре истории");
        assertEquals(task2.getId(), parsedList.getFirst().getId(),
                "Первым просмотренным элементом должна быть Задача 2");
        assertEquals(task2.getName(), parsedList.getFirst().getName(),
                "Неверное наименование первого просмотренного элемента");

        assertEquals(epic1.getId(), parsedList.getLast().getId(),
                "Последним просмотренным элементом должен быть Эпик 1");
        assertEquals(epic1.getName(), parsedList.getLast().getName(),
                "Неверное наименование последнего просмотренного элемента");
    }
}
