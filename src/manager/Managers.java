package manager;

/**
 * Утилитарный класс менеджеров
 *
 * @author Николаев Д.В.
 * @version 1.0
 */
public class Managers {
    /**
     * Метод получения менеджера работы с объектами учета (задачами, подзадачами, эпиками) {@link InMemoryTaskManager}
     *
     * @return Объект класса InMemoryTaskManager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Метод получения менеджера истории просмотра объектов учета (задач, подзадач, эпиков) {@link InMemoryHistoryManager}
     *
     * @return Объект класса InMemoryHistoryManager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
