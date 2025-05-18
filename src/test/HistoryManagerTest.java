package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import manager.*;
import task.*;

import java.util.List;

public class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void historyManagerShouldPreserveOriginalTaskData() {
        Task task = new Task(1, "Original", "Desc", Status.NEW);
        historyManager.add(task);
        task.setName("Changed");
        String newName = historyManager.getHistory().get(0).getName();
        assertNotEquals("Original", newName);
    }

    @Test
    void add() {
        Task task = new Task(1, "Task", "Desc", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(1, history.size(), "Таска не добавилась в историю");
    }

    @Test
    void taskNotAddTwice() {
        Task task = new Task(1, "Имя таски", "Описание таски", Status.NEW);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "1 задача может быть не больше 1 раза в истории");
    }

    @Test
    void historyNotLimited() {
        for (int i = 1; i <= 30; i++) {
            Task task = new Task(i, "Имя таски " + i, "Описание таски " + i, Status.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(30, history.size(), "Не должно быть ограничений на размер истории");
    }
}