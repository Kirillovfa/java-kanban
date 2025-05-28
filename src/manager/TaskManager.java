package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getTasks();

    Collection<Epic> getEpics();

    Collection<Subtask> getSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    int createTask(String name, String description, Duration duration, LocalDateTime startTime);

    int createEpic(String name, String description);

    int createSubtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getPrioritizedTasks();

    HistoryManager getHistoryManager();
}