package HealthcareBot.Command.PlayGame;

import HealthcareBot.Database.UserDAO;
import HealthcareBot.Model.Task;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TaskGeneration {

    public static String currentTask;
    public static boolean taskOngoing;

    public static SendPhoto taskGeneration(long chat_id) {
        String taskGenerationMessage = "Now let me pick your task for today...";
        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                // This time will send the picture using a URL
                .photo(new InputFile("https://static.vecteezy.com/system/resources/previews/020/575/862/original/3d-icon-illustration-two-floating-dice-png.png"))
                .caption(taskGenerationMessage)
                .build();
        return message;
    }

    public static SendPhoto taskFound(long chat_id, String telehandle) {

        UserDAO userDAO = new UserDAO();
        // List of possible tasks
        List<Task> tasks = Arrays.asList(
                new Task("Find a trail containing at least 8 ants!", "https://www.mypmp.net/wp-content/uploads/2017/10/iS-497387771_ant.jpg"),
                new Task("Look at the sky for 5 minutes and spot 3 different shapes in the clouds!", "https://th.bing.com/th/id/OIP.IOl09xzibTSeuyIT88GhoQHaD4?w=1200&h=630&rs=1&pid=ImgDetMain"),
                new Task("Write down 3 things you're grateful for!", "https://www.publicdomainpictures.net/pictures/220000/velka/hand-writing-note.jpg"),
                new Task("Take a 2-minute break and drink a glass of water!", "https://dm0qx8t0i9gc9.cloudfront.net/watermarks/image/rDtN98Qoishumwih/glass-of-water_SB_PM.jpg"),
                new Task("Draw a quick sketch of a flower!", "https://blog.udemy.com/wp-content/uploads/2014/05/shutterstock_123770368.jpg"),
                new Task("Do 40 jumping jacks!", "https://th.bing.com/th/id/R.f2baff2e406642985cfdee597fe090da?rik=JxvuxLaIvwFHkQ&riu=http%3a%2f%2f2.bp.blogspot.com%2f-EprhXUZ9PMA%2fUD-K5MYRjFI%2fAAAAAAAAAZ8%2fS0AaA99_rwc%2fs1600%2fjumping-jacks.jpg&ehk=nhc9ueqv0EDlTcJ6lDxTuJQsqyoH8xkrIwtIcmKF%2bt0%3d&risl=&pid=ImgRaw&r=0"),
                new Task("Stand up and stretch for 1 minute!", "https://www.millerstructures.com/wp-content/uploads/2017/10/greatest_stretch_step_3_main_0-1024x886.jpg")
        );

        // Randomly select a task
        Random rand = new Random();
        Task randomTask = tasks.get(rand.nextInt(tasks.size()));

        currentTask = randomTask.getName();
        taskOngoing = true;

        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile(randomTask.getImageUrl()))
                .caption("Task Found!!\n" + randomTask.getName())
                .build();

        // Add the keyboard with task completion options
        message.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                .keyboardRow(new KeyboardRow("I am done with my Task!!"))
                .keyboardRow(new KeyboardRow("Leave"))
                .build());

        return message;
    }
}
