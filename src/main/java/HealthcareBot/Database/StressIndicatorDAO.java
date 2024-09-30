package HealthcareBot.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static HealthcareBot.Database.DatabaseConnection.connect;

public class StressIndicatorDAO {

    public void storeUserStressLevel(long chat_id, long userId, int stressFrequency) {
        String query = "UPDATE users SET stress_frequency = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, stressFrequency);
            pstmt.setLong(2, userId);
            pstmt.executeUpdate();
            System.out.println("Stress frequency updated for user " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserStressLevel(long userId) {
        String query = "SELECT stress_frequency FROM users WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stress_frequency");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Default to 0 if user not found or no stress level set
    }
}

