package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер задач - реализация интерфейса {@link TaskManager}
 *
 * @author Николаев Д.В.
 * @version 3.4
 */
public class InMemoryTaskManager implements TaskManager {
    /**
     * Поле счетчика для генерации идентификаторов методом {@link InMemoryTaskManager#getNextId()}
     */
    protected int idCounter;
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

    /**
     * Поле множества задач и подзадач, упорядоченных по приоритету даты/времени начала выполнения
     */
    private Set<Task> tasksByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            historyManager.remove(entry.getKey());
            if (entry.getValue().getStartTime() != null) {
                tasksByStartTime.remove(entry.getValue());
            }
        }
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
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                historyManager.remove(entry.getKey());
                if (entry.getValue().getStartTime() != null) {
                    tasksByStartTime.remove(entry.getValue());
                }
            }
            subtasks.clear();
        }
    }

    /**
     * Метод очистки хранилища эпиков {@link InMemoryTaskManager#epics}, включая и хранилище подзадач {@link InMemoryTaskManager#subtasks}
     */
    @Override
    public void clearAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();

        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            historyManager.remove(entry.getKey());
            if (entry.getValue().getStartTime() != null) {
                tasksByStartTime.remove(entry.getValue());
            }
        }
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
        return createTask(task, false);
    }

    /**
     * Метод добавления задачи в хранилище {@link InMemoryTaskManager#tasks} с возможностью форсированного выставления
     * заданного id.
     *
     * @param task    задача с атрибутми для добавления
     * @param forceId флаг (true) использования заданного id задачи, если он > 0, иначе (false) - генерация нового id
     * @return созданная задача - объект {@link Task}
     */
    protected Task createTask(Task task, Boolean forceId) {
        if (task != null && isValid(task)) {
            if (forceId) {
                if (task.getId() == 0) {
                    task.setId(getNextId());
                }
            } else {
                task.setId(getNextId());
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                tasksByStartTime.add(task);
            }
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
        return createSubtask(subtask, false);
    }

    /**
     * Метод добавления подзадачи в хранилище {@link InMemoryTaskManager#subtasks} с занесением в список у назначеннго
     * эпика и с возможностью форсированного выставления заданного id.
     *
     * @param subtask подзадача с атрибутами для добавления
     * @param forceId флаг (true) использования заданного id подзадачи, если он > 0, иначе (false) - генерация нового id
     * @return созданная подзадача - объект {@link Subtask}
     */
    protected Subtask createSubtask(Subtask subtask, Boolean forceId) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null && isValid(subtask)) {
                if (forceId) {
                    if (subtask.getId() == 0) {
                        subtask.setId(getNextId());
                    }
                } else {
                    subtask.setId(getNextId());
                }
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtask(subtask, subtasks);
                if (subtask.getStartTime() != null) {
                    tasksByStartTime.add(subtask);
                }
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
        return createEpic(epic, false);
    }

    /**
     * Метод добавления эпика в хранилище {@link InMemoryTaskManager#epics} с возможностью форсированного выставления
     * заданного id.
     *
     * @param epic    эпик с атрибутми для добавления
     * @param forceId флаг (true) использования заданного id эпика, если он > 0, иначе (false) - генерация нового id
     * @return созданный эпик - объект {@link Epic}
     */
    protected Epic createEpic(Epic epic, Boolean forceId) {
        if (epic != null) {
            if (forceId) {
                if (epic.getId() == 0) {
                    epic.setId(getNextId());
                }
            } else {
                epic.setId(getNextId());
            }
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
        if (task != null && tasks.containsKey(task.getId()) && isValid(task)) {
            // Изменение работает через новый объект - надо заменить его в множестве.
            // Плюс если время начала убрано, то убираем объект из множества.
            Task taskPrev = tasks.get(task.getId());
            if (taskPrev.getStartTime() != null) {
                tasksByStartTime.remove(taskPrev);
            }
            if (task.getStartTime() != null) {
                tasksByStartTime.add(task);
            }

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
        if (subtask != null && subtasks.containsKey(subtask.getId()) && isValid(subtask)) {
            Subtask subtaskPrev = subtasks.get(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null && subtaskPrev.getEpicId() == subtask.getEpicId()) {
                if (subtaskPrev.getStartTime() != null) {
                    tasksByStartTime.remove(subtaskPrev);
                }
                if (subtask.getStartTime() != null) {
                    tasksByStartTime.add(subtask);
                }
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
        Task task = tasks.remove(taskId);
        if (task != null && task.getStartTime() != null) {
            tasksByStartTime.remove(task);
        }
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
                if (subtask.getStartTime() != null) {
                    tasksByStartTime.remove(subtask);
                }
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
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask.getStartTime() != null) {
                    tasksByStartTime.remove(subtask);
                }
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
        return subtasks.entrySet().stream()
                .filter(entry -> entry.getValue().getEpicId() == epicId)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
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

    /**
     * Метод получения списка задач и подзадач в порядке приоритета по датам начала выполнения
     *
     * @return List<Task> список задач, подзадач
     */
    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksByStartTime.stream().toList();
    }

    /**
     * Метод валидации задачи. Проверяет:
     * на пересечение по датам начала/окончания с другими задачами(подзадачами) согласно списку из {@link InMemoryTaskManager#tasksByStartTime}.
     *
     * @param taskToCheck объект {@link Task} задачи (подзадачи) для проверки.
     * @return true - задача прошла проверки; иначе генерация исключения.
     */
    @Override
    public boolean isValid(Task taskToCheck) throws InvalidTaskException {
        List<Task> tasks = getPrioritizedTasks();
        Optional<Task> taskCrossingWith = tasks.stream()
                .filter(task -> task.getId() != taskToCheck.getId())
                .filter(task -> TaskUtil.isCrossing(taskToCheck, task))
                .findFirst();
        if (taskCrossingWith.isPresent()) {
            throw new InvalidTaskException("Задача пересекается по времени с уже имеющейся: " + taskCrossingWith.get());
        }
        return taskCrossingWith.isEmpty();
    }

    /**
     * Вспомогательный метод очистки истории просмотра задач (подзадач, эпиков)
     */
    @Override
    public void clearHistory() {
        historyManager.clear();
    }
}
