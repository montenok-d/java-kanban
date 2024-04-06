import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerTest {

    Task task;
    FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    public void beforeEach() {
        fileBackedTaskManager = new FileBackedTaskManager();
        Task task1 = new Task("Таск 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40));
        ArrayList<Integer> epic1SUbtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), epic1SUbtasks);
        Epic epic2 = new Epic("Эпик 2", "Описание 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), epic1SUbtasks);
        Subtask subtask1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), 2);
        Subtask subtask2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), 3);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createEpic(epic2);
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
    }

    @Test
    void shouldRestoreTasks() {
        List<Task> allTasks = fileBackedTaskManager.getAllTasks();
        fileBackedTaskManager = fileBackedTaskManager.loadFromFile(fileBackedTaskManager.pathToFile);
        List<Task> loadedTasks = fileBackedTaskManager.getAllTasks();
        assertEquals(allTasks, loadedTasks);
    }

    @Test
    void shouldRestoreEpics() {
        List<Epic> allTasks = fileBackedTaskManager.getAllEpics();
        fileBackedTaskManager = fileBackedTaskManager.loadFromFile(fileBackedTaskManager.pathToFile);
        List<Epic> loadedTasks = fileBackedTaskManager.getAllEpics();
        assertEquals(allTasks, loadedTasks);
    }

    @Test
    void shouldRestoreSubtasks() {
        List<Subtask> allTasks = fileBackedTaskManager.getAllSubtasks();
        fileBackedTaskManager = fileBackedTaskManager.loadFromFile(fileBackedTaskManager.pathToFile);
        List<Subtask> loadedTasks = fileBackedTaskManager.getAllSubtasks();
        assertEquals(allTasks, loadedTasks);
    }

    @Test
    void shouldRestoreHistory() {
        List<Task> history = fileBackedTaskManager.getHistory();
        fileBackedTaskManager = fileBackedTaskManager.loadFromFile(fileBackedTaskManager.pathToFile);
        List<Task> loadedHistory = fileBackedTaskManager.getHistory();
        assertEquals(history, loadedHistory);
    }
}
