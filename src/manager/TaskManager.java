package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    int createTask(String name, String description);

    int createEpic(String name, String description);

    int createSubtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime);

    void updateTask(Task updatedTask);

    void updateEpic(Epic updatedEpic);

    void updateSubtask(Subtask updatedSubtask);

    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subtaskId);

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Subtask getSubtask(int subtaskId);

    Collection<Task> getTasks();

    Collection<Epic> getEpics();

    Collection<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}