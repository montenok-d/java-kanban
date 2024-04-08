import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager, HistoryManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getTaskId));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private static int taskCount = 0;

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void checkTimeOverlap(Task task) {
        for (Task priorTask : prioritizedTasks) {
            if (priorTask.getEndTime() != null && task.getStartTime() != null) {
                if (!(priorTask.getEndTime().isBefore(task.getStartTime()) || priorTask.getStartTime().isAfter(task.getEndTime()))) {
                    throw new TaskTimeException("Задача пересекается во времени с другой задачей");
                }
            }
        }
    }

    @Override
    public Task getTask(int taskId) {
        add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int taskId) {
        add(epics.get(taskId));
        return epics.get(taskId);
    }

    @Override
    public Subtask getSubtask(int taskId) {
        add(subtasks.get(taskId));
        return subtasks.get(taskId);
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int taskId) {
        ArrayList<Integer> epicSubtasksId = epics.get(taskId).getSubtasksId();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : epicSubtasksId) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    @Override
    public void createTask(Task task) {
        int taskId = taskCount;
        taskCount += 1;
        task.setTaskId(taskId);
        checkTimeOverlap(task);
        tasks.put(taskId, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        ArrayList<Integer> epicSubtasks = epic.getSubtasksId();
        for (Integer subtaskId : epicSubtasks) {
            if (subtasks.get(subtaskId) == null) {
                epicSubtasks.remove(subtaskId);
            }
        }
        taskCount += 1;
        int taskId = taskCount;
        epic.setTaskId(taskId);
        epic.setStatus(TaskStatus.NEW);
        checkTimeOverlap(epic);
        epics.put(taskId, epic);
        prioritizedTasks.add(epic);
    }


    @Override
    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.get(epicId) != null) {
            taskCount += 1;
            int taskId = taskCount;
            subtask.setTaskId(taskId);
            checkTimeOverlap(subtask);
            subtasks.put(taskId, subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksId().add(taskId);
            setEpicTimeAndDuration(epic);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateTask(Task task) {
        checkTimeOverlap(task);
        tasks.put(task.getTaskId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        checkTimeOverlap(epic);
        epics.put(epic.getTaskId(), epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        checkTimeOverlap(subtask);
        subtasks.put(subtask.getTaskId(), subtask);
        int epicId = subtask.getEpicId();
        setEpicStatus(epicId);
        setEpicTimeAndDuration(epics.get(epicId));
        prioritizedTasks.add(subtask);
    }

    public void setEpicStatus(int epicId) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epicId);
        if (epicSubtasks.isEmpty()) {
            epics.get(epicId).setStatus(TaskStatus.NEW);
        } else {
            int inProgressCount = 0;
            int doneCount = 0;
            for (Subtask subt : epicSubtasks) {
                if (subt.getStatus() == TaskStatus.IN_PROGRESS) {
                    inProgressCount += 1;
                } else if (subt.getStatus().equals(TaskStatus.DONE)) {
                    doneCount += 1;
                }
            }
            if (inProgressCount > 0) {
                epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
            } else if (doneCount == subtasks.size()) {
                epics.get(epicId).setStatus(TaskStatus.DONE);
            }
        }
    }

    public void setEpicTimeAndDuration(Epic epic) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epic.getTaskId());
        int epicDuration = 0;
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        for (Subtask subtask: epicSubtasks) {
            epicDuration += subtask.getDuration();
            if (epicStartTime == null || epicStartTime.isAfter(subtask.getStartTime())) {
                epicStartTime = subtask.getStartTime();
            }
            if (epicEndTime == null || epicEndTime.isBefore(subtask.getStartTime())) {
                epicEndTime = subtask.getStartTime();
            }
        }
        epic.setDuration(epicDuration);
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public void removeTask(int taskId) {
        remove(taskId);
        prioritizedTasks.remove(getTask(taskId));
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(int taskId) {
        remove(taskId);
        prioritizedTasks.remove(getTask(taskId));
        epics.remove(taskId);
    }

    @Override
    public void removeSubtask(int taskId) {
        remove(taskId);
        prioritizedTasks.remove(getTask(taskId));
        subtasks.remove(taskId);

    }

    @Override
    public void add(Task task) {
        inMemoryHistoryManager.add(task);
    }

    @Override
    public void remove(int id) {
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}
