import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryTaskManagerTest {

    Task task;
    TaskManager manager;
    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Test NewTask", "Test NewTask description", 0, TaskStatus.NEW);
        manager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addNewTask() {
        manager.createTask(task);
        Task savedTask = manager.getTask(0);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        ArrayList<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void tasksEquals() {
        Task task1 = new Subtask("Test NewSubTask", "Test description", 0, TaskStatus.NEW, 1);
        Task task2 = new Subtask("Test NewSubTask", "Test description", 0, TaskStatus.NEW, 1);
        assertEquals(task1, task2);
        Task task3 = new Task("Test NewTask", "Test description", 0, TaskStatus.NEW);
        Task task4 = new Task("Test NewTask", "Test description", 0, TaskStatus.NEW);
        assertEquals(task3, task4);
    }

    @Test
    void addEpicAsSubtask() {
        ArrayList<Integer> epic1SUbtasks = new ArrayList<>();
        Epic epic1 = new Epic("Epic 1", "description", 2, TaskStatus.NEW, epic1SUbtasks);
        Subtask task1 = new Subtask("Subtask 1", "description", 1, TaskStatus.NEW, 2);
        epic1SUbtasks.add(1);
        epic1SUbtasks.add(2);
        manager.createEpic(epic1);
        assertEquals(1, manager.getEpicSubtasks(2).size());
    }

    @Test
    void addSubtaskAsItsEpic() {
        Subtask subtask = new Subtask("Test NewSubTask", "description", 0, TaskStatus.NEW, 0);
        manager.createSubtask(subtask);
        assertEquals(0, manager.getAllSubtasks().size());
    }
}
