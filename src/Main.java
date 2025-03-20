import ManagerPackage.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask("Сделать ДЗ", "Сделать ТЗ по 4 спринту в практикуме");
        int epic1Id = taskManager.createEpic("Собрать книги в отпуск", "Перед отпуском нужно купить книги");
        taskManager.createSubtask("Купить умные книги", "Купить книги для развития", epic1Id);
        taskManager.createSubtask("Купить смешные книги", "Купить книги для веселья", epic1Id);
        taskManager.createSubtask("Купить грустные книги", "Купить книги для погрустить", epic1Id);
    }
}