package test;

import manager.FileBackedTaskManager;
import manager.TaskManager;
import task.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private TaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void shouldSaveAndLoadSimpleTask() {
        manager.createTask("Test", "Description");
        Task task = manager.getTasks().iterator().next();

        TaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTaskById(task.getId());

        assertNotNull(loadedTask);
        assertEquals(task, loadedTask);
    }

    @Test
    void shouldSaveAndLoadEpicWithSubtasks() {
        int epicId = manager.createEpic("Epic", "Epic Desc");
        manager.createSubtask("Sub1", "Subdesc1", epicId);
        manager.createSubtask("Sub2", "Subdesc2", epicId);

        TaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loaded.getEpicById(epicId);

        assertNotNull(loadedEpic);
        List<Subtask> subtasks = loaded.getSubtasks().stream().filter(s -> s.getEpicId() == epicId).toList();
        assertEquals(2, subtasks.size());
    }

    @Test
    void loadedManagerBehavesLikeOriginal() {
        int epicId = manager.createEpic("Epic", "desc");
        manager.createSubtask("S1", "A", epicId);
        manager.createTask("T1", "B");

        TaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(manager.getTasks().size(), loaded.getTasks().size());
        assertEquals(manager.getEpics().size(), loaded.getEpics().size());
        assertEquals(manager.getSubtasks().size(), loaded.getSubtasks().size());
    }
}