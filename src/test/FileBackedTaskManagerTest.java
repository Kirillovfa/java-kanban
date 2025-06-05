package test;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        file.delete();
    }

    @Test
    void saveAndLoadSingleTask() {
        int taskId = manager.createTask(
                "Test Task",
                "Task description",
                Duration.ofMinutes(30),
                LocalDateTime.of(2025, 5, 28, 12, 0)
        );
        Task loaded = manager.getTaskById(taskId);

        assertEquals("Test Task", loaded.getName());
        assertEquals("Task description", loaded.getDescription());

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        Task loadedAgain = loadedManager.getTaskById(taskId);
        assertEquals(loaded, loadedAgain);
    }

    @Test
    void saveAndLoadEpicWithSubtasks() {
        int epicId = manager.createEpic(
                "Epic 1",
                "Epic description"
        );

        int sub1Id = manager.createSubtask(
                "Subtask 1",
                "Description 1",
                Status.NEW,
                epicId,
                Duration.ofMinutes(45),
                LocalDateTime.of(2025, 5, 29, 10, 0)
        );
        int sub2Id = manager.createSubtask(
                "Subtask 2",
                "Description 2",
                Status.DONE,
                epicId,
                Duration.ofMinutes(30),
                LocalDateTime.of(2025, 5, 29, 11, 0)
        );

        Epic loadedEpic = manager.getEpicById(epicId);
        assertNotNull(loadedEpic);
        assertEquals(2, loadedEpic.getSubtaskIds().size());

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        Epic loadedEpicAgain = loadedManager.getEpicById(epicId);
        assertNotNull(loadedEpicAgain);
        assertEquals(loadedEpic.getSubtaskIds(), loadedEpicAgain.getSubtaskIds());

        Subtask loadedSub1 = loadedManager.getSubtaskById(sub1Id);
        Subtask loadedSub2 = loadedManager.getSubtaskById(sub2Id);
        assertEquals(manager.getSubtaskById(sub1Id), loadedSub1);
        assertEquals(manager.getSubtaskById(sub2Id), loadedSub2);
    }

    @Test
    void saveAndLoadMultipleTasks() {
        int id1 = manager.createTask(
                "Task 1", "desc1",
                Duration.ofMinutes(20),
                LocalDateTime.of(2025, 5, 28, 9, 0)
        );
        int id2 = manager.createTask(
                "Task 2", "desc2",
                Duration.ofMinutes(30),
                LocalDateTime.of(2025, 5, 28, 10, 0)
        );

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        assertNotNull(loadedManager.getTaskById(id1));
        assertNotNull(loadedManager.getTaskById(id2));
    }
}