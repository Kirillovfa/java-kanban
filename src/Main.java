public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask("Сделать ДЗ", "Сделать ТЗ по 4 спринту в практикуме");
        Epic epic1 = taskManager.createEpic("Собрать книги в отпуск", "Перед отпуском нужно купить книги");
        Subtask sub1 = taskManager.createSubtask("Купить умные книги", "Купить книги для развития", epic1.getId());
        Subtask sub2 = taskManager.createSubtask("Купить смешные книги", "Купить книги для веселья", epic1.getId());
        Subtask sub3 = taskManager.createSubtask("Купить грустные книги", "Купить книги для погрустить", epic1.getId());

        taskManager.printTasks();
        taskManager.printEpics();
        taskManager.printsubtasks();

        sub1.setStatus(Status.DONE);
        taskManager.updateSubtask(sub1);
        taskManager.printEpics();
    }
}