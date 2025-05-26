import org.junit.jupiter.api.Test;
import task.*;
import manager.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private final TaskManager manager = new InMemoryTaskManager();

    @Test
    void createTaskWithDurationAndStartTime() {
        Duration duration = Duration.ofMinutes(90);
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 25, 10, 0);

        int taskId = manager.createTask("Task 1", "Desc");
        Task task = manager.getTask(taskId);

        task.setDuration(duration);
        task.setStartTime(startTime);

        assertEquals(duration, task.getDuration());
        assertEquals(startTime, task.getStartTime());
        assertEquals(startTime.plus(duration), task.getEndTime());
    }

    @Test
    void createSubtaskWithDurationAndStartTime() {
        int epicId = manager.createEpic("Epic", "Epic Desc");
        Duration duration = Duration.ofMinutes(60);
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 25, 12, 0);

        int subtaskId = manager.createSubtask("Sub", "SubDesc", Status.DONE, epicId, duration, startTime);
        Subtask subtask = manager.getSubtask(subtaskId);

        assertEquals(duration, subtask.getDuration());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(startTime.plus(duration), subtask.getEndTime());
    }

    @Test
    void epicDurationAndTimeAreCalculated() {
        int epicId = manager.createEpic("Epic", "Epic Desc");

        Duration duration1 = Duration.ofMinutes(30);
        LocalDateTime start1 = LocalDateTime.of(2024, 5, 25, 11, 0);
        manager.createSubtask("Sub1", "Desc1", Status.NEW, epicId, duration1, start1);

        Duration duration2 = Duration.ofMinutes(70);
        LocalDateTime start2 = LocalDateTime.of(2024, 5, 25, 14, 0);
        manager.createSubtask("Sub2", "Desc2", Status.DONE, epicId, duration2, start2);

        Epic epic = manager.getEpic(epicId);

        assertEquals(duration1.plus(duration2), epic.getDuration());
        assertEquals(start1, epic.getStartTime());
        assertEquals(start2.plus(duration2), epic.getEndTime());
    }

    @Test
    void getAllTasksReturnsCollectionOfTasks() {
        manager.createTask("T1", "D1");
        manager.createTask("T2", "D2");
        Collection<Task> tasks = manager.getTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void getSubtasksByEpicIdReturnsCorrectSubtasks() {
        int epicId = manager.createEpic("EpicTest", "Desc");
        manager.createSubtask("Sub1", "Desc1", Status.NEW, epicId, Duration.ofMinutes(10), LocalDateTime.now());
        manager.createSubtask("Sub2", "Desc2", Status.DONE, epicId, Duration.ofMinutes(10), LocalDateTime.now());
        List<Subtask> subtasks = manager.getSubtasksByEpicId(epicId);
        assertEquals(2, subtasks.size());
    }

    @Test
    void getTasksAllowsIndexingViaArrayList() {
        manager.createTask("Alpha", "A");
        manager.createTask("Beta", "B");
        Collection<Task> tasks = manager.getTasks();
        Task first = new ArrayList<>(tasks).get(0);
        assertNotNull(first);
    }
}