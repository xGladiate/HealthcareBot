package Command.PlayGame;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class TaskCompletion {

    public static SendMessage endGame(long chat_id) {
        String endGameMessage = EmojiParser.parseToUnicode("Congratulations for completing the task!!\n" +
                "You have earned 10 points from this task :smile:\n" +
                "Keep up the good work!");

        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(endGameMessage)
                .build();

        // Add the keyboard to the message
        message.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                // Add first row of 3 buttons
                .keyboardRow(new KeyboardRow("/start"))
                .build());
        
        return message;
    }
}
