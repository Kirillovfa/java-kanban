package test;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    public abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void shouldCreateAndReturnTask() {
        int id = manager.createTask("Task", "Desc", Duration.ofMinutes(5), LocalDateTime.now());
        Task task = manager.getTaskById(id);
        assertNotNull(task);
        assertEquals("Task", task.getName());
        assertEquals("Desc", task.getDescription());
    }

    @Test
    void shouldRemoveTaskById() {
        int id = manager.createTask("Task", "Desc", Duration.ofMinutes(5), LocalDateTime.now());
        manager.removeTask(id);
        assertNull(manager.getTaskById(id));
    }

    @Test
    void shouldUpdateTaskStatus() {
        int id = manager.createTask("Task", "Desc", Duration.ofMinutes(5), LocalDateTime.now());
        Task task = manager.getTaskById(id);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(id).getStatus());
    }

    @Test
    void shouldReturnPrioritizedTasksSortedByStartTime() {
        manager.createTask("Task1", "Desc", Duration.ofMinutes(5), LocalDateTime.of(2024,1,1,10,0));
        manager.createTask("Task2", "Desc", Duration.ofMinutes(5), LocalDateTime.of(2024,1,1,9,0));
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertTrue(prioritized.get(0).getStartTime().isBefore(prioritized.get(1).getStartTime()));
    }

    @Test
    void shouldThrowExceptionIfTasksIntersect() {
        manager.createTask("Task1", "Desc", Duration.ofMinutes(60), LocalDateTime.of(2024,1,1,10,0));
        assertThrows(IllegalArgumentException.class,
                () -> manager.createTask("Task2", "Desc", Duration.ofMinutes(30), LocalDateTime.of(2024,1,1,10,30)));
    }

    @Test
    void shouldCreateAndReturnEpic() {
        int epicId = manager.createEpic("Epic", "EpicDesc");
        Epic epic = manager.getEpicById(epicId);
        assertNotNull(epic);
        assertEquals("Epic", epic.getName());
    }

    @Test
    void shouldRemoveEpicByIdAndItsSubtasks() {
        int epicId = manager.createEpic("Epic", "EpicDesc");
        int subId = manager.createSubtask("Sub", "SubDesc", Status.NEW, epicId, Duration.ofMinutes(5), LocalDateTime.now());
        manager.removeEpic(epicId);
        assertNull(manager.getEpicById(epicId));
        assertNull(manager.getSubtaskById(subId));
    }

    @Test
    void shouldCreateAndReturnSubtaskAndLinkToEpic() {
        int epicId = manager.createEpic("Epic", "EpicDesc");
        int subId = manager.createSubtask("Sub", "SubDesc", Status.NEW, epicId, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub = manager.getSubtaskById(subId);
        assertNotNull(sub);
        assertEquals(epicId, sub.getEpicId());
        assertTrue(manager.getEpicById(epicId).getSubtaskIds().contains(subId));
    }

    @Test
    void shouldRemoveSubtaskAndNotAffectEpic() {
        int epicId = manager.createEpic("Epic", "EpicDesc");
        int subId = manager.createSubtask("Sub", "SubDesc", Status.NEW, epicId, Duration.ofMinutes(5), LocalDateTime.now());
        manager.removeSubtask(subId);
        assertNull(manager.getSubtaskById(subId));
        assertFalse(manager.getEpicById(epicId).getSubtaskIds().contains(subId));
    }

    @Test
    void epicStatusAllNew() {
        int epicId = manager.createEpic("Epic", "Desc");
        manager.createSubtask("Sub1", "Desc", Status.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubtask("Sub2", "Desc", Status.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        assertEquals(Status.NEW, manager.getEpicById(epicId).getStatus());
    }

    @Test
    void epicStatusAllDone() {
        int epicId = manager.createEpic("Epic", "Desc");
        manager.createSubtask("Sub1", "Desc", Status.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubtask("Sub2", "Desc", Status.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        assertEquals(Status.DONE, manager.getEpicById(epicId).getStatus());
    }

    @Test
    void epicStatusMixedNewAndDone() {
        int epicId = manager.createEpic("Epic", "Desc");
        manager.createSubtask("Sub1", "Desc", Status.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubtask("Sub2", "Desc", Status.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicId).getStatus());
    }

    @Test
    void epicStatusAllInProgress() {
        int epicId = manager.createEpic("Epic", "Desc");
        manager.createSubtask("Sub1", "Desc", Status.IN_PROGRESS, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        manager.createSubtask("Sub2", "Desc", Status.IN_PROGRESS, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicId).getStatus());
    }

    @Test
    void shouldGetEpicSubtasks() {
        int epicId = manager.createEpic("Epic", "EpicDesc");
        int sub1 = manager.createSubtask("Sub1", "Desc", Status.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        int sub2 = manager.createSubtask("Sub2", "Desc", Status.DONE, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        List<Subtask> subs = manager.getEpicSubtasks(epicId);
        assertEquals(2, subs.size());
        assertTrue(subs.stream().anyMatch(s -> s.getId() == sub1));
        assertTrue(subs.stream().anyMatch(s -> s.getId() == sub2));
    }
}