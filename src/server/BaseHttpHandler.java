package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    /**
     * Поле диспетчера задач
     */
    protected TaskManager taskManager;

    /**
     * Поле json-парсера, используемого для разбора запросов
     */
    protected Gson gson;

    /**
     * Базовый конструктор
     *
     * @param taskManager назначенный диспетчер задач
     * @param gson        назначенный парсер
     */
    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    /**
     * Метод получения id обрабатываемого элемента из строки адреса
     *
     * @param h объект обмена обслуживаемого запроса
     * @return Опционал обертки распарсенного id
     */
    protected Optional<Integer> getTaskId(HttpExchange h) {
        String[] pathParts = h.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /** Метод отправки произвольного ответа с заданным кодом статуса
     * @param h объект обмена обслуживаемого запроса
     * @param text текст сообщения в теле ответа
     * @param code код статуса в ответе */
    private void sendResponse(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        if (!text.isBlank()) {
            h.getResponseBody().write(resp);
        }
        h.close();
    }

    /** Метод отправки ответа о подтверждении обработки - со статусом 201
     * @param h объект обмена обслуживаемого запроса
     * @param text текст сообщения в теле ответа */
    protected void sendConfirm(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 201);
    }

    /** Метод отправки результата успешной обработки - со статусом 200
     * @param h объект обмена обслуживаемого запроса
     * @param text текст тела ответа в формате json */
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        if (!text.isBlank()) {
            h.getResponseBody().write(resp);
        }
        h.close();
    }

    /** Метод отправки ответа о ненайденном объекте - со статусом 404
     * @param h объект обмена обслуживаемого запроса
     * @param text текст сообщения в теле ответа */
    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 404);
    }

    /** Метод отправки ответа о неподходящем объекте (пересечение по времени выполнения) - со статусом 406
     * @param h объект обмена обслуживаемого запроса
     * @param text текст сообщения в теле ответа */
    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 406);
    }

    /** Метод отправки ответа о внутренней ошибке сервера - со статусом 500
     * @param h объект обмена обслуживаемого запроса
     * @param text текст сообщения в теле ответа */
    protected void sendInternalError(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 500);
    }
}