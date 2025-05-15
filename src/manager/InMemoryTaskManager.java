package manager;

import task.*;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void createTask(String name, String description) {
        Task task = new Task(nextId++, name, description, Status.NEW);
        tasks.put(task.getId(), task);
    }

    public int createEpic(String name, String description) {
        Epic epic = new Epic(nextId++, name, description);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void createSubtask(String name, String description, int epicId) {
        if (!epics.containsKey(epicId)) return;
        Subtask subtask = new Subtask(nextId++, name, description, Status.NEW, epicId);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).addSubtask(subtask.getId());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }
}