package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime = null;
    private LocalDateTime endTime = null;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status, Duration.ZERO, null);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int subtaskId) {
        if (subtaskId != this.id && !subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void updateEpicFields(List<Subtask> subtasks) {
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.endTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == this.id) {
                if (subtask.getDuration() != null) {
                    this.duration = this.duration.plus(subtask.getDuration());
                }
                if (subtask.getStartTime() != null) {
                    if (this.startTime == null || subtask.getStartTime().isBefore(this.startTime)) {
                        this.startTime = subtask.getStartTime();
                    }
                    LocalDateTime subtaskEnd = subtask.getEndTime();
                    if (subtaskEnd != null) {
                        if (this.endTime == null || subtaskEnd.isAfter(this.endTime)) {
                            this.endTime = subtaskEnd;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) &&
                Objects.equals(duration, epic.duration) &&
                Objects.equals(startTime, epic.startTime) &&
                Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}