package ikeyler.mlmod.messages;

public enum MessageType {
    CREATIVE_CHAT("CC"),
    DONATE_CHAT("DC"),
    PRIVATE_MESSAGE("LS"),
    PM_REPLY("REPLY");

    MessageType(String name) {this.name = name;}
    private final String name;
    public String getName() {
        return name;
    }
}
