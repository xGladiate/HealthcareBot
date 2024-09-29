package Command.PlayGame;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class TaskGeneration {

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

    public static SendPhoto taskFound(long chat_id) {
        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile("https://www.mypmp.net/wp-content/uploads/2017/10/iS-497387771_ant.jpg"))
                .caption("Task Found!!\n" +
                        "Find a trail containing at least 8 ants!")
                .build();
        message.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                .keyboardRow(new KeyboardRow("I am done with my Task!!"))
                .keyboardRow(new KeyboardRow("Leave"))
                .build());
        return message;
    }
}
