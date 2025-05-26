import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static final File FILE = new File("test-tasks.csv");

    @AfterEach
    void cleanup() {
        if (FILE.exists()) {
            FILE.delete();
        }
    }

    @Test
    void saveAndLoadTaskWithDurationAndStartTime() {
        FileBackedTaskManager manager = new FileBackedTaskManager(FILE);

        Duration duration = Duration.ofMinutes(45);
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 25, 10, 30);
        int epicId = manager.createEpic("Эпик 1", "Описание эпик 1");
        int subtaskId = manager.createSubtask("Сабтаска", "Описание сабтаски 2", Status.NEW, epicId, duration, startTime);

        Subtask sub = manager.getSubtask(subtaskId);
        assertEquals(duration, sub.getDuration());
        assertEquals(startTime, sub.getStartTime());
        assertEquals(startTime.plus(duration), sub.getEndTime());

        FileBackedTaskManager loaded = new FileBackedTaskManager(FILE);

        Subtask loadedSub = loaded.getSubtask(subtaskId);
        assertNotNull(loadedSub);
        assertEquals(duration, loadedSub.getDuration());
        assertEquals(startTime, loadedSub.getStartTime());
        assertEquals(startTime.plus(duration), loadedSub.getEndTime());
    }

    @Test
    void epicDurationAndTimeAfterReload() {
        FileBackedTaskManager manager = new FileBackedTaskManager(FILE);

        int epicId = manager.createEpic("Эпик 1", "Описание эпик 1");

        Duration d1 = Duration.ofMinutes(10);
        Duration d2 = Duration.ofMinutes(50);
        LocalDateTime st1 = LocalDateTime.of(2024, 5, 25, 9, 0);
        LocalDateTime st2 = LocalDateTime.of(2024, 5, 25, 12, 0);

        manager.createSubtask("Сабтаска 1", "Описание сабтаски 1", Status.NEW, epicId, d1, st1);
        manager.createSubtask("Сабтаска 2", "Описание сабтаски 2", Status.DONE, epicId, d2, st2);

        Epic epic = manager.getEpic(epicId);
        assertEquals(d1.plus(d2), epic.getDuration());
        assertEquals(st1, epic.getStartTime());
        assertEquals(st2.plus(d2), epic.getEndTime());

        FileBackedTaskManager loaded = new FileBackedTaskManager(FILE);
        Epic loadedEpic = loaded.getEpic(epicId);
        assertEquals(d1.plus(d2), loadedEpic.getDuration());
        assertEquals(st1, loadedEpic.getStartTime());
        assertEquals(st2.plus(d2), loadedEpic.getEndTime());
    }
}