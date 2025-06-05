package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager history;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setup() {
        history = new InMemoryHistoryManager();
        task1 = new Task(1, "Task1", "Desc1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        task2 = new Task(2, "Task2", "Desc2", Status.DONE, Duration.ofMinutes(5), LocalDateTime.now());
        task3 = new Task(3, "Task3", "Desc3", Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now());
    }

    @Test
    void emptyHistory() {
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void addAndGetHistoryNoDuplicates() {
        history.add(task1);
        history.add(task1);
        assertEquals(1, history.getHistory().size());
    }

    @Test
    void removeFromHistoryBeginningMiddleEnd() {
        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task1.getId());
        assertFalse(history.getHistory().contains(task1));

        history.remove(task3.getId());
        assertFalse(history.getHistory().contains(task3));

        history.remove(task2.getId());
        assertTrue(history.getHistory().isEmpty());
    }
}