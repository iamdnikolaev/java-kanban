import java.util.ArrayList;
import java.util.HashMap;

/**
 * Трекер задач
 * @version 2.0
 * @author Николаев Д.В.
 */
public class TaskManager {
    /** Поле счетчика для генерации идентификаторов методом {@link TaskManager#getNextId()} */
    private int idCounter;
    /** Поле хранилище задач */
    private HashMap<Integer, Task> tasks = new HashMap<>();
    /** Поле хранилище подзадач */
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    /** Поле хранилище эпиков */
    private HashMap<Integer, Epic> epics = new HashMap<>();

    /** Метод сквозной генерации идентификаторов объектов учета (задач, подзадач, эпиков), используя поле {@link TaskManager#idCounter}
     * @return int целочисленный идентификатор */
    private int getNextId() {
        return ++idCounter;
    }

    /** Метод получения списка задач {@link TaskManager#tasks}
     * @return ArrayList<Task>
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            result.add(task);
        }
        return result;
    }

    /** Метод получения списка подзадач {@link TaskManager#subtasks}
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            result.add(subtask);
        }
        return result;
    }

    /** Метод получения списка эпиков {@link TaskManager#epics}
     * @return ArrayList<Epic>
     */
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Epic epic : epics.values()) {
            result.add(epic);
        }
        return result;
    }

    /** Метод очистки хранилища задач {@link TaskManager#tasks} */
    public void clearAllTasks() {
        tasks.clear();
    }

    /** Метод очистки хранилища подзадач {@link TaskManager#subtasks}, затрагивая список у эпиков */
    public void clearAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.clearSubtasks();
            }
            subtasks.clear();
        }
    }

    /** Метод очистки хранилища эпиков {@link TaskManager#epics}, включая и хранилище подзадач {@link TaskManager#subtasks} */
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    /** Метод получения объекта задачи по ее идентификатору {@link Task#id} в хранилище {@link TaskManager#tasks}
     * @param taskId идентификатор задачи
     * @return {@link Task} объект задачи
     */
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    /** Метод получения объекта подзадачи по ее идентификатору {@link Subtask#id} в хранилище {@link TaskManager#subtasks}
     * @param subtaskId идентификатор подзадачи
     * @return {@link Subtask} объект подзадачи
     */
    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    /** Метод получения объекта эпика по его идентификатору {@link Epic#id} в хранилище {@link TaskManager#epics}
     * @param epicId идентификатор эпика
     * @return {@link Epic} объект эпика
     */
    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    /** Метод добавления задачи в хранилище {@link TaskManager#tasks}
     * @param task задача с атрибутми для добавления
     * @return созданная задача - объект {@link Task}
     */
    public Task createTask(Task task) {
        Task newTask = null;
        if (task != null) {
            newTask = new Task(task.getName(), task.getDescription(), getNextId());
            tasks.put(newTask.getId(), newTask);
        }
        return newTask;
    }

    /** Метод добавления подзадачи в хранилище {@link TaskManager#subtasks} с занесением в список у назначеннго эпика
     * @param subtask подзадача с атрибутми для добавления
     * @return созданная подзадача - объект {@link Subtask}
     */
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = null;
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), getNextId(), subtask.getEpicId());
                epic.createOrUpdateSubtask(newSubtask, subtasks);
            }
        }
        return newSubtask;
    }

    /** Метод добавления эпика в хранилище {@link TaskManager#epics}
     * @param epic эпик с атрибутми для добавления
     * @return созданный эпик - объект {@link Epic}
     */
    public Epic createEpic(Epic epic) {
        Epic newEpic = null;
        if (epic != null) {
            newEpic = new Epic(epic.getName(), epic.getDescription(), getNextId());
            epics.put(newEpic.getId(), newEpic);
        }
        return newEpic;
    }

    /** Метод изменения задачи в хранилище {@link TaskManager#tasks}
     * @param task задача с обновленными атрибутами
     */
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    /** Метод изменения подзадачи в хранилище {@link TaskManager#subtasks}
     * @param subtask подзадача с обновленными атрибутами
     */
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.createOrUpdateSubtask(subtask, subtasks);
            }
        }
    }

    /** Метод изменения эпика в хранилище {@link TaskManager#epics}.
     * Не затрагивает его статус и список подзадач.
     * @param epic эпик с обновленными атрибутами
     */
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic epicTarget = epics.get(epic.getId());
            epicTarget.setName(epic.getName());
            epicTarget.setDescription(epic.getDescription());
            epics.put(epicTarget.getId(), epicTarget);
        }
    }

    /** Метод удаления задачи из хранилища {@link TaskManager#tasks} по идентификатору
     * @param taskId удаляемая задача
     */
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    /** Метод удаления подзадачи из хранилища по идентификатору {@link TaskManager#subtasks} и у своего эпика
     * @param subtaskId удаляемая подзадача
     */
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtaskId, subtasks);
            }
        }
    }

    /** Метод удаления эпика из хранилища {@link TaskManager#epics} по идентификатору,
     * @param epicId удаляемый эпик
     */
    public void removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskList()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    /** Метод получения списка подзадач эпика по его идентификатору {@link Epic#id}
     * @param epicId эпик для обработки
     * @return ArrayList<Subtask> список подзадач
     */
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
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

    /** Метод получения списка подзадач эпика
     * @param epic эпик для обработки
     * @return ArrayList<Subtask> список подзадач
     */
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        if (epic != null) {
            result = getSubtasksByEpicId(epic.getId());
        }
        return result;
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "idCounter=" + idCounter +
                ", tasks=" + tasks +
                ", subtasks=" + subtasks +
                ", epics=" + epics +
                '}';
    }
}
