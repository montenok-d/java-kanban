import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Покормить кота", "Дать коту 20 грамм корма", 1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40));
        ArrayList<Integer> epic1SUbtasks = new ArrayList<>();
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40), epic1SUbtasks);
        Epic epic2 = new Epic("Эпик 2", "Описание 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40), epic1SUbtasks);
        Subtask subtask1 = new Subtask("Саб 1", "Описание саб 1",1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40), 2);
        Subtask subtask2 = new Subtask("Саб 2", "Описание саб 2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40), 3);
        Subtask subtask3 = new Subtask("Саб 3", "Описание саб 3", 1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40), 3);
        Task task2 = new Task("Task2", "2", 1, TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 18, 17, 40));

        //создаем таски
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.createTask(task2);

        //выводим все таски в консоль
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getEpicSubtasks(2));

        //вносим изменения в сабтаск, проверяем статус эпика
        System.out.println("Epic 2: " + manager.getEpic(3));
        System.out.println("History" + ((InMemoryTaskManager)manager).getHistory());
        Subtask subtask4 = new Subtask("Саб 1", "Описание саб 1", 4, TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2023, 2, 18, 17, 40), 3);
        manager.updateSubtask(subtask4);
        System.out.println("Epic 2: " + manager.getEpic(3));

        //удаляем таски
        //manager.removeSubtask(5);
        //System.out.println(manager.getAllSubtasks());

        //проверяем историю просмотров
        manager.getTask(0);
        manager.getEpic(3);
        manager.getTask(6);
        System.out.println("History" + ((InMemoryTaskManager)manager).getHistory());

        System.out.println("Epic 2: " + manager.getTask(0));
        System.out.println("History" + ((InMemoryTaskManager)manager).getHistory());



    }
}
