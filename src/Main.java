import manager.*;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.createTask("Сделать ДЗ", "Сделать ТЗ по 4 спринту в практикуме");

        int epic1Id = taskManager.createEpic("Собрать книги в отпуск", "Перед отпуском нужно купить книги");
        taskManager.createSubtask(
                "Купить умные книги", "Купить книги для развития",
                Status.NEW, epic1Id, Duration.ofMinutes(30), LocalDateTime.of(2024, 5, 25, 15, 0)
        );
        taskManager.createSubtask(
                "Купить смешные книги", "Купить книги для веселья",
                Status.NEW, epic1Id, Duration.ofMinutes(20), LocalDateTime.of(2024, 5, 25, 16, 0)
        );
        taskManager.createSubtask(
                "Купить грустные книги", "Купить книги для погрустить",
                Status.NEW, epic1Id, Duration.ofMinutes(40), LocalDateTime.of(2024, 5, 25, 17, 0)
        );

        // Пример с эпиком и двумя сабтасками
        int epic2Id = taskManager.createEpic("Глобальный проект", "Большое описание эпика");
        int subtask1 = taskManager.createSubtask(
                "Сделать первую часть", "Описание подзадачи 1",
                Status.NEW, epic2Id,
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 5, 25, 10, 0)
        );
        int subtask2 = taskManager.createSubtask(
                "Сделать вторую часть", "Описание подзадачи 2",
                Status.IN_PROGRESS, epic2Id,
                Duration.ofMinutes(90),
                LocalDateTime.of(2024, 5, 25, 12, 30)
        );

        System.out.println("Все эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nВсе сабтаски эпика " + epic1Id + ":");
        for (Subtask sub : taskManager.getSubtasksByEpicId(epic1Id)) {
            System.out.println(sub);
        }

        System.out.println("\nВсе сабтаски эпика " + epic2Id + ":");
        for (Subtask sub : taskManager.getSubtasksByEpicId(epic2Id)) {
            System.out.println(sub);
        }

        System.out.println("\nИстория просмотров:");
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(taskManager.getSubtask(subtask1));
        historyManager.add(taskManager.getSubtask(subtask2));
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}