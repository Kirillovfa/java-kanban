package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        int taskId1 = manager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.of(2024, 5, 27, 10, 0));
        int taskId2 = manager.createTask("Задача 2", "Описание задачи 2", Duration.ofMinutes(60), LocalDateTime.of(2024, 5, 27, 12, 0));
        int epicId = manager.createEpic("Эпик 1", "Описание эпика");
        int subtaskId1 = manager.createSubtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epicId, Duration.ofMinutes(20), LocalDateTime.of(2024, 5, 27, 11, 0));
        int subtaskId2 = manager.createSubtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epicId, Duration.ofMinutes(40), LocalDateTime.of(2024, 5, 27, 15, 0));

        System.out.println("Все задачи:");
        manager.getTasks().forEach(System.out::println);

        System.out.println("Все эпики:");
        manager.getEpics().forEach(System.out::println);

        System.out.println("Все подзадачи:");
        manager.getSubtasks().forEach(System.out::println);

        System.out.println("Подзадачи эпика " + epicId + ":");
        manager.getEpicSubtasks(epicId).forEach(System.out::println);

        System.out.println("Задачи по приоритету:");
        manager.getPrioritizedTasks().forEach(System.out::println);

        manager.getTaskById(taskId1);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId1);

        System.out.println("История:");
        manager.getHistoryManager().getHistory().forEach(System.out::println);
    }
}