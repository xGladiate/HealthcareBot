package Model;

public class User {
    private String telehandle;
    private int points;

    public User(String telehandle, int points) {
        this.telehandle = telehandle;
        this.points = points;
    }

    public String getTelehandle() {
        return telehandle;
    }

    public int getPoints() {
        return points;
    }
}
