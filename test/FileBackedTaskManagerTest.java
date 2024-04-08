import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.Epic;
import Model.Subtask;
import Model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager();
        super.beforeEach();
    }

    @Test
    void shouldRestoreTasks() {
        List<Task> allTasks = manager.getAllTasks();
        manager = manager.loadFromFile(manager.pathToFile);
        List<Task> loadedTasks = manager.getAllTasks();
        assertEquals(allTasks, loadedTasks);
    }

    @Test
    void shouldRestoreEpics() {
        List<Epic> allTasks = manager.getAllEpics();
        manager = manager.loadFromFile(manager.pathToFile);
        List<Epic> loadedTasks = manager.getAllEpics();
        assertEquals(allTasks, loadedTasks);
    }

    @Test
    void shouldRestoreSubtasks() {
        List<Subtask> allTasks = manager.getAllSubtasks();
        manager = manager.loadFromFile(manager.pathToFile);
        List<Subtask> loadedTasks = manager.getAllSubtasks();
        assertEquals(allTasks, loadedTasks);
    }

    @Test
    void shouldRestoreHistory() {
        List<Task> history = manager.getHistory();
        manager = manager.loadFromFile(manager.pathToFile);
        List<Task> loadedHistory = manager.getHistory();
        assertEquals(history, loadedHistory);
    }
}
