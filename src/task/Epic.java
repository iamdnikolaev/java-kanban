package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс эпиков
 * @version 2.7
 * @author Николаев Д.В.
 */
public class Epic extends Task {
    /** Поле список id подзадач эпика */
    private List<Integer> subtaskList;

    /**
     * Расчетное поле даты и времени окончания выполнения
     */
    private LocalDateTime endTime;

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
                ", duration=" + (duration != null ? duration.toMinutes() : null) +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                "}";
    }

    public ArrayList<Integer> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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

    /** Метод обновления атрибутов времени выполнения эпика согласно его подзадач
     * @param subtasks общее хранилище подзадач, среди которых перебираются свои согласно {@link Epic#subtaskList}*/
    private void refreshTimes(Map<Integer, Subtask> subtasks) {
        duration = null;
        startTime = null;
        endTime = null;
        int numberOfSubtasks = subtaskList.size();
        if (numberOfSubtasks > 0 && subtasks != null) {
            List<Subtask> subtaskSortedByTime = new ArrayList<>();
            for (int id : subtaskList) {
                Subtask subtask = subtasks.get(id);
                if (subtask.getStartTime() != null) {
                    subtaskSortedByTime.add(subtask);
                }
            }
            if (!subtaskSortedByTime.isEmpty()) {
                subtaskSortedByTime.sort(Comparator.comparing(Subtask::getStartTime));

                Subtask firstSubtask = subtaskSortedByTime.getFirst();
                startTime = firstSubtask.getStartTime();

                Subtask lastSubtask = subtaskSortedByTime.getLast();
                endTime = lastSubtask.getEndTime();

                duration = firstSubtask.getDuration();
                subtaskSortedByTime.removeFirst();
                subtaskSortedByTime.forEach(subtask -> duration = duration.plus(subtask.getDuration()));
            }
        }
    }

    /** Метод удаления подзадачи из списка у эпика с обновлением его статуса
     * @param subtaskId идентификатор подзадачи
     * @param subtasks общее хранилище подзадач
     */
    public void removeSubtask(Integer subtaskId, Map<Integer, Subtask> subtasks) {
        if (subtasks != null && subtaskList.contains(subtaskId)) {
            subtaskList.remove(subtaskId);
            refreshStatus(subtasks);
            refreshTimes(subtasks);
        }
    }

    /** Метод очистки списка подзадач у эпика
     */
    public void clearSubtasks() {
        subtaskList.clear();
        refreshStatus(null);
        refreshTimes(null);
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
            refreshTimes(subtasks);
        }
    }
}
