package HealthcareBot;

import HealthcareBot.Command.FindFriend.RequestForTeleHandle;
import HealthcareBot.Command.GetSummary;
import HealthcareBot.Command.LeaderboardDisplay.LeaderboardDisplayMessage;
import HealthcareBot.Command.PlayGame.GameIntroduction;
import HealthcareBot.Command.PlayGame.TaskCompletion;
import HealthcareBot.Command.PlayGame.TaskGeneration;
import HealthcareBot.Command.ScheduleNotification.StressInput;
import HealthcareBot.Database.UserDAO;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static HealthcareBot.Command.ScheduleNotification.StressInput.handleUserStressInput;

public class HealthcareBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final Map<Long, String> userStates = new HashMap<>();

    public static void sendMessage(long chatId, String message, String botToken) {
        TelegramClient telegramClient = new OkHttpTelegramClient(botToken);
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPhotoMessageToUser(long chat_id, String text, String photoURL) {
        SendPhoto msg = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile(photoURL))
                .caption(text)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public HealthcareBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            String teleHandle = update.getMessage().getFrom().getUserName();  // Get the user's Telegram handle
            long chat_id = update.getMessage().getChatId();

            UserDAO userDAO = new UserDAO();

            if (message_text.equals("/pic")) {
                sendPhotoMessageToUser(chat_id, "This is a little cat :)", "https://png.pngtree.com/background/20230519/original/pngtree-this-is-a-picture-of-a-tiger-cub-that-looks-straight-picture-image_2660243.jpg");
            } else if (message_text.equals("/start")) {
                userStates.put(chat_id, "");
                SendPhoto message = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://ctot.com/wp-content/uploads/2016/01/fotolia_76080180_subscription_monthly_xl.jpg?w=700"))
                        .caption("Hi!! Welcome to Healthcare Bot, please click on the icon at the right of the text box or open your keyboard to play a game!")
                        .build();

                // Add the keyboard to the message
                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Play Game", "Stress Logger"))
                        .keyboardRow(new KeyboardRow( "Individual Progress", "Check Summary"))
                        // Add second row of 3 buttons
                        .keyboardRow(new KeyboardRow("Show Friend Status", "Leaderboard"))
                        .build());

                //If no user, default 0
                if (!userDAO.userExists(teleHandle)) {
                    userDAO.addUser(teleHandle, 0);
                }

                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Play Game")) {
                // Send a picture to the user
                SendPhoto message = GameIntroduction.startGame(chat_id);

                try {
                    telegramClient.execute(message); // Call method to send the photo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Stress Logger")) {
                // Set user state to expect stress frequency input
                userStates.put(chat_id, "awaiting_stress_input");

                SendMessage msg = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("How many times do you experience stress a week? \nPlease input numbers only.")
                        .build();

                try {
                    telegramClient.execute(msg); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if ("awaiting_stress_input".equals(userStates.get(chat_id))) {
                // Handle user input for stress frequency
                String input = message_text;

                // Validate the input to ensure it's a number
                if (input.matches("\\d+")) { // Regex to check if the input is numeric
                    int stressCount = Integer.parseInt(input);
                    long user_id = userDAO.getUserIdByTelehandle(teleHandle);
                    StressInput.handleUserStressInput(chat_id, user_id, stressCount); // Call your method to handle the input
                    userStates.put(chat_id, "");  // Reset state after processing

                    SendMessage msg = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("Thank you! Your stress count has been recorded as: " + stressCount)
                            .build();
                    try {
                        telegramClient.execute(msg); // Send confirmation message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Send a message to indicate invalid input
                    SendMessage msg = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("Invalid input. Please enter a valid number.")
                            .build();
                    try {
                        telegramClient.execute(msg); // Send invalid input message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (message_text.equals("Spin the wheel")) {
                // Send a picture to the user
                SendPhoto message = TaskGeneration.taskGeneration(chat_id);
                SendPhoto taskFoundMessage = TaskGeneration.taskFound(chat_id, teleHandle);

                try {
                    telegramClient.execute(message); // Call method to send the photo
                    telegramClient.execute(taskFoundMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Leaderboard")) {

                SendPhoto message = LeaderboardDisplayMessage.leaderboardDisplayMessage(chat_id, teleHandle);

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (message_text.equals("Show Friend Status")) {
                // Ask the user to input the friend's telehandle
                userStates.put(chat_id, "awaiting_telehandle");

                SendPhoto message = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://th.bing.com/th/id/OIP.B9NfjTZy37_N0e09O9OEjQAAAA?rs=1&pid=ImgDetMain"))
                        .caption("What is your friend's telehandle? \n(do not include @)")
                        .build();
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if ("awaiting_telehandle".equals(userStates.get(chat_id))) {
                // Handle friend's telehandle input
                String friendTelehandle = message_text;
                userStates.put(chat_id, "");  // Reset state after processing

                SendPhoto message = RequestForTeleHandle.getUser(friendTelehandle, chat_id);

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Leave") || message_text.equals("Back to Menu")) {

                TaskGeneration.taskOngoing = false;
                long user_id = userDAO.getUserIdByTelehandle(teleHandle);

                if (TaskGeneration.currentTask != null && !TaskGeneration.currentTask.isEmpty()) {
                    userDAO.storeTask(user_id, TaskGeneration.currentTask, "", false);
                }

                TaskGeneration.currentTask = "";

                SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Do refer to the keyboard for different functionalities!")
                        .build();

                // Add the keyboard to the message
                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Play Game", "Stress Logger"))
                        .keyboardRow(new KeyboardRow("Individual Progress", "Check Summary"))
                        // Add second row of 3 buttons
                        .keyboardRow(new KeyboardRow("Show Friend Status", "Leaderboard"))
                        .build());
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("I am done with my Task!!")) {
                long user_id = userDAO.getUserIdByTelehandle(teleHandle);

                // Send task completion message
                SendPhoto message = TaskCompletion.endGame(chat_id, user_id);

                // Add points
                int points = 10 + userDAO.getUserPoints(teleHandle);
                userDAO.addUser(teleHandle, points);

                try {
                    telegramClient.execute(message); // Send task completion message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Favorite")) {
                long user_id = userDAO.getUserIdByTelehandle(teleHandle);
                String currentTask = TaskGeneration.currentTask;

                // Mark task as favorited in the database
                userDAO.favoriteTask(user_id, currentTask);

                SendPhoto message = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://th.bing.com/th/id/OIP.r9pSedjpIFWODyT-g_WwogHaFk?rs=1&pid=ImgDetMain"))
                        .caption("Task favorited!! :)")
                        .build();

                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Back to Menu"))
                        .keyboardRow(new KeyboardRow("Unfavorite"))
                        .build());

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (message_text.equals("Unfavorite")) {
                long user_id = userDAO.getUserIdByTelehandle(teleHandle);
                String currentTask = TaskGeneration.currentTask;

                // Mark task as unfavorited in the database
                userDAO.unfavoriteTask(user_id, currentTask);

                SendPhoto message = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://th.bing.com/th/id/OIP.UngM96PQuoOTlEp9iZGKOAHaGr?rs=1&pid=ImgDetMain"))
                        .caption("Task unfavorited! :'0")
                        .build();

                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        // Add first row of 3 buttons
                        .keyboardRow(new KeyboardRow("Back to Menu"))
                        .keyboardRow(new KeyboardRow("Favorite"))
                        .build());

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
        } else if (message_text.equals("Individual Progress")) {
                int points = userDAO.getUserPoints(teleHandle);
                String individualMessage = "You have " + points + " points :fire:";
                List<String> favoriteList = userDAO.getFavoriteTasks(userDAO.getUserIdByTelehandle(teleHandle));
                int size;
                if (favoriteList.isEmpty()) {
                    size = 0;
                } else {
                    size = favoriteList.size();
                    individualMessage = individualMessage + "\n\nHere are your favorite tasks :heart: : ";
                    for (int i = 0 ; i < size; i++) {
                        individualMessage = individualMessage + "\n - " + favoriteList.get(i);
                    }

                }
                SendPhoto message = SendPhoto
                        .builder()
                        .chatId(chat_id)
                        // This time will send the picture using a URL
                        .photo(new InputFile("https://static.vecteezy.com/system/resources/previews/005/214/351/original/keep-up-the-good-work-typography-t-shirt-design-vector.jpg"))
                        .caption(EmojiParser.parseToUnicode(individualMessage))
                        .build();

                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("Check Summary")) {
                SendMessage message = GetSummary.sendTaskSummaryByDay(chat_id);
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