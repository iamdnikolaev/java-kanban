import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс эпиков
 * @version 1.0
 * @author Николаев Д.В.
 */
public class Epic extends Task{
    /** Поле хэш-таблица с подзадачами эпика */
    ArrayList<Subtask> subtasks;

    /** Конструктор эпика с параметрами.
     * @param name название
     * @param description описание
     * @param id идентификатор
     */
    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        subtasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subtasks=" + subtasks +
                "}";
    }

    @Override
    public TaskStatus getStatus() {
        refreshStatus();
        return status;
    }

    /** Метод обновления статуса эпика согласно статусам его подзадач */
    public void refreshStatus() {
        TaskStatus newStatus = TaskStatus.NEW;
        int numberOfSubtasks = subtasks.size();
        if (numberOfSubtasks > 0) {
            HashMap<TaskStatus, Integer> statusCounterMap = new HashMap<>();
            for (Subtask subtask : subtasks) {
                TaskStatus status = subtask.status;
                Integer statusCounter = statusCounterMap.get(status);
                if (statusCounter == null) {
                    statusCounter = 0;
                }
                statusCounter++;
                statusCounterMap.put(status, statusCounter);
            }
            for (TaskStatus status : statusCounterMap.keySet()) {
                if (statusCounterMap.get(status) == numberOfSubtasks) {
                    newStatus = status;
                    break;
                } else {
                    newStatus = TaskStatus.IN_PROGRESS;
                }
            }
        }
        status = newStatus;
    }
}
