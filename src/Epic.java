import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description, int taskId, TaskStatus status, ArrayList<Integer> subtasksId) {
        super(name, description, taskId, status);
        this.subtasksId = subtasksId;

    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {

        this.subtasksId = subtasksId;
    }
}
