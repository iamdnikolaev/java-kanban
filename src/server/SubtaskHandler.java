package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InvalidTaskException;
import manager.NotFoundException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Обработчик эндпоинта по подзадачам трекера
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    /**
     * Конструктор
     *
     * @param taskManager назначенный диспетчер задач
     * @param gson        назначенный парсер
     */
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
            Optional<Integer> reqSubtaskIdOpt = getTaskId(exchange);
            int reqSubtaskId = 0;
            if (reqSubtaskIdOpt.isPresent()) {
                reqSubtaskId = reqSubtaskIdOpt.get();
            }

            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET" -> {
                    String responseText = "";
                    if (reqSubtaskId > 0) {
                        responseText = gson.toJson(taskManager.getSubtask(reqSubtaskId));
                    } else {
                        responseText = gson.toJson(taskManager.getAllSubtasks());
                    }
                    sendText(exchange, responseText);
                }
                case "DELETE" -> {
                    taskManager.removeSubtask(reqSubtaskId);
                    sendText(exchange, "Подзадача удалена.");
                }
                case "POST" -> {
                    String taskJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask reqSubtask = gson.fromJson(taskJson, Subtask.class);
                    if (reqSubtask == null) {
                        throw new NotFoundException("Не передана подзадача для обработки.");
                    } else {
                        if (reqSubtask.getId() > 0) {
                            taskManager.updateSubtask(reqSubtask);
                            sendConfirm(exchange, "Подзадача обновлена.");
                        } else {
                            taskManager.createSubtask(reqSubtask);
                            sendConfirm(exchange, "Подзадача добавлена.");
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
