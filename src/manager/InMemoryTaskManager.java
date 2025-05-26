package manager;

import task.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int nextId = 1;

    protected final HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public int createTask(String name, String description) {
        Task task = new Task(nextId++, name, description, Status.NEW);
        tasks.put(task.getId(), task);
        historyManager.add(task);
        return task.getId();
    }

    @Override
    public int createEpic(String name, String description) {
        Epic epic = new Epic(nextId++, name, description, Status.NEW);
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
        return epic.getId();
    }

    @Override
    public int createSubtask(String name, String description, Status status, int epicId,
                             Duration duration, LocalDateTime startTime) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Epic с id " + epicId + " не найден");
        }
        Subtask subtask = new Subtask(nextId++, name, description, status, epicId, duration, startTime);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).addSubtask(subtask.getId());
        updateEpic(epicId);
        historyManager.add(subtask);
        return subtask.getId();
    }

    @Override
    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, updatedTask);
            historyManager.add(updatedTask);
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        if (epics.containsKey(id)) {
            epics.put(id, updatedEpic);
            historyManager.add(updatedEpic);
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        int subtaskId = updatedSubtask.getId();
        subtasks.put(subtaskId, updatedSubtask);
        updateEpic(updatedSubtask.getEpicId());
        historyManager.add(updatedSubtask);
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.removeSubtask(subtaskId);
                updateEpic(epicId);
            }
        }
        historyManager.remove(subtaskId);
    }

    @Override
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;
        List<Subtask> subtaskList = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtaskList.add(subtask);
            }
        }
        epic.updateEpicFields(subtaskList);
    }
}