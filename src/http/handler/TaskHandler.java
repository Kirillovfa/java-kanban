package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("/tasks".equals(path)) {
                if ("GET".equals(method)) {
                    // Получить все задачи
                    Collection<Task> tasks = taskManager.getTasks();
                    sendText(exchange, gson.toJson(tasks));
                } else if ("POST".equals(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);

                    boolean isUpdate = (taskManager.getTaskById(task.getId()) != null);

                    try {
                        if (isUpdate) {
                            taskManager.updateTask(task);
                        } else {
                            int id = taskManager.createTask(
                                    task.getName(),
                                    task.getDescription(),
                                    task.getDuration(),
                                    task.getStartTime()
                            );
                            task = taskManager.getTaskById(id);
                        }
                        sendCreated(exchange, gson.toJson(task));
                    } catch (manager.ManagerSaveException e) {
                        sendInternalError(exchange, "{\"error\":\"Ошибка при сохранении данных\"}");
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
            } else if (path.startsWith("/tasks/")) {
                String[] split = path.split("/");
                if (split.length == 3) {
                    int id = Integer.parseInt(split[2]);
                    if ("GET".equals(method)) {
                        Task task = taskManager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange, "{\"error\":\"Задача не найдена\"}");
                        } else {
                            sendText(exchange, gson.toJson(task));
                        }
                    } else if ("DELETE".equals(method)) {
                        Task task = taskManager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange, "{\"error\":\"Задача не найдена\"}");
                        } else {
                            taskManager.removeTask(id);
                            sendText(exchange, "{\"result\":\"Задача удалена\"}");
                        }
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                        exchange.close();
                    }
                } else {
                    sendNotFound(exchange, "{\"error\":\"Неверный путь\"}");
                }
            } else {
                sendNotFound(exchange, "{\"error\":\"Неверный путь\"}");
            }
        } catch (Exception e) {
            sendInternalError(exchange, "{\"error\":\"Ошибка при обработке запроса: " + e.getMessage() + "\"}");
        }
    }
}