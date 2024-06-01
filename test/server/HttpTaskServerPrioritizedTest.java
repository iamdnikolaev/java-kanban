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
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerPrioritizedTest {
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
    public void getPrioritized() throws IOException, InterruptedException {
        Epic epicInManager = manager.createEpic(new Epic("Эпик для подзадач",
                "Тестовый эпик для подзадач"));

        Subtask subtask = manager.createSubtask(new Subtask("Подзадача 1",
                "Тестовая подзадача 1 для проверки приоритетов", epicInManager.getId(),
                5L, LocalDateTime.now()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2",
                "Тестовая подзадача 2 для проверки приоритетов", epicInManager.getId(),
                5L, LocalDateTime.now().minusMinutes(15)));
        Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача 3",
                "Тестовая подзадача 3 для проверки приоритетов", epicInManager.getId(),
                5L, LocalDateTime.now().minusMinutes(9)));

        Task task = manager.createTask(new Task("Задача 1",
                "Тестовая задача 1 для проверки приоритетов", TaskStatus.NEW, 5L, LocalDateTime.now().plusMinutes(15)));
        Task task2 = manager.createTask(new Task("Задача 2",
                "Тестовая задача 2 для проверки приоритетов", TaskStatus.NEW, 5L, LocalDateTime.now().plusMinutes(10)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код статуса в ответе");

        List<Task> parsedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(5, parsedList.size(), "Получено неверное количесто записей по приоритетам");
        assertEquals(subtask2.getId(), parsedList.getFirst().getId(), 
                "Неверный id самой старой задачи(подзадачи) в списке по приоритетам");
        assertEquals(subtask2.getName(), parsedList.getFirst().getName(), 
                "Неверное наименование самой старой задачи(подзадачи) в списке по приоритетам");

        assertEquals(task.getId(), parsedList.getLast().getId(),
                "Неверный id самой свежей задачи(подзадачи) в списке по приоритетам");
        assertEquals(task.getName(), parsedList.getLast().getName(),
                "Неверное наименование свежей задачи(подзадачи) в списке по приоритетам");
    }
}
