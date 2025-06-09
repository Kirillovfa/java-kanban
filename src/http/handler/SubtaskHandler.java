package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpMethod;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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

            if ("/subtasks".equals(path)) {
                switch (method) {
                    case GET -> {
                        Collection<Subtask> subtasks = taskManager.getSubtasks();
                        sendText(exchange, gson.toJson(subtasks));
                    }
                    case POST -> {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtask = gson.fromJson(body, Subtask.class);

                        boolean isUpdate = false;
                        try {
                            if (isUpdate) {
                                taskManager.updateSubtask(subtask);
                            } else {
                                taskManager.getSubtasks().add(subtask);
                            }
                            sendCreated(exchange, gson.toJson(subtask));
                        } catch (manager.ManagerSaveException e) {
                            sendInternalError(exchange, "{\"error\":\"Ошибка при сохранении данных\"}");
                        }
                    }
                    default -> {
                        exchange.sendResponseHeaders(405, 0);
                        exchange.close();
                    }
                }
            } else if (path.startsWith("/subtasks/")) {
                String[] split = path.split("/");
                if (split.length == 3) {
                    int id = Integer.parseInt(split[2]);
                    switch (method) {
                        case GET -> {
                            Subtask subtask = taskManager.getSubtaskById(id);
                            if (subtask == null) {
                                sendNotFound(exchange, "{\"error\":\"Подзадача не найдена\"}");
                            } else {
                                sendText(exchange, gson.toJson(subtask));
                            }
                        }
                        case DELETE -> {
                            Subtask subtask = taskManager.getSubtaskById(id);
                            if (subtask == null) {
                                sendNotFound(exchange, "{\"error\":\"Подзадача не найдена\"}");
                            } else {
                                taskManager.removeSubtask(id);
                                sendText(exchange, "{\"result\":\"Подзадача удалена\"}");
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