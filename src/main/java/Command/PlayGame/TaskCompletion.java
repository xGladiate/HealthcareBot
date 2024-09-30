package Command.PlayGame;

import Database.UserDAO;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class TaskCompletion {

    public static SendPhoto endGame(long chat_id, long user_id) {
        UserDAO userDAO = new UserDAO();
        String endGameMessage = EmojiParser.parseToUnicode("Congratulations for completing the task!!\n" +
                "You have earned 10 points from this task :smile:\n" +
                "Keep up the good work!");


        if (!TaskGeneration.currentTask.isEmpty()) {
            String taskName = TaskGeneration.currentTask;
            userDAO.storeTask(user_id, taskName, "", true);
        }

        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                // This time will send the picture using a URL
                .photo(new InputFile("https://st.depositphotos.com/2274151/2943/i/450/depositphotos_29431943-stock-photo-good-job-red-stamp.jpg"))
                .caption(endGameMessage)
                .build();

        // Add the keyboard to the message
        message.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                // Add first row of 3 buttons
                .keyboardRow(new KeyboardRow("Back to Menu"))
                .build());
        TaskGeneration.currentTask = "";
        return message;
    }
}
