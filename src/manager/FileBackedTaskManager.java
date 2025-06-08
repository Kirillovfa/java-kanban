package manager;

import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            writer.write("\n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private void loadFromFile() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean isHistory = false;
            String line = reader.readLine();
            List<String> historyIds = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    isHistory = true;
                    continue;
                }
                if (!isHistory) {
                    Task task = fromString(line);
                    switch (task.getTaskType()) {
                        case TASK:
                            tasks.put(task.getId(), task);
                            if (task.getStartTime() != null) prioritizedTasks.add(task);
                            break;
                        case EPIC:
                            epics.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            subtasks.put(task.getId(), (Subtask) task);
                            if (task.getStartTime() != null) prioritizedTasks.add(task);
                            break;
                    }
                } else {
                    historyIds = Arrays.asList(line.split(","));
                }
            }
            // Восстановление связей сабтасков и эпиков
            for (Subtask sub : subtasks.values()) {
                Epic epic = epics.get(sub.getEpicId());
                if (epic != null) {
                    epic.addSubtask(sub.getId());
                }
            }
            for (Epic epic : epics.values()) {
                updateEpicStatusAndTime(epic);
            }
            if (!historyIds.isEmpty()) {
                for (String idStr : historyIds) {
                    if (idStr.isBlank()) continue;
                    int id = Integer.parseInt(idStr);
                    if (tasks.containsKey(id)) {
                        historyManager.add(tasks.get(id));
                    } else if (epics.containsKey(id)) {
                        historyManager.add(epics.get(id));
                    } else if (subtasks.containsKey(id)) {
                        historyManager.add(subtasks.get(id));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }
    }

    private String toString(Task task) {
        if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask st = (Subtask) task;
            return String.join(",", String.valueOf(st.getId()), st.getTaskType().toString(), st.getName(), st.getStatus().toString(), st.getDescription(), String.valueOf(st.getEpicId()), st.getDuration() != null ? String.valueOf(st.getDuration().toMinutes()) : "", st.getStartTime() != null ? st.getStartTime().toString() : "");
        } else if (task.getTaskType() == TaskType.EPIC) {
            return String.join(",", String.valueOf(task.getId()), task.getTaskType().toString(), task.getName(), task.getStatus().toString(), task.getDescription(), "", task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "", task.getStartTime() != null ? task.getStartTime().toString() : "");
        } else {
            return String.join(",", String.valueOf(task.getId()), task.getTaskType().toString(), task.getName(), task.getStatus().toString(), task.getDescription(), "", task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "", task.getStartTime() != null ? task.getStartTime().toString() : "");
        }
    }

    private Task fromString(String value) {
        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicIdString = fields[5];
        String durationString = fields.length > 6 ? fields[6] : "";
        String startTimeString = fields.length > 7 ? fields[7] : "";

        Duration duration = durationString.isEmpty() ? null : Duration.ofMinutes(Long.parseLong(durationString));
        LocalDateTime startTime = startTimeString.isEmpty() ? null : LocalDateTime.parse(startTimeString);

        switch (type) {
            case TASK:
                Task task = new Task(id, name, description, status, duration, startTime);
                if (id >= nextId) nextId = id + 1;
                return task;
            case EPIC:
                Epic epic = new Epic(id, name, description, Status.NEW);
                epic.setStatus(status);
                if (duration != null) epic.setDuration(duration);
                if (startTime != null) epic.setStartTime(startTime);
                if (id >= nextId) nextId = id + 1;
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(epicIdString);
                Subtask subtask = new Subtask(id, name, description, status, epicId, duration, startTime);
                if (id >= nextId) nextId = id + 1;
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String historyToString(HistoryManager manager) {
        return manager.getHistory().stream().map(task -> String.valueOf(task.getId())).collect(Collectors.joining(","));
    }

    @Override
    public int createTask(String name, String description, Duration duration, LocalDateTime startTime) {
        int id = super.createTask(name, description, duration, startTime);
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}