package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения", e);
        }
    }

    @Override
    public void createTask(String name, String description) {
        super.createTask(name, description);
        save();
    }

    @Override
    public int createEpic(String name, String description) {
        int id = super.createEpic(name, description);
        save();
        return id;
    }

    @Override
    public void createSubtask(String name, String description, int epicId) {
        Subtask subtask = new Subtask(name, description, epicId);
        if (subtask.getId() == epicId) {
            throw new IllegalArgumentException("Сабтаска не может ссылаться на саму себя как на эпик");
        }
        super.createSubtask(name, description, epicId);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) return manager;
            lines = lines.subList(1, lines.size());
            for (String line : lines) {
                if (line.isBlank()) continue;
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    Subtask sub = (Subtask) task;
                    manager.subtasks.put(sub.getId(), sub);
                    Epic epic = manager.epics.get(sub.getEpicId());
                    if (epic != null) {
                        epic.addSubtask(sub.getId());
                    }
                } else {
                    manager.tasks.put(task.getId(), task);
                }
                manager.nextId = Math.max(manager.nextId, task.getId() + 1);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки", e);
        }
        return manager;
    }

    private static Task fromString(String line) {
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalStateException("Неизвестный тип задачи");
        }
    }
}