import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager, HistoryManager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private static int taskCount = 0;

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
        tasks.put(taskId, task);

    }

    @Override
    public void createEpic(Epic epic) {
        taskCount += 1;
        int taskId = taskCount;
        epic.setTaskId(taskId);
        epic.setStatus(TaskStatus.NEW);
        epics.put(taskId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        taskCount += 1;
        int taskId = taskCount;
        subtask.setTaskId(taskId);
        subtasks.put(taskId, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(taskId);

    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        int epicId = subtask.getEpicId();
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


    @Override
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
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
        tasks.remove(taskId);
    }

    @Override
    public void removeEpic(int taskId) {
        epics.remove(taskId);
    }

    @Override
    public void removeSubtask(int taskId) {
        subtasks.remove(taskId);
    }


    @Override
    public void add(Task task) {
        inMemoryHistoryManager.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = inMemoryHistoryManager.getHistory();
        System.out.println(history);
        return inMemoryHistoryManager.getHistory();
    }
}