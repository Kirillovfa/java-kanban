package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        if (id == epicId) {
            throw new IllegalArgumentException("Сабтаска не может ссылаться на саму себя как на epic");
        }
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return id + "," + getTaskType() + "," + name + "," + status + "," + description +
                "," + epicId +
                "," + (duration != null ? duration.toMinutes() : "") +
                "," + (startTime != null ? startTime : "");
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

}