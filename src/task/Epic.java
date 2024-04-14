package task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс эпиков
 * @version 2.6
 * @author Николаев Д.В.
 */
public class Epic extends Task {
    /** Поле список id подзадач эпика */
    private List<Integer> subtaskList;

    /** Конструктор эпика с параметрами.
     * @param name название
     * @param description описание
     * @param id идентификатор
     */
    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        subtaskList = new ArrayList<>();
    }

    /** Конструктор эпика с параметрами, но без id для прикладных целей
     * @param name название
     * @param description описание
     */
    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtaskList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subtaskList=" + subtaskList +
                "}";
    }

    public ArrayList<Integer> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    /** Метод обновления статуса эпика согласно статусам его подзадач
     * @param subtasks общее хранилище подзадач, среди которых перебираются свои согласно {@link Epic#subtaskList}*/
    private void refreshStatus(Map<Integer, Subtask> subtasks) {
        TaskStatus newStatus = TaskStatus.NEW;
        int numberOfSubtasks = subtaskList.size();
        if (numberOfSubtasks > 0 && subtasks != null) {
            HashMap<TaskStatus, Integer> statusCounterMap = new HashMap<>();
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == id) { // Перебираем подзадачи нашего эпика
                    TaskStatus status = subtask.getStatus();
                    Integer statusCounter = statusCounterMap.get(status);
                    if (statusCounter == null) {
                        statusCounter = 0;
                    }
                    statusCounter++;
                    statusCounterMap.put(status, statusCounter);
                }
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

    /** Метод удаления подзадачи из списка у эпика с обновлением его статуса
     * @param subtaskId идентификатор подзадачи
     * @param subtasks общее хранилище подзадач
     */
    public void removeSubtask(Integer subtaskId, Map<Integer, Subtask> subtasks) {
        if (subtasks != null && subtaskList.contains(subtaskId)) {
            subtaskList.remove(subtaskId);
            refreshStatus(subtasks);
        }
    }

    /** Метод очистки списка подзадач у эпика
     */
    public void clearSubtasks() {
        subtaskList.clear();
        refreshStatus(null);
    }

    /** Метод добавления подзадачи в список у эпика с обновлением его статуса.
     * Добавляется в случае ее отсутствия в списке.
     * @param subtask объект подзадачи
     * @param subtasks общее хранилище подзадач
     */
    public void addSubtask(Subtask subtask, Map<Integer, Subtask> subtasks) {
        if (subtask != null && subtasks != null) {
            if (!subtaskList.contains(subtask.getId())) {
                subtaskList.add(subtask.getId());
            }
            refreshStatus(subtasks);
        }
    }
}
