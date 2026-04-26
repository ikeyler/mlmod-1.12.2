package ikeyler.mlmod.messages.chatprocessor;

import ikeyler.mlmod.messages.Message;
import ikeyler.mlmod.messages.MessageCollector;

import java.util.List;
import java.util.regex.Pattern;

public class Context {
    public final MessageCollector messageCollector;
    public final List<Message> actionbarMessages;
    public final List<Message> adMessages;
    public final Pattern adPattern;
    public final List<String> translatePrefix;
    public Context(MessageCollector messageCollector,
                   List<String> ignoredPlayers, List<Message> actionbarMessages,
                   List<Message> adMessages, Pattern adPattern,
                   List<String> translatePrefix) {
        this.messageCollector = messageCollector;
        this.actionbarMessages = actionbarMessages;
        this.adMessages = adMessages;
        this.adPattern = adPattern;
        this.translatePrefix = translatePrefix;
    }
}
