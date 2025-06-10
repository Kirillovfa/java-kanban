package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpMethod;
import manager.TaskManager;
import task.Task;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
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

            if ("/tasks/history".equals(path)) {
                switch (method) {
                    case GET -> {
                        List<Task> history = taskManager.getHistoryManager().getHistory();
                        sendText(exchange, gson.toJson(history));
                    }
                    default -> {
                        exchange.sendResponseHeaders(405, 0);
                        exchange.close();
                    }
                }
            } else {
                sendNotFound(exchange, "{\"error\":\"Неверный путь\"}");
            }
        } catch (Exception e) {
            sendInternalError(exchange, "{\"error\":\"Ошибка при обработке запроса: " + e.getMessage() + "\"}");
        }
    }
}