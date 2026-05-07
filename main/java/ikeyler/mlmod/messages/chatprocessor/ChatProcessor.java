package ikeyler.mlmod.messages.chatprocessor;

import ikeyler.mlmod.messages.Message;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public interface ChatProcessor {
    boolean process(Context ctx, Message message, ClientChatReceivedEvent event);
}
