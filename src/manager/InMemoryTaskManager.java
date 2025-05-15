package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    public HashMap<Integer, Task> getTasks() {
        return (HashMap<Integer, Task>) tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return (HashMap<Integer, Epic>) epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return (HashMap<Integer, Subtask>) subtasks;
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

    @Override
    public void createSubtask(String name, String description, int epicId) {
        if (!epics.containsKey(epicId)) return;
        int subtaskId = nextId;
        if (epicId == subtaskId) {
            return;
        }
        Subtask subtask = new Subtask(nextId++, name, description, Status.NEW, epicId);
        if (subtask.getId() == epicId) {
            throw new IllegalArgumentException("Сабтаска не может ссылаться на саму себя как на эпик");
        }
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

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return List.of();
        }
        List<Subtask> result = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return List.of();
    }
}