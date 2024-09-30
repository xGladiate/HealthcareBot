package HealthcareBot.Command.ScheduleNotification;

import HealthcareBot.Database.StressIndicatorDAO;
import HealthcareBot.Main;

import static HealthcareBot.Main.notifier;

public class StressInput {
    public static void handleUserStressInput(long chat_id, long userId, int stressFrequency) {

        StressIndicatorDAO dao = new StressIndicatorDAO();
        dao.storeUserStressLevel(chat_id, userId, stressFrequency);

        if (stressFrequency != 0) {
            notifier.scheduleNotifications(chat_id, userId, stressFrequency);
        }
    }
}
