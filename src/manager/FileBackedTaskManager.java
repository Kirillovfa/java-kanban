package manager;

import task.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks().values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics().values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks().values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить", e);
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

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        // сюда дополнить типом для файлика
        if (task instanceof Epic) {
            sb.append(TaskType.EPIC);
        } else if (task instanceof Subtask) {
            sb.append(TaskType.SUBTASK);
        } else {
            sb.append(TaskType.TASK);
        }
        sb.append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        } else {
            sb.append("");
        }
        return sb.toString();
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        // по типам
        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Не удалось преобразовать");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());// сделал через readAllLines
            if (lines.isEmpty()) return manager;
            lines = lines.subList(1, lines.size());
            for (String line : lines) {
                if (line.isBlank()) continue;
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.getEpics().put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.getSubtasks().put(task.getId(), (Subtask) task);
                    Epic epic = manager.getEpics().get(((Subtask) task).getEpicId());
                    if (epic != null) {
                        epic.addsubtask(task.getId());
                    }
                } else {
                    manager.getTasks().put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось преобразовать", e);
        }
        return manager;
    }
}
