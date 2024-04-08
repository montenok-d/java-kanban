package Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int taskId;
    private TaskStatus status;
    private int duration;
    private LocalDateTime startTime;

    public Task(String name, String description, int taskId, TaskStatus status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskId, status);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {
        return taskId + "," + TaskType.TASK + "," + name + "," + status + ","
                + description + "," + duration + "," + startTime + ",";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        Duration durationToMinutes = Duration.ofMinutes(this.duration);
        LocalDateTime endTime = this.startTime.plus(durationToMinutes);
        return endTime;
    }
}
