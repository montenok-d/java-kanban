import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

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

    private void checkTimeOverlap(Task task) {
        for (Task priorTask : prioritizedTasks) {
            if (!(priorTask.getTaskId() == (task.getTaskId()))) {
                if (priorTask.getEndTime() != null && task.getStartTime() != null) {
                    if (!(priorTask.getEndTime().isBefore(task.getStartTime()) || priorTask.getStartTime().isAfter(task.getEndTime()))) {
                        throw new TaskTimeException("Задача пересекается во времени с другой задачей");
                    }
                }
            }
        }
    }

    @Override
    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            add(tasks.get(taskId));
            return tasks.get(taskId);
        }
        return null;
    }

    @Override
    public Epic getEpic(int taskId) {
        if (epics.containsKey(taskId)) {
            add(epics.get(taskId));
            return epics.get(taskId);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int taskId) {
        if (subtasks.containsKey(taskId)) {
            add(subtasks.get(taskId));
            return subtasks.get(taskId);
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int taskId) {
        if (epics.get(taskId).getSubtasksId() != null) {
            List<Subtask> epicSubtasks = epics.get(taskId).getSubtasksId().stream()
                    .map(subtaskId -> subtasks.get(subtaskId))
                    .toList();
            return new ArrayList<>(epicSubtasks);
        }
        return null;
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
        if (epicSubtasks != null) {
            for (Integer subtaskId : epicSubtasks) {
                if (subtasks.get(subtaskId) == null) {
                    epicSubtasks.remove(subtaskId);
                }
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
            Epic epic = epics.get(epicId);
            if (epic.getSubtasksId() != null) {
                epic.getSubtasksId().add(taskId);
            }
            setEpicStatus(epicId);
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

    private void setEpicStatus(int epicId) {
        if (getEpicSubtasks(epicId) == null || getEpicSubtasks(epicId).isEmpty()) {
            epics.get(epicId).setStatus(TaskStatus.NEW);
        } else {
            ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epicId);
            int inProgressCount = 0;
            int doneCount = 0;
            for (Subtask subt : epicSubtasks) {
                if (subt.getStatus() == TaskStatus.IN_PROGRESS) {
                    inProgressCount += 1;
                } else if (subt.getStatus().equals(TaskStatus.DONE)) {
                    doneCount += 1;
                }
            }
            if (doneCount == epicSubtasks.size()) {
                epics.get(epicId).setStatus(TaskStatus.DONE);
            } else if (inProgressCount > 0 || (doneCount < subtasks.size() && doneCount != 0)) {
                epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    private void setEpicTimeAndDuration(Epic epic) {
        if (getEpicSubtasks(epic.getTaskId()) == null || getEpicSubtasks(epic.getTaskId()).isEmpty()) {
            return;
        } else {
            ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epic.getTaskId());
            int epicDuration = 0;
            LocalDateTime epicStartTime = null;
            LocalDateTime epicEndTime = null;
            for (Subtask subtask : epicSubtasks) {
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
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            remove(task.getTaskId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            prioritizedTasks.remove(epic);
            remove(epic.getTaskId());
        }
        epics.clear();
        if (!subtasks.isEmpty()) {
            removeAllSubtasks();
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (!epics.isEmpty()) {
                ArrayList<Integer> epicSubtasks = getEpic(subtask.getEpicId()).getSubtasksId();
                if (epicSubtasks.contains(subtask.getTaskId())) {
                    epicSubtasks.remove(Integer.valueOf(subtask.getTaskId()));
                }
            }
            remove(subtask.getTaskId());
            prioritizedTasks.remove(subtask);
            remove(subtask.getTaskId());
        }
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
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(taskId);
        for (Subtask subtask : epicSubtasks) {
            prioritizedTasks.remove(subtask);
            remove(subtask.getTaskId());
            subtasks.remove(subtask);
        }
        remove(taskId);
        prioritizedTasks.remove(getEpic(taskId));
        epics.remove(taskId);
    }

    @Override
    public void removeSubtask(int taskId) {
        Subtask subtask = subtasks.get(taskId);
        Epic epic = getEpic(subtask.getEpicId());
        epic.getSubtasksId().remove(Integer.valueOf(taskId));
        remove(taskId);
        prioritizedTasks.remove(getSubtask(taskId));
        subtasks.remove(taskId);
        setEpicStatus(epic.getTaskId());
        setEpicTimeAndDuration(epics.get(epic.getTaskId()));

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
