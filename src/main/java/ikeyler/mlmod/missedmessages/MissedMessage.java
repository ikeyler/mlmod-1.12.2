package ikeyler.mlmod.missedmessages;

public class MissedMessage {
    private final String playerName;
    private final String message;
    private final String date;
    public MissedMessage(String playerName, String message, String date) {
        this.playerName = playerName;
        this.message = message;
        this.date = date;
    }
    public String getPlayerName() {
        return playerName;
    }
    public String getMessage() {
        return message;
    }
    public String getDate() {
        return date;
    }
}
