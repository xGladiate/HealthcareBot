package HealthcareBot.Database;

import HealthcareBot.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static HealthcareBot.Database.DatabaseConnection.connect;

public class UserDAO {

    public boolean userExists(String telehandle) {
        String query = "SELECT 1 FROM users WHERE telehandle = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, telehandle);
            ResultSet rs = stmt.executeQuery();

            // If the query returns a row, the user exists
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // User does not exist
    }

    public int findUserRank(String telehandle) {
        String query = "SELECT rank FROM ( " +
                "  SELECT telehandle, RANK() OVER (ORDER BY points DESC) AS rank " +
                "  FROM users " +
                ") ranked_users WHERE telehandle = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, telehandle);
            ResultSet rs = stmt.executeQuery();

            // Check if the result set contains the rank
            if (rs.next()) {
                return rs.getInt("rank");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if user is not found
    }

    public User findUserBefore(String telehandle) {
        String query = "WITH ranked_users AS (" +
                "  SELECT telehandle, points, RANK() OVER (ORDER BY points DESC) AS rank " +
                "  FROM users " +
                ") " +
                "SELECT telehandle, points FROM ranked_users " +
                "WHERE rank = (SELECT rank FROM ranked_users WHERE telehandle = ?) - 1";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, telehandle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String beforeTelehandle = rs.getString("telehandle");
                int points = rs.getInt("points");
                return new User(beforeTelehandle, points);  // Assuming you have a User class
            } else {
                System.out.println("No user found before the given user: " + telehandle);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in findUserBefore: " + e.getMessage());
            e.printStackTrace();
        }
        return null;  // Return null if no user is found
    }

    public static void addUser(String telehandle, int points) {
        String SQL = "INSERT INTO users(telehandle, points) VALUES(?, ?) " +
                "ON CONFLICT (telehandle) DO UPDATE SET points = EXCLUDED.points";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, telehandle);
            pstmt.setInt(2, points);
            pstmt.executeUpdate();
            System.out.println("User added or updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public int getUserPoints(String teleHandle) {
        String query = "SELECT points FROM users WHERE telehandle = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, teleHandle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("points");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default points if user not found
    }

    public long getUserIdByTelehandle(String telehandle) {
        String query = "SELECT id FROM users WHERE telehandle = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, telehandle);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user is not found
    }


    public static void storeTask(long userId, String taskName, String taskType, boolean completed) {
        String query = "INSERT INTO user_tasks (user_id, task_name, task_type, completed, completion_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the parameters for the insertion
            pstmt.setLong(1, userId);
            pstmt.setString(2, taskName);
            pstmt.setString(3, taskType);
            pstmt.setBoolean(4, completed);
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));


            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, Integer>> getTaskSummaryByMonth() {
        // Modify the query to group by month instead of by week
        String query = "SELECT task_name, completed, DATE_TRUNC('month', completion_date) AS month_start, COUNT(*) AS task_count " +
                "FROM user_tasks " +
                "GROUP BY task_name, completed, month_start " +
                "ORDER BY month_start DESC, task_name";

        Map<String, Map<String, Integer>> summary = new HashMap<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String taskName = rs.getString("task_name");
                boolean completed = rs.getBoolean("completed");
                String monthStart = rs.getString("month_start");  // Now month-based
                int taskCount = rs.getInt("task_count");

                String status = completed ? "Completed" : "Incomplete";

                // Aggregate tasks by month
                summary.computeIfAbsent(monthStart, k -> new HashMap<>())
                        .put(taskName + " (" + status + ")", taskCount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    public Map<String, Map<String, Map<String, Integer>>> getTaskSummaryByDay(long userId) {
        String query = "SELECT task_name, completed, DATE_TRUNC('month', completion_date) AS month_start, " +
                "DATE_TRUNC('day', completion_date) AS day_start, COUNT(*) AS task_count " +
                "FROM user_tasks " +
                "WHERE user_id = ? " +  // Added a WHERE clause to filter by userId
                "GROUP BY task_name, completed, month_start, day_start " +
                "ORDER BY month_start DESC, day_start, task_name";

        Map<String, Map<String, Map<String, Integer>>> summary = new HashMap<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the userId parameter in the query
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String taskName = rs.getString("task_name");
                    boolean completed = rs.getBoolean("completed");
                    String monthStart = rs.getString("month_start");
                    String dayStart = rs.getString("day_start");
                    int taskCount = rs.getInt("task_count");

                    String status = completed ? "Completed" : "Incomplete";

                    // Group tasks by month, then by day
                    summary.computeIfAbsent(monthStart, k -> new HashMap<>())
                            .computeIfAbsent(dayStart, k -> new HashMap<>())
                            .put(taskName + " (" + status + ")", taskCount);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }

    public void favoriteTask(long userId, String taskName) {
        String query = "INSERT INTO user_favorites (user_id, task_name) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, taskName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unfavoriteTask(long userId, String taskName) {
        String query = "DELETE FROM user_favorites WHERE user_id = ? AND task_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, taskName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isTaskFavorited(long userId, String taskName) {
        String query = "SELECT COUNT(*) FROM user_favorites WHERE user_id = ? AND task_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, taskName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getFavoriteTasks(long userId) {
        String query = "SELECT task_name FROM user_favorites WHERE user_id = ?";
        List<String> favoriteTasks = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                favoriteTasks.add(rs.getString("task_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favoriteTasks;
    }
}
