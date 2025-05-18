import manager.TaskManager;
import manager.Managers;
import task.Task;
import task.Epic;
import task.Subtask;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = (TaskManager) Managers.getDefault();

        taskManager.createTask("Сделать ДЗ", "Сделать ТЗ по 4 спринту в практикуме");
        int epic1Id = taskManager.createEpic("Собрать книги в отпуск", "Перед отпуском нужно купить книги");
        taskManager.createSubtask("Купить умные книги", "Купить книги для развития", epic1Id);
        taskManager.createSubtask("Купить смешные книги", "Купить книги для веселья", epic1Id);
        taskManager.createSubtask("Купить грустные книги", "Купить книги для погрустить", epic1Id);

        taskManager.getTaskById(1);      // 1
        taskManager.getEpicById(epic1Id); // 2
        taskManager.getSubtaskById(3);   // 3
        taskManager.getSubtaskById(3);   // 4
        taskManager.getSubtaskById(3);   // 5
        taskManager.getSubtaskById(3);   // 6
        taskManager.getSubtaskById(3);   // 7
        taskManager.getTaskById(1);      // 8
        taskManager.getEpicById(epic1Id);// 9
        taskManager.getSubtaskById(3);   // 10
        taskManager.getTaskById(1);      // 11

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            for (Subtask sub : manager.getSubtasks()) {
                if (sub.getEpicId() == epic.getId()) {
                    System.out.println(sub);
                }
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
