package HealthcareBot.Model;

public class Task {
    private String name;
    private String imageUrl;

    public Task(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
