public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, int taskId, TaskStatus status, int epicId) {
        super(name, description, taskId, status);
        this.epicId = epicId;
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return getTaskId() + "," + TaskType.SUBTASK + "," + name + "," + status + ","
                + description + "," + epicId + ",";
    }

}
