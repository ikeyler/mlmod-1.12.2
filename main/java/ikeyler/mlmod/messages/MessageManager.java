package ikeyler.mlmod.messages;

import ikeyler.mlmod.messages.chatprocessor.ChatProcessor;
import ikeyler.mlmod.messages.chatprocessor.Context;
import ikeyler.mlmod.messages.chatprocessor.Processors;
import ikeyler.mlmod.util.ModUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

import static ikeyler.mlmod.Main.messageCollector;
import static ikeyler.mlmod.Main.missedMessagesManager;
import static ikeyler.mlmod.messages.Messages.messagesMap;

public class MessageManager {
    private final Pattern adPattern = Pattern.compile("/?\\b(ad|ад|id|айди|join)\\s+(\\S+)");
    private final List<String> translatePrefix = Arrays.asList("[Перевести]", "[Translate]");
    private List<String> ignoredPlayers;
    private List<Message> actionbarMessages;
    private List<Message> adMessages;
    private final List<ChatProcessor> processors;
    public MessageManager() {
        processors = Arrays.asList(
                new Processors.HideProcessor(),
                new Processors.ActionBarProcessor(),
                new Processors.DevModeProcessor(),
                new Processors.UnsAskMailProcessor(),
                new Processors.CreativeDonateChatProcessor(),
                new Processors.PMProcessor(),
                new Processors.PartyChatProcessor(),
                new Processors.WorldInviteProcessor()
        );
    }

    public void update() {
        actionbarMessages = messagesMap.getOrDefault("ABAR", new ArrayList<>());
        adMessages = messagesMap.getOrDefault("AD", new ArrayList<>());
        ignoredPlayers = ModUtils.getIgnoredPlayers();
    }

    public void processMessage(String chatMessage, ClientChatReceivedEvent event) {
        Message message = Messages.getMessage(chatMessage);
        if (message == null) return;
        Context ctx = new Context(messageCollector, missedMessagesManager, ignoredPlayers, actionbarMessages, adMessages, adPattern, translatePrefix);
        for (ChatProcessor processor : processors) {
            if (processor.process(ctx, message, event))
                return;
        }
    }
}
