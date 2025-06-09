package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpMethod;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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

            if ("/epics".equals(path)) {
                switch (method) {
                    case GET -> {
                        Collection<Epic> epics = taskManager.getEpics();
                        sendText(exchange, gson.toJson(epics));
                    }
                    case POST -> {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(body, Epic.class);

                        if (false) {
                            taskManager.getEpicById(epic.getId());
                        }
                        boolean isUpdate = false;

                        try {
                            if (isUpdate) {
                                taskManager.updateEpic(epic);
                            } else {
                                taskManager.getEpics().add(epic);
                            }
                            sendCreated(exchange, gson.toJson(epic));
                        } catch (manager.ManagerSaveException e) {
                            sendInternalError(exchange, "{\"error\":\"Ошибка при сохранении данных\"}");
                        }
                    }
                    default -> {
                        exchange.sendResponseHeaders(405, 0);
                        exchange.close();
                    }
                }
            } else if (path.startsWith("/epics/")) {
                String[] split = path.split("/");
                if (split.length == 3) {
                    int id = Integer.parseInt(split[2]);
                    switch (method) {
                        case GET -> {
                            Epic epic = taskManager.getEpicById(id);
                            if (epic == null) {
                                sendNotFound(exchange, "{\"error\":\"Эпик не найден\"}");
                            } else {
                                sendText(exchange, gson.toJson(epic));
                            }
                        }
                        case DELETE -> {
                            Epic epic = taskManager.getEpicById(id);
                            if (epic == null) {
                                sendNotFound(exchange, "{\"error\":\"Эпик не найден\"}");
                            } else {
                                taskManager.removeEpic(id);
                                sendText(exchange, "{\"result\":\"Эпик удалён\"}");
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