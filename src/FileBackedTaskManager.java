import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    final String pathToFile = "src/dataHistoryTask.csv";
    private static final String HEADER = "id,type,name,status,description,epic";
    private final HashMap<Integer, Task> allTasks = new HashMap<>();

    public FileBackedTaskManager() {
        super();
    }

    private static Task fromString(String value) {
        String[] taskString = value.split(",");
        int id = Integer.parseInt(taskString[0]);
        TaskType type = TaskType.valueOf(taskString[1]);
        String name = taskString[2];
        TaskStatus status = TaskStatus.valueOf(taskString[3]);
        String description = taskString[4];
        switch (type) {
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                ArrayList<Integer> epicSubtasks = new ArrayList<>();
                return new Epic(name, description, id, status, epicSubtasks);
            case SUBTASK:
                int epicId = Integer.parseInt(taskString[5]);
                return new Subtask(name, description, id, status, epicId);
        }
        return null;
    }


    static String historyToString(HistoryManager manager) {
        List<String> historyIds = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            historyIds.add(String.valueOf(task.getTaskId()));
        }
        return String.join(",", historyIds);
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] line = value.split(",");
        for (String e : line) {
            historyIds.add(Integer.parseInt(e));
        }
        return historyIds;
    }

    public void save() {
        Path path = Paths.get(pathToFile);
        try (FileWriter fileWriter = new FileWriter(path.toFile(), StandardCharsets.UTF_8)) {
            fileWriter.write(HEADER + "\n");
            for (Task task : this.tasks.values()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : this.epics.values()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (Subtask subtask : this.subtasks.values()) {
                fileWriter.write(subtask.toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(this.inMemoryHistoryManager));
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Данные не  удалось сохранить");
        }
    }

    public static FileBackedTaskManager loadFromFile(String pathToFile) {
        Path path = Paths.get(pathToFile);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
            List<String> linesFromFile = new ArrayList<>();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                linesFromFile.add(line);
            }
            fileReader.close();
            for (int i = 1; i < linesFromFile.size(); i++) {
                if (!linesFromFile.get(i).isBlank()) {
                    Task task = fromString(linesFromFile.get(i));
                    assert task != null;
                    if (task.getType() == TaskType.SUBTASK) {
                        fileBackedTaskManager.subtasks.put(task.getTaskId(), (Subtask) task);
                        fileBackedTaskManager.allTasks.put(task.getTaskId(), task);
                    } else if (task.getType() == TaskType.EPIC) {
                        fileBackedTaskManager.epics.put(task.getTaskId(), (Epic) task);
                        fileBackedTaskManager.allTasks.put(task.getTaskId(), task);
                    } else if (task.getType() == TaskType.TASK) {
                        fileBackedTaskManager.tasks.put(task.getTaskId(), task);
                        fileBackedTaskManager.allTasks.put(task.getTaskId(), task);
                    }
                } else {
                    break;
                }
            }
            //восстанавливаем список id сабтаксков для эпиков
            for (Subtask subtask : fileBackedTaskManager.subtasks.values()) {
                ArrayList<Integer> epicSubtasks = fileBackedTaskManager.epics.get(subtask.getEpicId()).getSubtasksId();
                epicSubtasks.add(subtask.getTaskId());
                fileBackedTaskManager.epics.get(subtask.getEpicId()).setSubtasksId(epicSubtasks);
            }
            if (!linesFromFile.getLast().isBlank()) {
                List<Integer> historyIds = historyFromString(linesFromFile.getLast());
                for (Integer e : historyIds.reversed()) {
                    fileBackedTaskManager.add(fileBackedTaskManager.allTasks.get(e));
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Данные не  удалось загрузить");
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int taskId) {
        super.removeEpic(taskId);
        save();
    }

    @Override
    public void removeSubtask(int taskId) {
        super.removeSubtask(taskId);
        save();
    }

    public static void main(String[] args) {


        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        //fileBackedTaskManager = fileBackedTaskManager.loadFromFile(fileBackedTaskManager.pathToFile);
        Task task1 = new Task("Таск 1", "Описание 1", 1, TaskStatus.NEW);
        ArrayList<Integer> epic1SUbtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, epic1SUbtasks);
        Epic epic2 = new Epic("Эпик 2", "Описание 2", 1, TaskStatus.NEW, epic1SUbtasks);
        Subtask subtask1 = new Subtask("Саб 1", "Описание саб 1", 1, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.NEW, 3);
        Subtask subtask3 = new Subtask("Саб 3", "Описание саб 3", 1, TaskStatus.NEW, 3);
        Task task2 = new Task("Task2", "2", 1, TaskStatus.NEW);

        //создаем таски
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createEpic(epic2);
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);
        fileBackedTaskManager.createSubtask(subtask3);
        fileBackedTaskManager.createTask(task2);

        //вносим изменения в сабтаск, проверяем статус эпика
        System.out.println("Epic 2: " + fileBackedTaskManager.getEpic(3));
        System.out.println("History" + ((InMemoryTaskManager) fileBackedTaskManager).getHistory());
        Subtask subtask4 = new Subtask("Саб 1", "Описание саб 1", 4, TaskStatus.IN_PROGRESS, 3);
        fileBackedTaskManager.updateSubtask(subtask4);
        System.out.println("Epic 2: " + fileBackedTaskManager.getEpic(3));
        System.out.println("History" + ((InMemoryTaskManager) fileBackedTaskManager).getHistory());
        System.out.println(fileBackedTaskManager.getAllEpics());
        System.out.println(fileBackedTaskManager.getAllTasks());
        System.out.println(fileBackedTaskManager.getAllSubtasks());
        System.out.println(fileBackedTaskManager.getEpicSubtasks(3));
    }
}
