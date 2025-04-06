package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import manager.*;
import task.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
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
    void historyShouldStoreUpToTenTasks() {
        for (int i = 1; i <= 20; i++) {
            Task task = new Task(i, "Task " + i, "Описание " + i, Status.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна состоять максимум из 10 тасок");
        assertEquals(11, history.get(0).getId(), "Таска не стала первой в списке");
    }
}