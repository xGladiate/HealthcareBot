package Command.FindFriend;

import Database.UserDAO;
import Model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class RequestForTeleHandle {

    public static SendMessage getUser(String friendTelehandle, long chat_id) {
        UserDAO userDAO = new UserDAO();
        String findFriendMessage;
        if (!userDAO.userExists(friendTelehandle)) {
            findFriendMessage = "Your friend hasn't start using this bot yet!!! :0";
        } else {
            int rank = userDAO.findUserRank(friendTelehandle);
            int points = userDAO.getUserPoints(friendTelehandle);
            findFriendMessage = "Your friend @" + friendTelehandle + " has " + points + " points and is at the "
                    + rank + getPrefixFromRank(rank)
                    + " rank!!";
        }

        SendMessage message = SendMessage
                .builder()
                .chatId(chat_id)
                .text(findFriendMessage)
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
