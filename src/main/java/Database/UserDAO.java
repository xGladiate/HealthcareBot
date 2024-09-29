package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
