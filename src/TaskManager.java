import Model.Epic;
import Model.Subtask;
import Model.Task;

import java.util.ArrayList;

public interface TaskManager {
    Task getTask(int taskId);

    Epic getEpic(int taskId);

    Subtask getSubtask(int taskId);

    ArrayList<Subtask> getEpicSubtasks(int taskId);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeTask(int taskId);

    void removeEpic(int taskId);

    void removeSubtask(int taskId);

}
