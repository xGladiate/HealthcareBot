package Command.LeaderboardDisplay;

import Database.UserDAO;
import Model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class LeaderboardDisplayMessage {
    public static SendMessage leaderboardDisplayMessage(long chat_id, String teleHandle) {
        UserDAO userDAO = new UserDAO();

        int rank = userDAO.findUserRank(teleHandle);

        String congratsMessage = "You are currently in the " + rank
                + getPrefixFromRank(rank) + " position!!\n";

        String additionalMessage;
        if (rank != 1) {
            User previousUser = userDAO.findUserBefore(teleHandle);
            additionalMessage = "You are currently " + previousUser.getPoints() + " points " +
                    "away from the user one rank above you!";
        } else {
            additionalMessage = "Good Job!! Keep up the good work XD";
        }

        SendMessage message = SendMessage
                .builder()
                .chatId(chat_id)
                .text(congratsMessage + additionalMessage)
                .build();
        return message;
    }

    public static String getPrefixFromRank(int rank) {
        String prefix = "th";
        if (rank % 10 == 1) {
            prefix = "st";
        }

        if (rank % 10 == 2) {
            prefix = "nd";
        }

        if (rank % 10 == 3) {
            prefix = "rd";
        }
        return prefix;
    }
}
