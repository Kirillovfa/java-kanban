package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public ArrayList<Integer> getsubtasksIds() {
        return subtasksIds;
    }

    public void addsubtask(int subtaskId) {
        if (this.getId() == subtaskId) {
            return; // не добавляем эпик как подзадачу самому себе
        }
        subtasksIds.add(subtaskId);
    }

    public void removesubtask(int subtaskId) {
        subtasksIds.remove((Integer) subtaskId);
    }

    @Override
    public String toString() {
        return "TaskPackage.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasksIds=" + getsubtasksIds() +
                '}';
    }
}