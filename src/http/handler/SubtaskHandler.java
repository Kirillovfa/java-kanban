package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("/subtasks".equals(path)) {
                if ("GET".equals(method)) {
                    Collection<Subtask> subtasks = taskManager.getSubtasks();
                    sendText(exchange, gson.toJson(subtasks));
                } else if ("POST".equals(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    boolean isUpdate = (taskManager.getSubtaskById(subtask.getId()) != null);

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
                } else {
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
            } else if (path.startsWith("/subtasks/")) {
                String[] split = path.split("/");
                if (split.length == 3) {
                    int id = Integer.parseInt(split[2]);
                    if ("GET".equals(method)) {
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask == null) {
                            sendNotFound(exchange, "{\"error\":\"Подзадача не найдена\"}");
                        } else {
                            sendText(exchange, gson.toJson(subtask));
                        }
                    } else if ("DELETE".equals(method)) {
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask == null) {
                            sendNotFound(exchange, "{\"error\":\"Подзадача не найдена\"}");
                        } else {
                            taskManager.removeSubtask(id);
                            sendText(exchange, "{\"result\":\"Подзадача удалена\"}");
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