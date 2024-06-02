package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InvalidTaskException;
import manager.NotFoundException;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Обработчик эндпоинта по эпикам трекера
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    /**
     * Конструктор
     *
     * @param taskManager назначенный диспетчер задач
     * @param gson        назначенный парсер
     */
    public EpicHandler(TaskManager taskManager, Gson gson) {
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
            Optional<Integer> reqEpicIdOpt = getTaskId(exchange);
            int reqEpicId = 0;
            boolean isSubtasksNeed = false;
            if (reqEpicIdOpt.isPresent()) {
                reqEpicId = reqEpicIdOpt.get();

                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                if (pathParts.length > 3 && pathParts[3].equals("subtasks")) {
                    isSubtasksNeed = true;
                }
            }

            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET" -> {
                    String responseText;
                    if (reqEpicId > 0) {
                        if (isSubtasksNeed) {
                            responseText = gson.toJson(taskManager.getSubtasksByEpicId(reqEpicId));
                        } else {
                            responseText = gson.toJson(taskManager.getEpic(reqEpicId));
                        }
                    } else {
                        responseText = gson.toJson(taskManager.getAllEpics());
                    }
                    sendText(exchange, responseText);
                }
                case "DELETE" -> {
                    taskManager.removeEpic(reqEpicId);
                    sendText(exchange, "Эпик удален.");
                }
                case "POST" -> {
                    String epicJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic reqEpic = gson.fromJson(epicJson, Epic.class);
                    if (reqEpic == null) {
                        throw new NotFoundException("Не передан эпик для обработки.");
                    } else {
                        if (reqEpic.getId() > 0) {
                            taskManager.updateEpic(reqEpic);
                            sendConfirm(exchange, "Эпик обновлен.");
                        } else {
                            taskManager.createEpic(reqEpic);
                            sendConfirm(exchange, "Эпик добавлен.");
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
