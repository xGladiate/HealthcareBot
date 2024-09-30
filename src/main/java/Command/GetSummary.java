package Command;

import Database.UserDAO;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GetSummary {
    public static SendMessage sendTaskSummaryByDay(long chatId) {
        String taskSummary = getFormattedTaskSummaryByDay();

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(taskSummary)
                .parseMode("Markdown") // Use Markdown for formatting if desired
                .build();
        return message;
    }

    public static SendMessage sendTaskSummaryByMonth(long chatId) {
        String taskSummary = EmojiParser.parseToUnicode(getFormattedTaskSummaryByMonth());

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(taskSummary)
//                .parseMode("Markdown") // Use Markdown for formatting if desired
                .build();
        return message;
    }
    public static String getFormattedTaskSummaryByMonth() {
        UserDAO userDAO = new UserDAO();
        Map<String, Map<String, Integer>> summary = userDAO.getTaskSummaryByMonth(); // Modify this method to return tasks grouped by month
        StringBuilder formattedSummary = new StringBuilder();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/yyyy");

        for (Map.Entry<String, Map<String, Integer>> monthEntry : summary.entrySet()) {
            // Parse the date string to LocalDateTime, then convert to LocalDate
            LocalDateTime monthStartDateTime = LocalDateTime.parse(monthEntry.getKey(), dateTimeFormatter);
            YearMonth month = YearMonth.from(monthStartDateTime);
            String monthName = toTitleCase(month.getMonth().name());

            // Format the month display
            String monthDisplay = String.format("%s %d",
                    month.getMonth().name(),
                    month.getYear());

            formattedSummary.append(monthDisplay).append("\n");

            for (Map.Entry<String, Integer> taskEntry : monthEntry.getValue().entrySet()) {
                formattedSummary.append("- ").append(taskEntry.getKey())
                        .append(": ").append(taskEntry.getValue()).append("\n");
            }

            formattedSummary.append("\n"); // Add extra line for better readability
        }

        return formattedSummary.toString();
    }

    public static String getFormattedTaskSummaryByDay() {
        UserDAO userDAO = new UserDAO();
        Map<String, Map<String, Map<String, Integer>>> summary = userDAO.getTaskSummaryByDay();
        StringBuilder formattedSummary = new StringBuilder();

        // DateTime formatters for displaying the date information
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map.Entry<String, Map<String, Map<String, Integer>>> monthEntry : summary.entrySet()) {
            // Parse the month start date to a LocalDateTime
            LocalDateTime monthStartDateTime = LocalDateTime.parse(monthEntry.getKey(), dateTimeFormatter);

            // Format the month display, e.g., September 2024
            String monthDisplay = String.format("%s %d",
                    toTitleCase(monthStartDateTime.getMonth().name()), monthStartDateTime.getYear());

            formattedSummary.append(monthDisplay).append("\n");

            // Loop through each day of the month
            for (Map.Entry<String, Map<String, Integer>> dayEntry : monthEntry.getValue().entrySet()) {
                // Parse and format the day start date as [Day/Month/Year]
                LocalDateTime dayStartDateTime = LocalDateTime.parse(dayEntry.getKey(), dateTimeFormatter);
                String dayDisplay = dayStartDateTime.format(dayFormatter);

                formattedSummary.append("  ").append(dayDisplay).append("\n");

                // Loop through tasks for that day
                for (Map.Entry<String, Integer> taskEntry : dayEntry.getValue().entrySet()) {
                    String taskNameWithStatus = taskEntry.getKey(); // This includes task name and status
                    int taskCount = taskEntry.getValue();

                    // Determine the symbol based on the status (Completed/Incomplete)
                    String symbol = EmojiParser.parseToUnicode(taskNameWithStatus.contains("Completed") ? ":star:" : ":fire:");

                    // Format task entry with the symbol, e.g., ⭐️ Task Name (Completed) : X times
                    formattedSummary.append("    ").append(symbol).append(" ")
                            .append(taskNameWithStatus)
                            .append(": ").append(taskCount).append(" times\n");
                }
            }

            formattedSummary.append("\n"); // Extra line for readability
        }

        return formattedSummary.toString();
    }


    public static String toTitleCase(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return capitalizeFirstLetter(word.toLowerCase());
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
