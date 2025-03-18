import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Task createTask(String name, String description) {
        Task task = new Task(nextId, name, description, Status.NEW);
        nextId += 1;
        tasks.put(task.getId(), task);
        return task;
    }


    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(nextId, name, description);
        nextId += 1;
        epics.put(epic.getId(), epic);
        return epic;
    }


    public Subtask createSubtask(String name, String description, int epicId) {
        Subtask subtask = new Subtask(nextId, name, description, Status.NEW, epicId);
        nextId += 1;
        subtasks.put(subtask.getId(), subtask);
        Epic newEpic = epics.get(epicId);
        newEpic.addsubtask(subtask.getId());
        return subtask;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getsubtasks();
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

    public void deleteEpic(int id) {
        Epic neededEpic = epics.get(id);
        for (int subtaskId : neededEpic.getsubtasks()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }


    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        subtasks.remove(id);
        updateEpicStatus(epicId);

    }

    public void printTasks() {
        System.out.println("Tasks: " + tasks.values());
    }

    public void printEpics() {
        System.out.println("Epics: " + epics.values());
    }

    public void printsubtasks() {
        System.out.println("subtasks: " + subtasks.values());
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "nextId=" + nextId +
                ", tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }
}