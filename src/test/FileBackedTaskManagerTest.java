package test;

import manager.*;
import task.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    File tempFile;
    FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        // чистим файл
        if (tempFile.exists()) {
            tempFile.delete();
            tempFile.createNewFile();
        }
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void saveAndLoadEmptyFile() {

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loaded.getEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loaded.getSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void saveAndLoadSeveralTasks() {
        manager.createTask("Task1", "Desc1");
        int epicId = manager.createEpic("Epic1", "EpicDesc1");
        manager.createSubtask("Sub1", "SubDesc1", epicId);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getTasks().size(), "Должна быть одна обычная задача");
        assertEquals(1, loaded.getEpics().size(), "Должен быть один эпик");
        assertEquals(1, loaded.getSubtasks().size(), "Должна быть одна подзадача");

        Task task = loaded.getTasks().values().iterator().next();
        assertEquals("Task1", task.getName());
        Epic epic = loaded.getEpics().values().iterator().next();
        assertEquals("Epic1", epic.getName());
        Subtask subtask = loaded.getSubtasks().values().iterator().next();
        assertEquals("Sub1", subtask.getName());
        assertEquals(epicId, subtask.getEpicId());
    }

    @Test
    void loadFromFileWithMultipleTasks() throws IOException {
        manager.createTask("T1", "D1");
        manager.createTask("T2", "D2");
        int epicId = manager.createEpic("Epic", "Edesc");
        manager.createSubtask("S1", "Sdesc1", epicId);
        manager.createSubtask("S2", "Sdesc2", epicId);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loaded.getTasks().size(), "Должно быть две задачи");
        assertEquals(1, loaded.getEpics().size(), "Должен быть один эпик");
        assertEquals(2, loaded.getSubtasks().size(), "Должно быть две подзадачи");

        Epic epic = loaded.getEpics().values().iterator().next();
        List<Integer> subtaskIds = epic.getsubtasksIds();
        assertEquals(2, subtaskIds.size(), "У эпика должно быть две подзадачи");
    }

    @Test
    void loadedManagerBehavesLikeInMemoryManager() {
        manager.createTask("T", "D");
        int epicId = manager.createEpic("E", "Ed");
        manager.createSubtask("S", "Sd", epicId);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        Task t = loaded.getTaskById(1);
        assertNotNull(t, "Задача должна быть найдена по id");

        Epic e = loaded.getEpicById(epicId);
        assertNotNull(e, "Эпик должен быть найден по id");

        Subtask s = loaded.getSubtaskById(3);
        assertNotNull(s, "Сабтаска должна быть найдена по id");
    }
}