package Command.PlayGame;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class GameIntroduction {

    public static SendPhoto startGame(long chat_id) {
        String introductionMessage = EmojiParser.parseToUnicode("This is a random task generator game!!! \n" +
                "Press ~Spin The Wheel~ to start! :smile:");

        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                // This time will send the picture using a URL
                .photo(new InputFile("https://th.bing.com/th/id/OIP.veJikwbOuypo1JSX0tLm3QHaHa?rs=1&pid=ImgDetMain"))
                .caption(introductionMessage)
                .build();

        // Add the keyboard to the message
        message.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                // Add first row of 3 buttons
                .keyboardRow(new KeyboardRow("Spin the wheel"))
                .build());

        return message;
    }
}
