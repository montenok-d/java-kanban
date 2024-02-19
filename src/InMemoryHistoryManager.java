import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    static ArrayList<Task> history = new ArrayList<>();


    @Override
    public void add(Task task) {

        if (history.size() >= 10) {
            history.removeFirst();
        } else {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
