import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    Task task;
    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Test addNewTask", "Test addNewTask description", 0, TaskStatus.NEW);
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addAndGetHistory() {
        historyManager.add(task);
        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}