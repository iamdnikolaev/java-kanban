import java.util.ArrayList;
import java.util.HashMap;

/**
 * Планировщик задач
 * @version 1.0
 * @author Николаев Д.В.
 */
public class TaskManager {
    /** Поле счетчика для генерации идентификаторов методом {@link TaskManager#getNextId()} */
    private int idCounter;
    /** Поле хэш-таблица задач */
    private HashMap<Integer, Task> tasks = new HashMap<>();
    /** Поле хэш-таблица подзадач */
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    /** Поле хэш-таблица эпиков */
    private HashMap<Integer, Epic> epics = new HashMap<>();

    /** Метод сквозной генерации идентификаторов объектов учета (задач, подзадач, эпиков), используя поле {@link TaskManager#idCounter}
     * @return int целочисленный идентификатор */
    public int getNextId() {
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

    /** Метод очистки таблицы задач {@link TaskManager#tasks} */
    public void clearAllTasks() {
        tasks.clear();
    }

    /** Метод очистки таблицы подзадач {@link TaskManager#subtasks}, затрагивая поле эпиков {@link Epic#subtasks} */
    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            epic.refreshStatus();
        }
    }

    /** Метод очистки таблицы эпиков {@link TaskManager#epics}, включая и таблицу подзадач {@link TaskManager#subtasks} */
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    /** Метод получения объекта задачи по ее идентификатору {@link Task#id} в таблице {@link TaskManager#tasks}
     * @param taskId идентификатор задачи
     * @return {@link Task} объект задачи
     */
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    /** Метод получения объекта подзадачи по ее идентификатору {@link Subtask#id} в таблице {@link TaskManager#subtasks}
     * @param subtaskId идентификатор подзадачи
     * @return {@link Subtask} объект подзадачи
     */
    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    /** Метод получения объекта эпика по его идентификатору {@link Epic#id} в таблице {@link TaskManager#epics}
     * @param epicId идентификатор эпика
     * @return {@link Epic} объект эпика
     */
    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    /** Метод добавления объекта задачи в таблицу {@link TaskManager#tasks}
     * @param task добавляемая задача
     */
    public void createTask(Task task) {
        if (task != null) {
            tasks.put(task.id, task);
        }
    }

    /** Метод добавления объекта подзадачи в таблицу {@link TaskManager#subtasks}, если указанный эпик существует в {@link TaskManager#epics}
     * @param subtask добавляемая подзадача
     */
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                subtasks.put(subtask.id, subtask);
                if (epic.subtasks != null) {
                    epic.subtasks.remove(subtask);
                    epic.subtasks.add(subtask);
                }
                epic.refreshStatus();
            }
        }
    }

    /** Метод добавления объекта эпика в таблицу {@link TaskManager#epics}
     * @param epic добавляемый эпик
     */
    public void createEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.id, epic);
        }
    }

    /** Метод изменения объекта задачи в таблице {@link TaskManager#tasks}
     * @param task задача с обновленными атрибутами
     */
    public void updateTask(Task task) {
        if (task != null) {
            tasks.put(task.id, task);
        }
    }

    /** Метод изменения объекта подзадачи в таблице {@link TaskManager#subtasks}, затрагивая и поле своего эпика {@link Epic#subtasks}
     * с обновлением статуса этого эпика.
     * @param subtask подзадача с обновленными атрибутами
     */
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                subtasks.put(subtask.id, subtask);
                if (epic.subtasks != null) {
                    epic.subtasks.remove(subtask);
                    epic.subtasks.add(subtask);
                }
                epic.refreshStatus();
            }
        }
    }

    /** Метод изменения объекта эпика в таблице {@link TaskManager#epics}
     * @param epic эпик с обновленными атрибутами
     */
    public void updateEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.id, epic);
            epic.refreshStatus();
        }
    }

    /** Метод удаления объекта задачи в таблице {@link TaskManager#tasks} по идентификатору
     * @param taskId удаляемая задача
     */
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    /** Метод удаления объекта подзадачи в таблице {@link TaskManager#subtasks} по идентификатору,
     * затрагивая и поле своего эпика {@link Epic#subtasks}
     * с обновлением статуса этого эпика
     * @param subtaskId удаляемая подзадача
     */
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.subtasks.remove(subtask);
                epic.refreshStatus();
            }
            subtasks.remove(subtaskId);
        }
    }

    /** Метод удаления объекта эпика в таблице {@link TaskManager#epics} по идентификатору,
     * @param epicId удаляемый эпик
     */
    public void removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.subtasks) {
                subtasks.remove(subtask.id);
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
            for (Subtask subtask : epic.subtasks) {
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    /** Метод получения списка подзадач объекта эпика
     * @param epic эпик для обработки
     * @return ArrayList<Subtask> список подзадач
     */
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Subtask subtask : epic.subtasks) {
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }
}
