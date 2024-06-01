package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Веб-сервер для работы с трекером задач
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class HttpTaskServer {
    /**
     * Назначенный порт для работы
     */
    private static final int PORT = 8080;

    /**
     * Поле запущенного сервера
     */
    private HttpServer httpServer;

    /**
     * Поле диспетчера задач. Назначается обработачикам эндпоинтов
     */
    private TaskManager taskManager;

    /**
     * Поле JSON-парсера. Назначается обработачикам эндпоинтов для единообразной обработки
     */
    private Gson gson;

    public Gson getGson() {
        return gson;
    }

    /**
     * Адаптер типа LocalDateTime
     */
    private LocalDateTimeTypeAdapter localDateTimeTypeAdapter = new LocalDateTimeTypeAdapter();

    /**
     * Адаптер типа Duration
     */
    private DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter();

    /** Конструктор
     * @param manager используемый диспетчер задач
     * */
    public HttpTaskServer(TaskManager manager) {
        taskManager = manager;
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeTypeAdapter)
                .registerTypeAdapter(Duration.class, durationTypeAdapter)
                .create();
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    /** Метод запуска веб-сервера */
    public void start() {
        try {
            httpServer = HttpServer.create();

            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PriorityHandler(taskManager, gson));

            httpServer.start();
        } catch (Exception e) {
            System.out.println("Возникла ошибка работы http-сервера.");
            e.printStackTrace();
        }
    }

    /** Метод остановки веб-сервера */
    public void stop() {
        httpServer.stop(0);
    }
}
