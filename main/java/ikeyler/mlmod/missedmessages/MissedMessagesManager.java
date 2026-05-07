package ikeyler.mlmod.missedmessages;

import ikeyler.mlmod.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class MissedMessagesManager {
    private final List<MissedMessage> missedMessages;
    public MissedMessagesManager() {
        this.missedMessages = new ArrayList<>();
    }
    public void addMessage(String playerName, String message) {
        missedMessages.add(new MissedMessage(playerName, message, TextUtil.dateAsString()));
    }
    public void removeMessages(String playerName) {
        if (missedMessages.isEmpty()) return;
        missedMessages.removeIf(msg -> msg.getPlayerName().equalsIgnoreCase(playerName));
    }
    public List<MissedMessage> getMessages() {
        return missedMessages;
    }
    public void clearMessages() {
        missedMessages.clear();
    }
}