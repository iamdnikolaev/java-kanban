package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

/**
 * Обработчик эндпоинта по приоретизированному списку задач/подзадач
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class PriorityHandler extends BaseHttpHandler implements HttpHandler {

    /**
     * Конструктор
     *
     * @param taskManager назначенный диспетчер задач
     * @param gson        назначенный парсер
     */
    public PriorityHandler(TaskManager taskManager, Gson gson) {
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
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                String responseText = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, responseText);
            } else {
                sendNotFound(exchange, "Некорректный эндпоинт.");
            }
        } catch (Exception e) {
            sendInternalError(exchange, e.getMessage());
        } finally {
            exchange.close();
        }
    }
}

