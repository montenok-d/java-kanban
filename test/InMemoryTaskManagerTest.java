import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.Subtask;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryTaskManagerTest {

    Task task;
    TaskManager manager;
    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Test NewTask", "Test NewTask description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40));
        manager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addNewTask() {
        manager.createTask(task);
        Task savedTask = manager.getTask(task.getTaskId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        ArrayList<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void tasksEquals() {
        Task task1 = new Subtask("Test NewSubTask", "Test description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), 1);
        Task task2 = new Subtask("Test NewSubTask", "Test description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), 1);
        assertEquals(task1, task2);
        Task task3 = new Task("Test NewTask", "Test description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40));
        Task task4 = new Task("Test NewTask", "Test description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40));
        assertEquals(task3, task4);
    }

    @Test
    void addSubtaskAsItsEpic() {
        Subtask subtask = new Subtask("Test NewSubTask", "description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 4, 5, 17, 40), 0);
        manager.createSubtask(subtask);
        assertEquals(0, manager.getAllSubtasks().size());
    }
}
