import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import manager.*;
import task.*;

import java.util.List;

public class TaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Name", "Desc", Status.NEW);
        Task task2 = new Task(1, "Name", "Desc", Status.NEW);
        assertEquals(task1, task2, "экземпляры класса Task не равны друг другу, если равен их id");
    }

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(1, "Epic", "EpicDesc");
        Epic epic2 = new Epic(1, "Epic", "EpicDesc");
        assertEquals(epic1, epic2, "наследники класса Task не равны друг другу, если равен их id");
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Sub", "Desc", Status.NEW, 100);
        Subtask subtask2 = new Subtask(1, "Sub", "Desc", Status.NEW, 100);
        assertEquals(subtask1, subtask2, "наследники класса Task не равны друг другу, если равен их id");
    }

    @Test
    void epicCannotContainItself() {
        Epic epic = new Epic(1, "Epic", "Self-reference check");
        epic.addsubtask(1); // добавляем ID самого себя
        assertFalse(epic.getsubtasksIds().contains(1), "Epic может быть своей подзадачей");
    }

    @Test
    void subtaskCannotReferenceItselfAsEpic() {
        Subtask subtask = new Subtask(1, "Subtask", "Проверка", Status.NEW, 1);
        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Сабтаска может ссылаться на саму себя как на epic");
    }

    @Test
    void managersShouldReturnInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager);
        assertNotNull(historyManager);
    }

    @Test
    void inMemoryTaskManagerShouldCreateAndRetrieveTasks() {
        taskManager.createTask("Task", "Desc");
        assertNotNull(taskManager.getTaskById(1));
    }

}