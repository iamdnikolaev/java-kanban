package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс задач
 * @version 2.1
 * @author Николаев Д.В.
 */
public class Task {
    /** Поле названия */
    protected String name;
    /** Поле описания */
    protected String description;
    /** Поле идентификатора задачи */
    protected int id;
    /** Поле статуса перечисляемого типа {@link TaskStatus} */
    protected TaskStatus status;

    /** Поле продолжительности задания, в минутах */
    protected Duration duration;

    /** Поле даты и времени начала выполнения */
    protected LocalDateTime startTime;

    /** Конструктор задачи с параметрами без указания статуса. Создается как NEW.
     * @param name название
     * @param description описание
     * @param id идентификатор
     */
    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = TaskStatus.NEW;
    }

    /** Конструктор задачи с параметрами с указанием текущего статуса.
     * @param name название
     * @param description описание
     * @param id идентификатор
     * @param status текущий статус
     */
    public Task(String name, String description, int id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    /** Конструктор задачи со статусом NEW, без id для прикладных целей
     * @param name название
     * @param description описание
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = TaskStatus.NEW;
    }

    /**
     * Конструктор задачи с указанием текущего статуса, но без id для прикладных целей
     *
     * @param name        название
     * @param description описание
     * @param status      текущий статус
     */
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = status;
    }

    /**
     * Конструктор задачи с параметрами без указания статуса, с атрибутами времени. Создается как NEW.
     *
     * @param name        название
     * @param description описание
     * @param id          идентификатор
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Task(String name, String description, int id, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = TaskStatus.NEW;
        this.duration = (duration != null) ? Duration.ofMinutes(duration) : null;
        this.startTime = startTime;
    }

    /**
     * Конструктор задачи с параметрами, с указанием текущего статуса и атрибутов времени.
     *
     * @param name        название
     * @param description описание
     * @param id          идентификатор
     * @param status      текущий статус
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Task(String name, String description, int id, TaskStatus status, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = (duration != null) ? Duration.ofMinutes(duration) : null;
        this.startTime = startTime;
    }

    /**
     * Конструктор задачи со статусом NEW и атрибутами времени, без id для прикладных целей
     *
     * @param name        название
     * @param description описание
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Task(String name, String description, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = TaskStatus.NEW;
        this.duration = (duration != null) ? Duration.ofMinutes(duration) : null;
        this.startTime = startTime;
    }

    /**
     * Конструктор задачи с указанием текущего статуса и атрибутов времени, но без id для прикладных целей
     *
     * @param name        название
     * @param description описание
     * @param status      текущий статус
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Task(String name, String description, TaskStatus status, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = status;
        this.duration = (duration != null) ? Duration.ofMinutes(duration) : null;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Метод получения расчетной даты и времени окончания выполнения задачи
     * @return startTime + duration
     */
    public LocalDateTime getEndTime() {
       LocalDateTime result = null;
        if (startTime != null && duration != null) {
            result = startTime.plus(duration);
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "task.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + (duration != null ? duration.toMinutes() : null) +
                ", startTime=" + startTime +
                '}';
    }
}