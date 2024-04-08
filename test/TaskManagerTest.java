import Model.Task;
import Model.Epic;
import Model.Subtask;
import Model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task task;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;
    ArrayList<Integer> epic1SUbtasks;
    ArrayList<Integer> epic2SUbtasks;

    @BeforeEach
    void beforeEach(){
        task = new Task("Test NewTask", "Test NewTask description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2024, 2, 5, 17, 40));
        epic1SUbtasks = new ArrayList<>();
        epic2SUbtasks = new ArrayList<>();
        epic1 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 5, 5, 17, 40), epic1SUbtasks);
        epic2 = new Epic("Эпик 2", "Описание 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 6, 5, 17, 40), epic2SUbtasks);
        subtask1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 7, 5, 17, 40), 2);
        subtask2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 8, 5, 17, 40), 3);
        manager.createTask(task);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
    }

    /*@AfterEach
    void clear() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
    }*/

    @Test
    void addNewTask() {
        int tasksSize = manager.getAllTasks().size();
        Task task1 = new Task("Test NewTask", "Test NewTask description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2025, 2, 5, 17, 40));
        manager.createTask(task1);
        Task savedTask = manager.getTask(task1.getTaskId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        ArrayList<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(tasksSize + 1, tasks.size(), "Неверное количество задач.");
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

    @Test
    void addSubtasksNewAndSetEpicStatusAsNew() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic);
        int epicId = epic.getTaskId();
        Subtask subtaskNew1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 7, 5, 17, 40), epicId);
        Subtask subtaskNew2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 8, 5, 17, 40), epicId);
        manager.createSubtask(subtaskNew1);
        manager.createSubtask(subtaskNew2);
        TaskStatus epicStatus = epic.getStatus();
        assertEquals(TaskStatus.NEW, epicStatus, "Статус должен быть 'New'.");
    }

    @Test
    void addSubtasksDoneAndSetEpicStatusAsDone() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic2 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic2);
        int epicId = epic2.getTaskId();
        Subtask subtaskNew1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.DONE, 30, LocalDateTime.of(2026, 7, 5, 17, 40), epicId);
        Subtask subtaskNew2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.DONE, 30, LocalDateTime.of(2026, 8, 5, 17, 40), epicId);
        manager.createSubtask(subtaskNew1);
        manager.createSubtask(subtaskNew2);
        TaskStatus epicStatus = epic2.getStatus();
        assertEquals(TaskStatus.DONE, epicStatus, "Статус должен быть 'DONE'.");
    }

    @Test
    void addSubtasksNewAndInprogressAndSetEpicStatusAsInprogress() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic3 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic3);
        int epicId = epic3.getTaskId();
        Subtask subtaskNew1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 7, 5, 17, 40), epicId);
        Subtask subtaskNew2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.DONE, 30, LocalDateTime.of(2026, 8, 5, 17, 40), epicId);
        manager.createSubtask(subtaskNew1);
        manager.createSubtask(subtaskNew2);
        TaskStatus epicStatus = epic3.getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Статус должен быть 'IN_PROGRESS'.");
    }

    @Test
    void addSubtasksInProgressAndSetEpicStatusAsInprogress() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic4 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic4);
        int epicId = epic4.getTaskId();
        Subtask subtaskNew1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2026, 7, 5, 17, 40), epicId);
        Subtask subtaskNew2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2026, 8, 5, 17, 40), epicId);
        manager.createSubtask(subtaskNew1);
        manager.createSubtask(subtaskNew2);
        TaskStatus epicStatus = epic4.getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Статус должен быть 'IN_PROGRESS'.");
    }

}
