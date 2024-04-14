package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер задач - реализация интерфейса {@link TaskManager}
 *
 * @author Николаев Д.В.
 * @version 3.2
 */
public class InMemoryTaskManager implements TaskManager {
    /**
     * Поле счетчика для генерации идентификаторов методом {@link InMemoryTaskManager#getNextId()}
     */
    private int idCounter;
    /**
     * Поле хранилище задач
     */
    private Map<Integer, Task> tasks = new HashMap<>();
    /**
     * Поле хранилище подзадач
     */
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    /**
     * Поле хранилище эпиков
     */
    private Map<Integer, Epic> epics = new HashMap<>();

    /**
     * Поле менеджера истории просмотра объектов учета (задач, подзадач, эпиков)
     */
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                "idCounter=" + idCounter +
                ", tasks=" + tasks +
                ", subtasks=" + subtasks +
                ", epics=" + epics +
                '}';
    }

    /**
     * Метод сквозной генерации идентификаторов объектов учета (задач, подзадач, эпиков), используя поле {@link InMemoryTaskManager#idCounter}
     *
     * @return int целочисленный идентификатор
     */
    private int getNextId() {
        return ++idCounter;
    }

    /**
     * Метод получения списка задач {@link InMemoryTaskManager#tasks}
     *
     * @return ArrayList<Task>
     */
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Метод получения списка подзадач {@link InMemoryTaskManager#subtasks}
     *
     * @return ArrayList<Subtask>
     */
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Метод получения списка эпиков {@link InMemoryTaskManager#epics}
     *
     * @return ArrayList<Epic>
     */
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());

    }

    /**
     * Метод очистки хранилища задач {@link InMemoryTaskManager#tasks}
     */
    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    /**
     * Метод очистки хранилища подзадач {@link InMemoryTaskManager#subtasks}, затрагивая список у эпиков
     */
    @Override
    public void clearAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.clearSubtasks();
            }
            subtasks.clear();
        }
    }

    /**
     * Метод очистки хранилища эпиков {@link InMemoryTaskManager#epics}, включая и хранилище подзадач {@link InMemoryTaskManager#subtasks}
     */
    @Override
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    /**
     * Метод получения объекта задачи по ее идентификатору {@link Task#getId()} в хранилище {@link InMemoryTaskManager#tasks}
     *
     * @param taskId идентификатор задачи
     * @return {@link Task} объект задачи
     */
    @Override
    public Task getTask(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    /**
     * Метод получения объекта подзадачи по ее идентификатору {@link Subtask#getId()} в хранилище {@link InMemoryTaskManager#subtasks}
     *
     * @param subtaskId идентификатор подзадачи
     * @return {@link Subtask} объект подзадачи
     */
    @Override
    public Subtask getSubtask(int subtaskId) {
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    /**
     * Метод получения объекта эпика по его идентификатору {@link Epic#getId()} в хранилище {@link InMemoryTaskManager#epics}
     *
     * @param epicId идентификатор эпика
     * @return {@link Epic} объект эпика
     */
    @Override
    public Epic getEpic(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    /**
     * Метод добавления задачи в хранилище {@link InMemoryTaskManager#tasks}
     *
     * @param task задача с атрибутми для добавления
     * @return созданная задача - объект {@link Task}
     */
    @Override
    public Task createTask(Task task) {
        if (task != null) {
            task.setId(getNextId());
            tasks.put(task.getId(), task);
        }
        return task;
    }

    /**
     * Метод добавления подзадачи в хранилище {@link InMemoryTaskManager#subtasks} с занесением в список у назначеннго эпика
     *
     * @param subtask подзадача с атрибутми для добавления
     * @return созданная подзадача - объект {@link Subtask}
     */
    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                subtask.setId(getNextId());
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtask(subtask, subtasks);
            }
        }
        return subtask;
    }

    /**
     * Метод добавления эпика в хранилище {@link InMemoryTaskManager#epics}
     *
     * @param epic эпик с атрибутми для добавления
     * @return созданный эпик - объект {@link Epic}
     */
    @Override
    public Epic createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(getNextId());
            epics.put(epic.getId(), epic);
        }
        return epic;
    }

    /**
     * Метод изменения задачи в хранилище {@link InMemoryTaskManager#tasks}
     *
     * @param task задача с обновленными атрибутами
     */
    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    /**
     * Метод изменения подзадачи в хранилище {@link InMemoryTaskManager#subtasks}
     *
     * @param subtask подзадача с обновленными атрибутами
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Subtask subtaskPrev = subtasks.get(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null && subtaskPrev.getEpicId() == subtask.getEpicId()) {
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtask(subtask, subtasks); // Вызов для обновления статуса эпика
            }
        }
    }

    /**
     * Метод изменения эпика в хранилище {@link InMemoryTaskManager#epics}.
     * Не затрагивает его статус и список подзадач.
     *
     * @param epic эпик с обновленными атрибутами
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic epicTarget = epics.get(epic.getId());
            epicTarget.setName(epic.getName());
            epicTarget.setDescription(epic.getDescription());
        }
    }

    /**
     * Метод удаления задачи из хранилища {@link InMemoryTaskManager#tasks} по идентификатору
     *
     * @param taskId удаляемая задача
     */
    @Override
    public void removeTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    /**
     * Метод удаления подзадачи из хранилища {@link InMemoryTaskManager#subtasks} по идентификатору и у своего эпика
     *
     * @param subtaskId удаляемая подзадача
     */
    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                subtasks.remove(subtaskId);
                epic.removeSubtask(subtaskId, subtasks);
                historyManager.remove(subtaskId);
            }
        }
    }

    /**
     * Метод удаления эпика из хранилища {@link InMemoryTaskManager#epics} по идентификатору,
     *
     * @param epicId удаляемый эпик
     */
    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskList()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    /**
     * Метод получения списка подзадач эпика по его идентификатору {@link Epic#getId()}
     *
     * @param epicId эпик для обработки
     * @return ArrayList<task.Subtask> список подзадач
     */
    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskList()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    /**
     * Метод получения списка подзадач эпика
     *
     * @param epic эпик для обработки
     * @return ArrayList<task.Subtask> список подзадач
     */
    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> result = new ArrayList<>();
        if (epic != null) {
            result = getSubtasksByEpicId(epic.getId());
        }
        return result;
    }

    /**
     * Метод получения истории просмотра задач (подзадач, эпиков) через назначенный {@link InMemoryTaskManager#historyManager}
     *
     * @return List<Task> список задач (подзадач, эпиков) в истории
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
