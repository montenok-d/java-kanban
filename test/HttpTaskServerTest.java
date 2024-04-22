import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private final Gson gson = httpTaskServer.getGson();

    private TaskManager taskManager;

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        task = new Task("Test NewTask", "Test NewTask description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 2, 5, 17, 40));
        taskManager.createTask(task);
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 5, 5, 17, 40), epicSubtasks);
        taskManager.createEpic(epic);
        int epicId = epic.getTaskId();
        subtask = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 7, 5, 17, 40), epicId);
        taskManager.createSubtask(subtask);

        httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(task, actual.get(0), "Задачи не совпадают");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int taskId = task.getTaskId();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(task, actual, "Задачи не совпадают");
    }

    @Test
    void getTaskByIdTaskGet404() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/404");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        int tasksSize = taskManager.getAllTasks().size();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        Task newTask = new Task("Test NewTask", "description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2025, 2, 5, 17, 40));
        String newTaskToJson = gson.toJson(newTask);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskToJson))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(tasksSize + 1, taskManager.getAllTasks().size());
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        int tasksSize = taskManager.getAllTasks().size();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + task.getTaskId());
        Task newTask = new Task("Test NewTask", "description", task.getTaskId(), TaskStatus.NEW, 30, LocalDateTime.of(2025, 3, 5, 17, 40));
        String newTaskToJson = gson.toJson(newTask);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskToJson))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(tasksSize, taskManager.getAllTasks().size());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int taskId = task.getTaskId();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getTask(taskId));
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(epic, actual.get(0), "Задачи не совпадают");
    }

    @Test
    void getEpicsById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int epicId = epic.getTaskId();
        URI uri = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(epic, actual, "Задачи не совпадают");
    }

    @Test
    void getEpicsByIdGet404() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/404");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void getEpicsSubtasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int epicId = epic.getTaskId();
        URI uri = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
    }

    @Test
    void createEpic() throws IOException, InterruptedException {
        int epicsSize = taskManager.getAllEpics().size();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        ArrayList<Integer> subtasks = new ArrayList<>();
        Epic newEpic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 11, 5, 17, 40), subtasks);
        String newTaskToJson = gson.toJson(newEpic);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskToJson))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(epicsSize + 1, taskManager.getAllEpics().size(), "Неверное количество задач.");
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        int epicsSize = taskManager.getAllEpics().size();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getTaskId());
        ArrayList<Integer> subtasks = new ArrayList<>();
        Epic newEpic = new Epic("Эпик 1", "Описание 1", epic.getTaskId(), TaskStatus.NEW, 30, LocalDateTime.of(2029, 6, 5, 17, 40), subtasks);
        String newTaskToJson = gson.toJson(newEpic);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskToJson))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(epicsSize, taskManager.getAllEpics().size(), "Неверное количество задач.");
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int epicId = epic.getTaskId();
        URI uri = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getEpic(epicId));
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(subtask, actual.get(0), "Задачи не совпадают");
    }

    @Test
    void getSubtasksById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int subtaskId = subtask.getTaskId();
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(subtask, actual, "Задачи не совпадают");
    }

    @Test
    void getSubtasksByIdGet404() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int subtaskId = subtask.getTaskId();
        URI uri = URI.create("http://localhost:8080/subtasks/404");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void createSubtask() throws IOException, InterruptedException {
        int subtasksSize = taskManager.getAllSubtasks().size();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        Subtask newSubtask = new Subtask("Сабтаск", "Описание саб 1", 121, TaskStatus.NEW, 30, LocalDateTime.of(2028, 7, 5, 17, 40), epic.getTaskId());
        String newTaskToJson = gson.toJson(newSubtask);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskToJson))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(subtasksSize + 1, taskManager.getAllSubtasks().size());
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        int subtasksSize = taskManager.getAllSubtasks().size();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask.getTaskId());
        Subtask newSubtask = new Subtask("Сабтаск", "Описание саб 1", subtask.getTaskId(), TaskStatus.NEW, 30, LocalDateTime.of(2028, 8, 5, 17, 40), epic.getTaskId());
        String newTaskToJson = gson.toJson(newSubtask);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskToJson))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(subtasksSize, taskManager.getAllSubtasks().size());
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        int subtaskId = subtask.getTaskId();
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getSubtask(subtaskId));
    }

    @Test
    void deleteAllSubtasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
    }

    @Test
    void getPrioritized() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
    }


}
