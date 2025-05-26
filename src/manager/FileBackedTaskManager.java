package manager;

import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile();
    }

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить", e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(getTaskType(task)).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        } else {
            sb.append("");
        }
        sb.append(",");
        sb.append(task.getDuration() != null ? task.getDuration().toMinutes() : "");
        sb.append(",");
        sb.append(task.getStartTime() != null ? task.getStartTime() : "");
        return sb.toString();
    }

    private String getTaskType(Task task) {
        if (task instanceof Epic) return "EPIC";
        if (task instanceof Subtask) return "SUBTASK";
        return "TASK";
    }

    private Task fromString(String value) {
        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicIdString = fields[5];
        String durationString = fields.length > 6 ? fields[6] : "";
        String startTimeString = fields.length > 7 ? fields[7] : "";

        Duration duration = durationString.isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(durationString));
        LocalDateTime startTime = startTimeString.isEmpty() ? null : LocalDateTime.parse(startTimeString);

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status, duration, startTime);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(epicIdString);
                return new Subtask(id, name, description, status, epicId, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private void loadFromFile() {
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Task task = fromString(line);
                if (task instanceof Epic) {
                    epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    subtasks.put(subtask.getId(), subtask);
                    if (epics.containsKey(subtask.getEpicId())) {
                        epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
                    }
                } else {
                    tasks.put(task.getId(), task);
                }
            }
            for (Epic epic : epics.values()) {
                updateEpic(epic.getId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось загрузить", e);
        }
    }

    @Override
    public int createTask(String name, String description) {
        int id = super.createTask(name, description);
        save();
        return id;
    }

    @Override
    public int createEpic(String name, String description) {
        int id = super.createEpic(name, description);
        save();
        return id;
    }

    @Override
    public int createSubtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        int id = super.createSubtask(name, description, status, epicId, duration, startTime);
        save();
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }
}