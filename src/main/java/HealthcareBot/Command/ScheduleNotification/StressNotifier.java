package HealthcareBot.Command.ScheduleNotification;

import HealthcareBot.HealthcareBot;
import HealthcareBot.Model.WellnessQuotes;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StressNotifier {
    private static StressNotifier instance;

    private String botToken;
    private StressNotifier(String botToken) {
        this.botToken = botToken;
    }

    public static StressNotifier getInstance(String botToken) {
        if (instance == null) {
            instance = new StressNotifier(botToken);
        }
        return instance;
    }

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Schedules notifications for the number of times per week based on the stress frequency
    public void scheduleNotifications(long chat_id, long userId, int stressFrequency) {
        if (stressFrequency <= 0) {
            System.out.println("Invalid stress frequency. Must be greater than zero.");
            return; // Exit the method or handle it as needed
        }
        int delayBetweenNotifications = (7 * 24 * 60) / stressFrequency;  // minutes between notifications

        Runnable sendNotificationTask = () -> sendWellnessNotification(chat_id);

        // Schedule notifications with a fixed delay
        scheduler.scheduleAtFixedRate(sendNotificationTask, 0, delayBetweenNotifications, TimeUnit.MINUTES);
    }

    private void sendWellnessNotification(long chat_id) {
        String quote = WellnessQuotes.getRandomQuote();
        // Assuming you have a method to send a message to the user
        HealthcareBot.sendMessage(chat_id, quote, botToken);
    }
}

