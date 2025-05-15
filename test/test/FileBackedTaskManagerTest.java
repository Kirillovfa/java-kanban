package manager;

import org.junit.jupiter.api.*;
import task.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws Exception {
        file = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        file.delete();
    }

    @Test
    void testSaveAndLoad() throws Exception {
        int epicId = manager.createEpic("Epic1", "EpicDesc");
        manager.createTask("Task1", "TaskDesc");
        manager.createSubtask("Subtask1", "SubDesc", epicId);

        manager.save();

        List<String> lines = Files.readAllLines(file.toPath());
        assertTrue(lines.size() > 1);
        assertEquals("id,type,name,status,description,epic", lines.get(0));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getTasks().size(), loadedManager.getTasks().size());
        assertEquals(manager.getEpics().size(), loadedManager.getEpics().size());
        assertEquals(manager.getSubtasks().size(), loadedManager.getSubtasks().size());

        Task loadedTask = loadedManager.getTasks().values().iterator().next();
        assertEquals("Task1", loadedTask.getName());

        Epic loadedEpic = loadedManager.getEpics().get(epicId);
        assertNotNull(loadedEpic);
        assertEquals("Epic1", loadedEpic.getName());
        assertEquals(1, loadedEpic.getSubtaskIds().size());

        Subtask loadedSubtask = loadedManager.getSubtasks().values().iterator().next();
        assertEquals("Subtask1", loadedSubtask.getName());
        assertEquals(epicId, loadedSubtask.getEpicId());
    }

    @Test
    void testCreateAndDelete() {
        manager.createTask("Task", "Desc");
        int epicId = manager.createEpic("Epic", "Desc");
        manager.createSubtask("Subtask", "Desc", epicId);

        assertEquals(1, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getSubtasks().size());

        manager.deleteTask(1);
        assertEquals(0, manager.getTasks().size());

        manager.deleteSubtask(3);
        assertEquals(0, manager.getSubtasks().size());
        assertTrue(manager.getEpics().get(epicId).getSubtaskIds().isEmpty());

        manager.deleteEpicById(epicId);
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void testUpdateTask() {
        manager.createTask("Task", "Desc");
        Task task = manager.getTasks().get(1);
        task.setStatus(Status.DONE);
        manager.updateTask(task);

        Task updated = manager.getTasks().get(1);
        assertEquals(Status.DONE, updated.getStatus());
    }
}