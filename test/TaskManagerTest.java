import model.Task;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    void beforeEach() {
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

    @Test
    void getPrioritizedTasksNotNullAndSorted() {
        Task oldTask = new Task("Test OldTask", "description", 0, TaskStatus.NEW, 30, LocalDateTime.of(1988, 2, 5, 17, 40));
        Task newTask = new Task("Test NewTask", "description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2224, 2, 5, 17, 40));
        manager.createTask(oldTask);
        manager.createTask(newTask);
        ArrayList<Task> allTasks = manager.getPrioritizedTasks();
        assertTrue(allTasks.getFirst().getStartTime().isEqual(LocalDateTime.of(1988, 2, 5, 17, 40)));
        assertTrue(allTasks.getLast().getStartTime().isEqual(LocalDateTime.of(2224, 2, 5, 17, 40)));
    }

    @Test
    void getTaskNotNull() {
        assertNotNull(manager.getTask(task.getTaskId()));
    }

    @Test
    void getEpicNotNull() {
        assertNotNull(manager.getEpic(epic1.getTaskId()));
    }

    @Test
    void getSubtaskNotNull() {
        Epic epicAddSubtask = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 5, 5, 17, 40));
        manager.createEpic(epicAddSubtask);
        Subtask subtaskGet = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 7, 5, 17, 40), epicAddSubtask.getTaskId());
        manager.createSubtask(subtaskGet);
        assertNotNull(manager.getSubtask(subtaskGet.getTaskId()));
    }

    @Test
    void getEpicSubtasksNotNull() {
        ArrayList<Integer> subtasks = new ArrayList<>();
        Epic getEpicAddSubtask = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 5, 5, 17, 40), subtasks);
        manager.createEpic(getEpicAddSubtask);
        Subtask subtaskGet = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2026, 7, 5, 17, 40), getEpicAddSubtask.getTaskId());
        manager.createSubtask(subtaskGet);
        assertNotNull(getEpicAddSubtask.getSubtasksId());
        assertEquals(1, getEpicAddSubtask.getSubtasksId().size());
    }

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
    void addNewEpic() {
        int epicsSize = manager.getAllEpics().size();
        Epic epicAddNewEpic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 5, 5, 17, 40), epic1SUbtasks);
        manager.createEpic(epicAddNewEpic);
        Task savedTask = manager.getEpic(epicAddNewEpic.getTaskId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epicAddNewEpic, savedTask, "Задачи не совпадают.");

        ArrayList<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(epicsSize + 1, epics.size(), "Неверное количество задач.");
    }

    @Test
    void addNewSubtask() {
        int subtaskSize = manager.getAllSubtasks().size();
        Subtask subtaskAddNew = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 7, 5, 17, 40), epic1.getTaskId());
        manager.createSubtask(subtaskAddNew);
        Subtask savedTask = manager.getSubtask(subtaskAddNew.getTaskId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtaskAddNew, savedTask, "Задачи не совпадают.");

        ArrayList<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(subtaskSize + 1, subtasks.size(), "Неверное количество задач.");
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

    @Test
    void updateTaskTest() {
        Task task = new Task("Test Task", "description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2036, 2, 5, 17, 40));
        manager.createTask(task);
        Task savedTask = manager.getTask(task.getTaskId());
        Task newTask = new Task("Test NewTask", "description", savedTask.getTaskId(), TaskStatus.NEW, 30, LocalDateTime.of(2036, 2, 5, 17, 40));
        manager.updateTask(newTask);
        assertEquals("description", newTask.getDescription());
    }

    @Test
    void updateEpicTest() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic);
        Task savedTask = manager.getEpic(epic.getTaskId());
        Epic newEpic = new Epic("New Epic", "Описание 1", savedTask.getTaskId(), TaskStatus.NEW, 30, LocalDateTime.of(2024, 5, 5, 17, 40), epicSubtasks);
        manager.updateEpic(newEpic);
        assertEquals("New Epic", newEpic.getName());
    }

    @Test
    void updateSubtaskTest() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 7, 5, 17, 40), epic.getTaskId());
        manager.createSubtask(subtask);
        Subtask savedTask = manager.getSubtask(subtask.getTaskId());
        Subtask newSubtask = new Subtask("Сабтаск NEW", "Описание саб 1", savedTask.getTaskId(), TaskStatus.NEW, 30, LocalDateTime.of(2029, 7, 5, 17, 40), epic.getTaskId());
        manager.updateSubtask(newSubtask);
        assertEquals("Сабтаск NEW", newSubtask.getName());
    }

    @Test
    void setEpicTimeAndDurationTest() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic);
        Subtask subtaskOne = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 7, 5, 15, 40), epic.getTaskId());
        Subtask subtaskTwo = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 7, 5, 17, 40), epic.getTaskId());
        manager.createSubtask(subtaskOne);
        manager.createSubtask(subtaskTwo);
        int epicDuration = epic.getDuration();
        LocalDateTime epicStartTime = epic.getStartTime();
        LocalDateTime epicEndTime = epic.getEndTime();
        assertEquals(60, epicDuration);
        assertTrue(epicStartTime.isEqual(LocalDateTime.of(2028, 7, 5, 15, 40)));
        assertTrue(epicEndTime.isEqual(LocalDateTime.of(2028, 7, 5, 17, 40)));
    }

    @Test
    void getAllTasksTest() {
        ArrayList<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
    }

    @Test
    void getAllEpicsTest() {
        ArrayList<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
    }

    @Test
    void getAllSubtasksTest() {
        ArrayList<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются.");
    }

    @Test
    void removeAllTasksTest() {
        manager.removeAllTasks();
        ArrayList<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void removeAllEpicsTest() {
        manager.removeAllEpics();
        ArrayList<Epic> epics = manager.getAllEpics();
        assertTrue(epics.isEmpty());
    }

    @Test
    void removeAllSubtasksTest() {
        manager.removeAllSubtasks();
        ArrayList<Subtask> subtasks = manager.getAllSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void removeTaskByIdTest() {
        Task task = new Task("Test Task", "description", 0, TaskStatus.NEW, 30, LocalDateTime.of(2036, 2, 5, 17, 40));
        manager.createTask(task);
        Task savedTask = manager.getTask(task.getTaskId());
        manager.removeTask(savedTask.getTaskId());
        assertNull(manager.getTask(savedTask.getTaskId()));
    }

    @Test
    void removeEpicByIdTest() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2024, 6, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic);
        int savedTaskId = manager.getEpic(epic.getTaskId()).getTaskId();
        manager.removeEpic(savedTaskId);
        assertNull(manager.getEpic(savedTaskId));
    }

    @Test
    void removeSubtaskByIdTest() {
        ArrayList<Integer> epicSubtasks = new ArrayList<>();
        Epic epic = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 5, 5, 17, 40), epicSubtasks);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Сабтаск", "Описание саб 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2028, 7, 5, 17, 40), epic.getTaskId());
        manager.createSubtask(subtask);
        int savedTaskId = manager.getSubtask(subtask.getTaskId()).getTaskId();
        manager.removeSubtask(savedTaskId);
        assertNull(manager.getSubtask(savedTaskId));
        assertTrue(epic.getSubtasksId().isEmpty());
    }
}
