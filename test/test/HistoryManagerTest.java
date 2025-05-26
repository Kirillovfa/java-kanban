import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addAndGetHistory() {
        Task task1 = new Task(1, "Таска 1", "Описание таски 1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task(2, "Таска 2", "Описание таски 2", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void addSameTaskMovesToEnd() {
        Task task = new Task(1, "Таска 1", "Описание таски 1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void removeTaskFromHistory() {
        Task task1 = new Task(1, "Таска 1", "Описание таски 1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task(2, "Таска 2", "Описание таски 2", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void clearHistory() {
        Task task1 = new Task(1, "Таска 1", "Описание таски 1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.clear();
        assertTrue(historyManager.getHistory().isEmpty());
    }
}