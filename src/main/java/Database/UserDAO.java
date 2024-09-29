package Database;

import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import static Database.DatabaseConnection.connect;

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
                System.out.println("No user found before the given user.");
            }
        } catch (SQLException e) {
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
}
