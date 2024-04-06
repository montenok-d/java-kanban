package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description, int taskId, TaskStatus status, int duration, LocalDateTime startTime, ArrayList<Integer> subtasksId) {
        super(name, description, taskId, status, duration, startTime);
        this.subtasksId = subtasksId;
    }

    public Epic(String name, String description, int taskId, TaskStatus status, int duration, LocalDateTime startTime) {
        super(name, description, taskId, status, duration, startTime);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getTaskId() + "," + TaskType.EPIC + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getDuration() + "," + getStartTime() + ",";
    }
}

