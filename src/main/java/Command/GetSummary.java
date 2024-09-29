package Command;

import Database.UserDAO;
import Model.TaskInfo;
import Model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

public class GetSummary {
    public static SendMessage sendTaskSummary(long chatId) {
        String taskSummary = getFormattedTaskSummaryByWeek();

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(taskSummary)
                .parseMode("Markdown") // Use Markdown for formatting if desired
                .build();
        return message;
    }
    public static String getFormattedTaskSummaryByWeek() {
        UserDAO userDAO = new UserDAO();
        Map<String, Map<String, Integer>> summary = userDAO.getTaskSummaryByWeek();
        StringBuilder formattedSummary = new StringBuilder();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map.Entry<String, Map<String, Integer>> weekEntry : summary.entrySet()) {
            // Parse the date string to LocalDateTime, then convert to LocalDate
            LocalDateTime weekStartDateTime = LocalDateTime.parse(weekEntry.getKey(), dateTimeFormatter);
            LocalDate weekStartDate = weekStartDateTime.toLocalDate();
            LocalDate weekEndDate = weekStartDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); // Get the next Sunday

            // Format the week display
            String weekDisplay = String.format("%s Week %d (%s - %s)",
                    weekStartDate.getMonth().name(),
                    weekStartDate.get(WeekFields.of(Locale.getDefault()).weekOfYear()),
                    weekStartDate.format(dateFormatter),
                    weekEndDate.format(dateFormatter));

            formattedSummary.append(weekDisplay).append("\n");

            for (Map.Entry<String, Integer> taskEntry : weekEntry.getValue().entrySet()) {
                formattedSummary.append("- ").append(taskEntry.getKey())
                        .append(": ").append(taskEntry.getValue()).append("\n");
            }

            formattedSummary.append("\n"); // Add extra line for better readability
        }

        return formattedSummary.toString();
    }
}
