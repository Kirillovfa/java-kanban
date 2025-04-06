package manager;

import task.Task;
import task.Epic;
import task.Subtask;
import java.util.List;
import java.util.HashMap;


public interface TaskManager {
    void createTask(String name, String description);
    int createEpic(String name, String description);
    void createSubtask(String name, String description, int epicId);
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void deleteTask(int id);
    void deleteEpicById(int id);
    void deleteSubtask(int id);
    void deleteAllTasks();
    void deleteAllSubtasks();
    void deleteAllEpics();
    Task getTaskById(int id);
    Subtask getSubtaskById(int id);
    Epic getEpicById(int id);
    List<Subtask> getSubtasksByEpicId(int epicId);
    List<Task> getHistory();
    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, Subtask> getSubtasks();
}