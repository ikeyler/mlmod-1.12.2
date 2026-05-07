package ikeyler.mlmod;

import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.commands.Command;
import ikeyler.mlmod.commands.CommandManager;
import ikeyler.mlmod.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ikeyler.mlmod.Main.*;

public class ChatListener {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final CommandManager manager = new CommandManager();

    private void processAlias(String cmd, ClientChatEvent event) {
        String[] split = cmd.split(" ", 2);
        String command = split[0].replaceFirst("/", "");
        String args = split.length > 1 ? split[1] : "";
        for (String alias : Configuration.MISC.COMMAND_ALIASES) {
            alias = alias.trim();
            String[] entry = alias.split(":", 2);
            if (entry.length < 2) continue;
            if (command.equalsIgnoreCase(entry[0]) && !command.equalsIgnoreCase(entry[1])) {
                event.setCanceled(true);
                String aliasCmd = (!entry[1].startsWith("/") ? "/" : "") + entry[1].toLowerCase();
                String newCmd = (aliasCmd + " " + args).trim();
                mc.ingameGUI.getChatGUI().addToSentMessages(newCmd);
                MinecraftForge.EVENT_BUS.post(new ClientChatEvent(newCmd));
                break;
            }
        }
    }

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        if (Configuration.MISC.DETECT_MINELAND.get() && !ModUtils.isOnMineland())
            return;
        messageManager.processMessage(event.getMessage().getUnformattedText(), event);
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatEvent event) {
        String message = event.getMessage();
        String[] split = message.split(" ");
        String start = split.length > 0 ? split[0] : "";

        if (message.startsWith("/")) {
            if (Configuration.MISC.COMMAND_ALIASES.length > 0)
                processAlias(message, event);

            String commandName = start.toLowerCase().replaceFirst("/", "");
            List<String> args = split.length > 1 ? Arrays.asList(split).subList(1, split.length) : Collections.emptyList();
            manager.getCommand(commandName).ifPresent(command -> {
                if (!command.isEnabled()) return;
                command.execute(args);
                event.setCanceled(true);
                if (command instanceof Command)
                    mc.ingameGUI.getChatGUI().addToSentMessages(message);
            });
        }

        if (message.startsWith("!") && Configuration.GENERAL.EXCL_MARK_TO_CHAT != Configuration.CHAT_MODE.OFF) {
            if (Configuration.MISC.DETECT_MINELAND.get() && !ModUtils.isOnMineland()) return;
            String newMessage = message.replaceFirst("!", "").trim();
            if (newMessage.isEmpty()) return;
            event.setCanceled(true);
            mc.ingameGUI.getChatGUI().addToSentMessages(message);
            String chatType = Configuration.GENERAL.EXCL_MARK_TO_CHAT == Configuration.CHAT_MODE.CC ? "/cc" : "/dc";
            mc.player.sendChatMessage(chatType + " " + newMessage);
        }
    }
}
