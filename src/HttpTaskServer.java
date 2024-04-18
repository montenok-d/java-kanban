import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private HttpServer server;
    private Gson gson;

    private TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", this::handler);
    }

    public void start() {
        server.start();
        System.out.println("Server started");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
    }

    private void handler(HttpExchange httpExchange) {
        try {
            String[] pathStrings = httpExchange.getRequestURI().getPath().split("/");
            String path = pathStrings[1];
            String requestMethod = httpExchange.getRequestMethod();
            switch (path) {
                case "tasks":
                    handleTasks(httpExchange);
                    break;
                case "epics": {
                    handleEpics(httpExchange);
                    break;
                }
                case "subtasks": {
                    handleSubtasks(httpExchange);
                    break;
                }
                case "history": {
                    handleHistory(httpExchange);
                    break;
                }
                case "prioritized": {
                    handlePrioritized(httpExchange);
                    break;
                }
            }
        } catch (Exception exception){
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("/tasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/tasks/", "");
                    int id = Integer.parseInt(pathId);
                    if (taskManager.getTask(id) != null) {
                        String response = gson.toJson(taskManager.getTask(id));
                        sendText(httpExchange, response);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                     break;
                } else if (Pattern.matches("/tasks$", path)) {
                    String response = gson.toJson(taskManager.getAllTasks());
                    sendText(httpExchange, response);
                    break;
                }
            case "POST":
                if (Pattern.matches("/tasks/\\d+$", path)) {
                    String json = readText(httpExchange);
                    Task task = gson.fromJson(json, Task.class);
                    taskManager.updateTask(task);
                    System.out.println("Задача с " + task.getTaskId() + " успешно обновлена.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else if (Pattern.matches("/tasks$", path)) {
                    String json = readText(httpExchange);
                    Task task = gson.fromJson(json, Task.class);
                    try {
                        taskManager.createTask(task);
                        System.out.println("Задача успешно добавлена.");
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    catch (TaskTimeException exception) {
                        httpExchange.sendResponseHeaders(406, 0);
                    }
                    break;
                }
                break;
            case "DELETE":
                if (Pattern.matches("/tasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/tasks/", "");
                    int id = Integer.parseInt(pathId);
                    taskManager.removeTask(id);
                    System.out.println("Задача с id " + id + " удалена.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else if (Pattern.matches("/tasks$", path)) {
                    taskManager.removeAllTasks();
                    System.out.println("Все задачи удалены.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                }
            default:
                System.out.println("Неизвестный метод запроса: " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
                break;
            }
        }

    private void handleEpics(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("/epics/\\d+$", path)) {
                    String pathId = path.replaceFirst("/epics/", "");
                    int id = Integer.parseInt(pathId);
                    if (taskManager.getEpic(id) != null) {
                        String response = gson.toJson(taskManager.getEpic(id));
                        sendText(httpExchange, response);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                } else if (Pattern.matches("/epics$", path)) {
                    String response = gson.toJson(taskManager.getAllEpics());
                    sendText(httpExchange, response);
                    break;
                } else if (Pattern.matches("/epics/\\d+/subtasks$", path)) {
                    String pathId = path.replaceFirst("/epics/", "").replaceFirst("/subtasks", "");
                    int id = Integer.parseInt(pathId);
                    String response = gson.toJson(taskManager.getEpicSubtasks(id));
                    sendText(httpExchange, response);
                }
            case "POST":
                if (Pattern.matches("/epics/\\d+$", path)) {
                    String json = readText(httpExchange);
                    Epic epic = gson.fromJson(json, Epic.class);
                    taskManager.updateEpic(epic);
                    System.out.println("Задача с id " + epic.getTaskId() + " успешно обновлена.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else if (Pattern.matches("/epics$", path)) {
                    String json = readText(httpExchange);
                    Epic epic = gson.fromJson(json, Epic.class);
                    try {
                        taskManager.createEpic(epic);
                        System.out.println("Задача успешно добавлена.");
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (TaskTimeException exception) {
                        httpExchange.sendResponseHeaders(406, 0);
                    }
                    break;
                }
                break;
            case "DELETE":
                if (Pattern.matches("/epics/\\d+$", path)) {
                    String pathId = path.replaceFirst("/epics/", "");
                    int id = Integer.parseInt(pathId);
                    taskManager.removeEpic(id);
                    System.out.println("Задача с id " + id + " удалена.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else if (Pattern.matches("/epics$", path)) {
                    taskManager.removeAllEpics();
                    System.out.println("Все эпики удалены.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                }
            default:
                System.out.println("Неизвестный метод запроса: " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleSubtasks(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("/subtasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/subtasks/", "");
                    int id = Integer.parseInt(pathId);
                    if (taskManager.getSubtask(id) != null) {
                        String response = gson.toJson(taskManager.getSubtask(id));
                        sendText(httpExchange, response);
                    }
                    else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                } else if (Pattern.matches("/subtasks$", path)) {
                    String response = gson.toJson(taskManager.getAllSubtasks());
                    sendText(httpExchange, response);
                    break;
                }
            case "POST":
                if (Pattern.matches("/subtasks/\\d+$", path)) {
                    String json = readText(httpExchange);
                    Subtask subtask = gson.fromJson(json, Subtask.class);
                    taskManager.updateSubtask(subtask);
                    System.out.println("Задача с " + subtask.getTaskId() + " успешно обновлена.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else if (Pattern.matches("/subtasks$", path)) {
                    String json = readText(httpExchange);
                    Subtask subtask = gson.fromJson(json, Subtask.class);
                    try {
                        taskManager.createSubtask(subtask);
                        System.out.println("Задача успешно добавлена.");
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (TaskTimeException exception) {
                        httpExchange.sendResponseHeaders(406, 0);
                    }
                    break;
                }
                break;
            case "DELETE":
                if (Pattern.matches("/subtasks/\\d+$", path)) {
                    String pathId = path.replaceFirst("/subtasks/", "");
                    int id = Integer.parseInt(pathId);
                    taskManager.removeSubtask(id);
                    System.out.println("Задача с id " + id + " удалена.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else if (Pattern.matches("/subtasks$", path)) {
                    taskManager.removeAllSubtasks();
                    System.out.println("Все подзадачи удалены.");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                }
            default:
                System.out.println("Неизвестный метод запроса: " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handlePrioritized(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if (!requestMethod.equals("GET")) {
            System.out.println("/prioritized ждет GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
        }
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(httpExchange, response);
    }

    private void handleHistory(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if (!requestMethod.equals("GET")) {
            System.out.println("/history ждет GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
        }
        String response = gson.toJson(taskManager.getHistory());
        sendText(httpExchange, response);
    }

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        Task task = new Task("Test NewTask", "Test NewTask description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 2, 5, 17, 40));
        taskManager.createTask(task);
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 5, 5, 17, 40), epicSubtasks);
        taskManager.createEpic(epic);
        int epicId = epic.getTaskId();
        Subtask subtask = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 7, 5, 17, 40), epicId);
        taskManager.createSubtask(subtask);


        //httpTaskServer.stop();
    }
}
