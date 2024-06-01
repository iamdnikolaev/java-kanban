package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

/**
 * Обработчик эндпоинта по списку истории просмотра объектов трекера
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    /**
     * Конструктор
     *
     * @param taskManager назначенный диспетчер задач
     * @param gson        назначенный парсер
     */
    public HistoryHandler(TaskManager taskManager, Gson gson) {
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
                String responseText = gson.toJson(taskManager.getHistory());
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