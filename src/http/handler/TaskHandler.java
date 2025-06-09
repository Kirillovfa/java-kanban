package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpMethod;
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
            HttpMethod method;
            try {
                method = HttpMethod.valueOf(exchange.getRequestMethod());
            } catch (IllegalArgumentException e) {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if ("/tasks".equals(path)) {
                switch (method) {
                    case GET -> {
                        Collection<Task> tasks = taskManager.getTasks();
                        sendText(exchange, gson.toJson(tasks));
                    }
                    case POST -> {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(body, Task.class);

                        int id = taskManager.createTask(
                                task.getName(),
                                task.getDescription(),
                                task.getDuration(),
                                task.getStartTime()
                        );
                        Task createdTask = taskManager.getTaskById(id);
                        sendCreated(exchange, gson.toJson(createdTask));
                    }
                    default -> {
                        exchange.sendResponseHeaders(405, 0);
                        exchange.close();
                    }
                }
            } else if (path.startsWith("/tasks/")) {
                String[] split = path.split("/");
                if (split.length == 3) {
                    int id = Integer.parseInt(split[2]);
                    switch (method) {
                        case GET -> {
                            Task task = taskManager.getTaskById(id);
                            if (task == null) {
                                sendNotFound(exchange, "{\"error\":\"Задача не найдена\"}");
                            } else {
                                sendText(exchange, gson.toJson(task));
                            }
                        }
                        case DELETE -> {
                            Task task = taskManager.getTaskById(id);
                            if (task == null) {
                                sendNotFound(exchange, "{\"error\":\"Задача не найдена\"}");
                            } else {
                                taskManager.removeTask(id);
                                sendText(exchange, "{\"result\":\"Задача удалена\"}");
                            }
                        }
                        default -> {
                            exchange.sendResponseHeaders(405, 0);
                            exchange.close();
                        }
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