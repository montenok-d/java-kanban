import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    Task task;
    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Test addNewTask", "Test addNewTask description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40));
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addAndGetHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addTaskTwoTimesAndGetHistory() {
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void removeTaskAndGetHistory() {
        historyManager.add(task);
        historyManager.remove(task.getTaskId());
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История пустая.");
    }


}