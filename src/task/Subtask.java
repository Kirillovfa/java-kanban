package task;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        if (id == epicId) {
            throw new IllegalArgumentException("Сабтаска не может ссылаться на саму себя как на epic");
        }
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
