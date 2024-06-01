package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InvalidTaskException;
import manager.NotFoundException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Обработчик эндпоинта по задачам трекера
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    /**
     * Конструктор
     *
     * @param taskManager назначенный диспетчер задач
     * @param gson        назначенный парсер
     */
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    /**
     * Метод обработки
     *
     * @param exchange объект обмена обслуживаемого запроса
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> reqTaskIdOpt = getTaskId(exchange);
            int reqTaskId = 0;
            if (reqTaskIdOpt.isPresent()) {
                reqTaskId = reqTaskIdOpt.get();
            }

            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET" -> {
                    String responseText = "";
                    if (reqTaskId > 0) {
                        responseText = gson.toJson(taskManager.getTask(reqTaskId));
                    } else {
                        responseText = gson.toJson(taskManager.getAllTasks());
                    }
                    sendText(exchange, responseText);
                }
                case "DELETE" -> {
                    taskManager.removeTask(reqTaskId);
                    sendText(exchange, "Задача удалена.");
                }
                case "POST" -> {
                    String taskJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task reqTask = gson.fromJson(taskJson, Task.class);
                    if (reqTask == null) {
                        throw new NotFoundException("Не передана задача для обработки.");
                    } else {
                        if (reqTask.getId() > 0) {
                            taskManager.updateTask(reqTask);
                            sendConfirm(exchange, "Задача обновлена.");
                        } else {
                            taskManager.createTask(reqTask);
                            sendConfirm(exchange, "Задача добавлена.");
                        }
                    }
                }
                default -> sendNotFound(exchange, "Некорректный эндпоинт.");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (InvalidTaskException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange, e.getMessage());
        } finally {
            exchange.close();
        }
    }
}
