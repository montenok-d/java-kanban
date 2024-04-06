import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, int taskId, TaskStatus status, int duration, LocalDateTime startTime, int epicId) {
        super(name, description, taskId, status, duration, startTime);
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
        return getTaskId() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + epicId + "," + getDuration() + "," + getStartTime() + ",";
    }

}
