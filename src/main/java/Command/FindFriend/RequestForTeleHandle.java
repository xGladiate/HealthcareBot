package Command.FindFriend;

import Database.UserDAO;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class RequestForTeleHandle {

    public static SendPhoto getUser(String friendTelehandle, long chat_id) {
        UserDAO userDAO = new UserDAO();
        String findFriendMessage;
        String photoLink;
        if (!userDAO.userExists(friendTelehandle)) {
            findFriendMessage = "Your friend hasn't start using this bot yet!!! :0";
            photoLink = "https://th.bing.com/th/id/OIP.gXFbdKsQs0q-qvvdhOo8lwHaHa?w=576&h=576&rs=1&pid=ImgDetMain";
        } else {
            int rank = userDAO.findUserRank(friendTelehandle);
            int points = userDAO.getUserPoints(friendTelehandle);
            findFriendMessage = "Your friend @" + friendTelehandle + " has " + points + " points and is at the "
                    + rank + getPrefixFromRank(rank)
                    + " rank!!";
            photoLink = "https://www.theladders.com/wp-content/uploads/handshake_190617.jpg";
        }

        SendPhoto message = SendPhoto
                .builder()
                .chatId(chat_id)
                // This time will send the picture using a URL
                .photo(new InputFile(photoLink))
                .caption(findFriendMessage)
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
