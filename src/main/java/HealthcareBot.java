import Command.PlayGame.GameIntroduction;
import Command.PlayGame.TaskCompletion;
import Command.PlayGame.TaskGeneration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.Game;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.List;

public class HealthcareBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public HealthcareBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if (message_text.equals("/pic")) {
                SendPhoto msg = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        .photo(new InputFile("https://png.pngtree.com/background/20230519/original/pngtree-this-is-a-picture-of-a-tiger-cub-that-looks-straight-picture-image_2660243.jpg"))
                        .caption("This is a little cat :)")
                        .build();
                try {
                    telegramClient.execute(msg); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/start")) {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("Hi!! Welcome to Healthcare Bot, please click on the icon at the right of the text box or open your keyboard to play a game!")
                        .build();

                // Add the keyboard to the message
                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Play Game"))
                        .keyboardRow(new KeyboardRow( "Individual Progress", "Check Summary"))
                        // Add second row of 3 buttons
                        .keyboardRow(new KeyboardRow("Show Friend Status", "Leaderboard"))
                        .build());

                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Play Game")) {
                // Send a picture to the user
                SendMessage message = GameIntroduction.startGame(chat_id);

                try {
                    telegramClient.execute(message); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Spin the wheel")) {
                // Send a picture to the user
                SendPhoto message = TaskGeneration.taskGeneration(chat_id);
                SendPhoto taskFoundMessage = TaskGeneration.taskFound(chat_id);

                try {
                    telegramClient.execute(message); // Call method to send the photo
                    telegramClient.execute(taskFoundMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("I am done with my Task!!")) {
                SendMessage message = TaskCompletion.endGame(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/hide")) {
                // Hide the keyboard
                SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Keyboard hidden")
                        .replyMarkup(new ReplyKeyboardRemove(true))
                        .build();
                try {
                    telegramClient.execute(message); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("Unknown command")
                        .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            // Message contains photo
            // Set variables
            long chat_id = update.getMessage().getChatId();

            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getFileId)
                    .orElse("");
            int f_width = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getWidth)
                    .orElse(0);
            int f_height = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getHeight)
                    .orElse(0);
            String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
            SendPhoto msg = SendPhoto
                    .builder()
                    .chatId(chat_id)
                    .photo(new InputFile(f_id))
                    .caption(caption)
                    .build();
            try {
                telegramClient.execute(msg); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}