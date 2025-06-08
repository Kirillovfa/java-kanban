package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(Task::getId)
    );
    protected int nextId = 1;

    protected int generateId() {
        return nextId++;
    }

    @Override
    public Collection<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Collection<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Collection<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.values().forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
        epics.values().forEach(epic -> updateEpicStatusAndTime(epic));
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int createTask(String name, String description, Duration duration, LocalDateTime startTime) {
        Task task = new Task(generateId(), name, description, Status.NEW, duration, startTime);
        if (hasIntersection(task))
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей");
        tasks.put(task.getId(), task);
        if (startTime != null) prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int createEpic(String name, String description) {
        Epic epic = new Epic(generateId(), name, description, Status.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        Epic epic = epics.get(epicId);
        if (epic == null) throw new IllegalArgumentException("Epic не найден");
        Subtask subtask = new Subtask(generateId(), name, description, status, epicId, duration, startTime);
        if (hasIntersection(subtask))
            throw new IllegalArgumentException("Сабтаска пересекается по времени с другой задачей");
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        if (startTime != null) prioritizedTasks.add(subtask);
        updateEpicStatusAndTime(epic);
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) throw new IllegalArgumentException("Task not found");
        prioritizedTasks.remove(tasks.get(task.getId()));
        if (hasIntersection(task))
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей");
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) throw new IllegalArgumentException("Epic not found");
        updateEpicStatusAndTime(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) throw new IllegalArgumentException("Subtask not found");
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        if (hasIntersection(subtask))
            throw new IllegalArgumentException("Сабтаска пересекается по времени с другой задачей");
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);
        updateEpicStatusAndTime(epic);
    }

    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null && removed.getStartTime() != null) prioritizedTasks.remove(removed);
    }

    @Override
    public void removeEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            removedEpic.getSubtaskIds().forEach(subId -> {
                Subtask subtask = subtasks.remove(subId);
                if (subtask != null && subtask.getStartTime() != null) prioritizedTasks.remove(subtask);
            });
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null && removed.getStartTime() != null) prioritizedTasks.remove(removed);
        if (removed != null) {
            Epic epic = epics.get(removed.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatusAndTime(epic);
            }
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return subtasks.values().stream()
                .filter(st -> st.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    protected void updateEpicStatusAndTime(Epic epic) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            return;
        }
        long doneCount = epicSubtasks.stream().filter(st -> st.getStatus() == Status.DONE).count();
        long newCount = epicSubtasks.stream().filter(st -> st.getStatus() == Status.NEW).count();

        if (doneCount == epicSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else if (newCount == epicSubtasks.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        epic.updateTimesAndStatus(epicSubtasks);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean hasIntersection(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) return false;
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> t.getStartTime() != null && t.getDuration() != null)
                .anyMatch(t -> isIntersection(taskStart, taskEnd, t.getStartTime(), t.getEndTime()));
    }

    public static boolean isIntersection(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public abstract List<Task> getHistory();
}