package Model;

public class TaskInfo {
    private String taskName;
    private int count;
    private boolean completed;

    public TaskInfo(String taskName, int count, boolean completed) {
        this.taskName = taskName;
        this.count = count;
        this.completed = completed;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getCount() {
        return count;
    }

    public boolean isCompleted() {
        return completed;
    }
}