package ManagerPackage;

import TaskPackage.Epic;
import TaskPackage.Subtask;
import TaskPackage.Task;
import TaskPackage.Status;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void createTask(String name, String description) {
        Task task = new Task(nextId, name, description, Status.NEW);
        nextId += 1;
        tasks.put(task.getId(), task);
    }

    public int createEpic(String name, String description) {
        Epic epic = new Epic(nextId, name, description);
        nextId += 1;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void createSubtask(String name, String description, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            Subtask subtask = new Subtask(nextId, name, description, Status.NEW, epicId);
            nextId += 1;
            subtasks.put(subtask.getId(), subtask);
            epic.addsubtask(subtask.getId());
        } else {
            System.out.println("Epic with id " + epicId + " not found.");
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getsubtasksIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allDone = true;
        boolean anyInProgress = false;
        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subtaskId : epic.getsubtasksIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        subtasks.remove(id);
        updateEpicStatus(epicId);
    }

    // Метод для удаления всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Метод для удаления всех подзадач
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getsubtasksIds().clear();
        }
    }

    // Метод для удаления всех эпиков
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Метод для получения задачи по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Метод для получения подзадачи по идентификатору
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Метод для получения эпика по идентификатору
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Метод для получения всех подзадач эпика
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        if (epic != null) {
            for (int subtaskId : epic.getsubtasksIds()) {
                subtaskList.add(subtasks.get(subtaskId));
            }
        }
        return subtaskList;
    }

    @Override
    public String toString() {
        return "ManagerPackage.TaskManager{" +
                "nextId=" + nextId +
                ", tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }
}