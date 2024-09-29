package Command.LeaderboardDisplay;

import Database.UserDAO;
import Model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class LeaderboardDisplayMessage {
    public static SendPhoto leaderboardDisplayMessage(long chat_id, String teleHandle) {
        UserDAO userDAO = new UserDAO();

        int rank = userDAO.findUserRank(teleHandle);
        String photoLink;

        String congratsMessage = "You are currently in the " + rank
                + getPrefixFromRank(rank) + " position!!\n";

        String additionalMessage;
        if (rank != 1) {
            User previousUser = userDAO.findUserBefore(teleHandle);
            photoLink = "https://sayingimages.com/wp-content/uploads/you-got-this-dog-meme.jpg.webp";
            additionalMessage = "You are currently " + previousUser.getPoints() + " points " +
                    "away from the user one rank above you!";
        } else {
            additionalMessage = "Good Job!! Keep up the good work XD";
            photoLink = "https://cdn0.iconfinder.com/data/icons/awards-6/500/award_first-512.png";
        }

        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                // This time will send the picture using a URL
                .photo(new InputFile(photoLink))
                .caption(congratsMessage + additionalMessage)
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
