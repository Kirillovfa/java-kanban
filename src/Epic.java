import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public ArrayList<Integer> getsubtasks() {
        return subtasks;
    }

    public void addsubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public void removesubtask(int subtaskId) {
        subtasks.remove((Integer) subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{"
                + "id = " + getId()
                + ", name = '" + getName()
                + ", description = '" + getDescription()
                + ", status = " + getStatus()
                + " subtasks = " + getsubtasks()
                + '}';
    }
}