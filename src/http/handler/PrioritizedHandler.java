package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.Collection;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("/prioritized".equals(path)) {
                if ("GET".equals(method)) {
                    // Получить приортетные задачи
                    Collection<Task> prioritized = taskManager.getPrioritizedTasks();
                    sendText(exchange, gson.toJson(prioritized));
                } else {
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
            } else {
                sendNotFound(exchange, "{\"error\":\"Неверный путь\"}");
            }
        } catch (Exception e) {
            sendInternalError(exchange, "{\"error\":\"Ошибка при обработке запроса: " + e.getMessage() + "\"}");
        }
    }
}