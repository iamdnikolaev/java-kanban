package task;

import java.util.Objects;

/**
 * Класс задач
 * @version 2.0
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

    /** Конструктор задачи с указанием текущего статуса, но без id для прикладных целей
     * @param name название
     * @param description описание
     * @param status текущий статус
     */
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = status;
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
                '}';
    }
}