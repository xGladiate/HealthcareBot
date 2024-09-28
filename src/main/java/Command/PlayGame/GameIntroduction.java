package Command.PlayGame;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class GameIntroduction {

    public static SendMessage startGame(long chat_id) {
        String introductionMessage = EmojiParser.parseToUnicode("This is a random task generator game!!! \n" +
                "Press [Spin The Wheel] to start! :smile:");

        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(introductionMessage)
                .build();

        // Add the keyboard to the message
        message.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                // Add first row of 3 buttons
                .keyboardRow(new KeyboardRow("Spin the wheel"))
                .build());

//        List<InlineKeyboardRow> rowsInline = new ArrayList<>();
//        InlineKeyboardRow rowInline = new InlineKeyboardRow();
//
//        InlineKeyboardButton spinTheWheelButton = new InlineKeyboardButton("Spin the Wheel");
//        spinTheWheelButton.setText("Spin the Wheel");
//        spinTheWheelButton.setCallbackData("spin_the_wheel"); // You can handle this callback data in your callback query handler
//        rowInline.add(spinTheWheelButton);
//
//        rowsInline.add(rowInline);
//
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rowsInline);
//
//        SendMessage message = SendMessage.builder()
//                .chatId(chat_id)
//                .text(introductionMessage)
//                .replyMarkup(inlineKeyboardMarkup)
//                .build();
//
//        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }
}
