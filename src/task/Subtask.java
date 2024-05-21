package task;

import java.time.LocalDateTime;

/**
 * Класс подзадач
 * @version 2.1
 * @author Николаев Д.В.
 */
public class Subtask extends Task {
    /** Поле идентификатора эпика, чьей подзадачей является */
    private int epicId;

    /** Конструктор подзадачи с параметрами без указания статуса. По умолчанию, NEW.
     * @param name название
     * @param description описание
     * @param id идентификатор
     * @param epicId идентификатор эпика
     */
    public Subtask(String name, String description, int id, int epicId) {
        super(name, description, id, TaskStatus.NEW);
        this.epicId = epicId;
    }

    /** Конструктор подзадачи с параметрами с указанием текущего статуса.
     * @param name название
     * @param description описание
     * @param id идентификатор
     * @param status текущий статус
     * @param epicId идентификатор эпика
     */
    public Subtask(String name, String description, int id, TaskStatus status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    /** Конструктор подзадачи со статусом NEW, без id для прикладных целей
     * @param name название
     * @param description описание
     * @param epicId идентификатор эпика
     */
    public Subtask(String name, String description, int epicId) {
        super(name, description, TaskStatus.NEW);
        this.epicId = epicId;
    }

    /** Конструктор подзадачи с указанием текущего статуса, но без id для прикладных целей
     * @param name название
     * @param description описание
     * @param status текущий статус
     * @param epicId идентификатор эпика
     */
    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    /**
     * Конструктор подзадачи с параметрами и атрибутами времени без указания статуса. По умолчанию, NEW.
     *
     * @param name        название
     * @param description описание
     * @param id          идентификатор
     * @param epicId      идентификатор эпика
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Subtask(String name, String description, int id, int epicId, Long duration, LocalDateTime startTime) {
        super(name, description, id, TaskStatus.NEW, duration, startTime);
        this.epicId = epicId;
    }

    /**
     * Конструктор подзадачи с параметрами и атрибутами времени с указанием текущего статуса.
     *
     * @param name        название
     * @param description описание
     * @param id          идентификатор
     * @param status      текущий статус
     * @param epicId      идентификатор эпика
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Subtask(String name, String description, int id, TaskStatus status, int epicId, Long duration,
                   LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    /**
     * Конструктор подзадачи со статусом NEW и атрибутами времени, без id для прикладных целей
     *
     * @param name        название
     * @param description описание
     * @param epicId      идентификатор эпика
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Subtask(String name, String description, int epicId, Long duration, LocalDateTime startTime) {
        super(name, description, TaskStatus.NEW, duration, startTime);
        this.epicId = epicId;
    }

    /**
     * Конструктор подзадачи с указанием текущего статуса и атрибутами времени, но без id для прикладных целей
     *
     * @param name        название
     * @param description описание
     * @param status      текущий статус
     * @param epicId      идентификатор эпика
     * @param duration    продолжительность задачи, мин. - целое число
     * @param startTime   дата и время начала выполнения
     */
    public Subtask(String name, String description, TaskStatus status, int epicId, Long duration,
                   LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epicId=" + epicId +
                ", duration=" + (duration != null ? duration.toMinutes() : null) +
                ", startTime=" + startTime +
                '}';
    }
}