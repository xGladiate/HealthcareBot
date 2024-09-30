package HealthcareBot;

import HealthcareBot.Command.ScheduleNotification.StressNotifier;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static StressNotifier notifier;
    public static void main(String[] args) {
        String botToken = "7803548047:AAG7cIPEdmLIOSEES5570yqG0qJxAmzZCto";
        notifier = StressNotifier.getInstance(botToken);

        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new HealthcareBot(botToken));
            System.out.println("MyAmazingBot successfully started!");
            // Ensure this process wait forever
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
