package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        if (id == epicId) {
            System.out.println("Сабтаска не может ссылаться на саму себя как на эпик.");
            this.epicId = -1; // или 0 — зависит от твоей логики
        } else {
            this.epicId = epicId;
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (this.getId() == epicId) {
            System.out.println("Сабтаска не может ссылаться на саму себя.");
            this.epicId = -1;
        } else {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", epicId=" + epicId;
    }
}