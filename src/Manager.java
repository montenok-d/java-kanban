import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int taskCount = 0;

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(int taskId) {
        return epics.get(taskId);
    }

    public Subtask getSubtask(int taskId) {
        return subtasks.get(taskId);
    }

    public ArrayList<Subtask> getEpicSubtasks(int taskId) {
        ArrayList<Integer> epicSubtasksId = epics.get(taskId).getSubtasksId();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : epicSubtasksId) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    public void createTask(Task task) {
        int taskId = taskCount;
        taskCount += 1;
        task.setTaskId(taskId);
        tasks.put(taskId, task);

    }

    public void createEpic(Epic epic) {
        taskCount += 1;
        int taskId = taskCount;
        epic.setTaskId(taskId);
        epic.setStatus(TaskStatus.NEW);
        epics.put(taskId, epic);
    }

    public void createSubtask(Subtask subtask) {
        taskCount += 1;
        int taskId = taskCount;
        subtask.setTaskId(taskId);
        subtasks.put(taskId, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(taskId);

    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
    }

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


    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    public void removeEpic(int taskId) {
        epics.remove(taskId);
    }

    public void removeSubtask(int taskId) {
        subtasks.remove(taskId);
    }


}
