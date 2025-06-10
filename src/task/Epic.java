package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String name, String description, Status aNew) {
        super(id, name, description, Status.NEW, Duration.ZERO, null);
    }

    public List<Integer> getSubtaskIds() {
        return Collections.unmodifiableList(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        if (subtaskId != this.id && !subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void updateTimesAndStatus(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            return;
        }
        this.duration = subtasks.stream()
                .filter(st -> st.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public LocalDateTime getEndTime(List<Subtask> subtasks) {
        return subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public String toString() {
        return id + "," + getTaskType() + "," + name + "," + status + "," + description +
                "," + (duration != null ? duration.toMinutes() : "") +
                "," + (startTime != null ? startTime : "");
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}