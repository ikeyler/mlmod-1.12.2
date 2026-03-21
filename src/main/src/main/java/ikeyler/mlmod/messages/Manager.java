package ikeyler.mlmod.messages;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.SoundUtil;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.messageCollector;
import static ikeyler.mlmod.messages.Messages.messagesMap;

public class Manager {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Pattern adPattern = Pattern.compile("/?\\b(ad|ад|id|айди|join)\\s+(\\S+)");
    private final List<String> translatePrefix = Arrays.asList("[Перевести]", "[Translate]");
    private List<String> ignoredPlayers;
    private List<Message> actionbarMessages;
    private List<Message> adMessages;

    public Manager() {}

    public void update() {
        actionbarMessages = messagesMap.getOrDefault("ABAR", new ArrayList<>());
        adMessages = messagesMap.getOrDefault("AD", new ArrayList<>());
        ignoredPlayers = Arrays.stream(Configuration.GENERAL.IGNORED_PLAYERS).map(String::toLowerCase).collect(Collectors.toList());
    }

    public void processMessages(String chatMessage, ClientChatReceivedEvent event) {
        Message message = Messages.getMessage(chatMessage);
        if (message == null) return;
        if (!message.isActive() || Messages.contains("HIDE", message) || (!Configuration.GENERAL.ADS.get() && adMessages.contains(message))) {
            event.setCanceled(true);
            //mc.player.sendMessage(new TextComponentString("hide: "+message.getMatcher().pattern()));
            return;
        }
        if (Configuration.GENERAL.MESSAGES_IN_ACTIONBAR.get() && actionbarMessages.contains(message)) {
            event.setCanceled(true);
            mc.ingameGUI.setOverlayMessage(event.getMessage(), false);
            return;
        }

        ITextComponent messageComponent = event.getMessage();
        Matcher matcher = message.getMatcher();

        if (Messages.contains("DEV_MODE_JOIN", message)) {
            if (!Configuration.CREATIVE.DEV_MODE_JOIN.get()) event.setCanceled(true);
            ModUtils.enableNightDevMode();
            return;
        }

        boolean isUnsAsk = Messages.contains("UNANSWERED_ASKS", message);
        boolean isUnreadMail = Messages.contains("UNREAD_MAIL", message);
        if (isUnsAsk || isUnreadMail) {
            String cmd = isUnsAsk ? "/q" : "/mailgui";
            TextComponentTranslation component = new TextComponentTranslation("mlmod.messages.open_component");
            component.setStyle(component.getStyle()
                    .setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            cmd
                    )));
            event.setMessage(messageComponent.createCopy().appendText(" ").appendSibling(component));
            return;
        }

        /*
        creative & donate chat handling
        */
        boolean isCreativeChat = Messages.contains("CREATIVE_CHAT", message);
        boolean isDonateChat = Messages.contains("DONATE_CHAT", message);
        if (isCreativeChat || isDonateChat) {
            boolean setMessage = false;
            String[] split = matcher.group(2).split(" ");
            String player = split[split.length-1];
            String msg = trimMessage(matcher.group(3));
            String reply = isCreativeChat ? "/cc "+player+", " : "/dc "+player+", ";
            MessageType type = isCreativeChat ? MessageType.CREATIVE_CHAT : MessageType.DONATE_CHAT;
            messageCollector.addEntry(type, player, msg);
            if (isPlayerIgnored(player)) {
                event.setCanceled(true);
                return;
            }
            List<ITextComponent> siblingList = messageComponent.getSiblings();
            if (Configuration.GENERAL.HIDE_TRANSLATE.get() && translatePrefix.contains(siblingList.get(siblingList.size()-1).getUnformattedText())) {
                messageComponent = new TextComponentString("");
                siblingList.subList(0, siblingList.size()-1).forEach(messageComponent::appendSibling);
                setMessage = true;
            }
            if (isChatFormattingEnabled() && messageComponent.getSiblings().size() > 2) {
                String formatting = isCreativeChat ? Configuration.CHAT_FORMATTING.CREATIVE_CHAT : Configuration.CHAT_FORMATTING.DONATE_CHAT;
                if (formatting != null && !formatting.isEmpty()) {
                    ITextComponent formattedComponent = new TextComponentString("");
                    formattedComponent.appendText(TextUtil.replaceColorCodes(formatting) + " ");
                    List<ITextComponent> componentList = messageComponent.getSiblings();
                    componentList.subList(2, componentList.size())
                            .forEach(formattedComponent::appendSibling);
                    setMessage = true;
                    messageComponent = formattedComponent;
                }
            }
            if (Configuration.GENERAL.CHAT_PLAYER_INTERACT.get()) {
                String sep = "§§";
                ITextComponent component = messageComponent.createCopy();
                Style style = messageComponent.getStyle()
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.chat_player_interact.click", player)))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodplayerinteract " + player + sep + msg + sep + reply));
                component.setStyle(style);
                if (Configuration.CREATIVE.SHOW_MESSAGE_ADS.get()) {
                    Matcher adMatcher = adPattern.matcher(msg.toLowerCase());
                    Set<String> adList = new HashSet<>();
                    while (adMatcher.find()) {
                        String[] spl = adMatcher.group(0).split(" ");
                        String adId = spl[spl.length - 1].replace(",", "");
                        if (adId.length() > 2)
                            adList.add("/ad " + adId);
                    }
                    if (!adList.isEmpty()) {
                        Style adStyle = TextUtil.newStyle().
                                setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodshowmessageads " + String.join(",", adList))).
                                setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.show_world_ads")));
                        String adSymbol = msg.endsWith(" ") ? "⬈" : " ⬈";
                        TextComponentString adComponent = new TextComponentString(adSymbol);
                        adComponent.setStyle(adStyle);
                        messageComponent.appendSibling(adComponent);
                    }
                }
                setMessage = true;
            }
            if (setMessage) event.setMessage(messageComponent);
            return;
        }

        boolean isPM = Messages.contains("PM", message);
        boolean isPMReply = Messages.contains("PM_REPLY", message);
        if (isPM || isPMReply) {
            String player = matcher.group(1);
            String msg = trimMessage(matcher.group(3));
            MessageType type = isPM ? MessageType.PRIVATE_MESSAGE : MessageType.PM_REPLY;
            String data = isPM ? msg : matcher.group(1)+" -> "+msg;
            messageCollector.addEntry(type, player, data);
            if (isPlayerIgnored(player)) {
                event.setCanceled(true);
                return;
            }
            if (Configuration.GENERAL.PM_NOTIFICATION.get() && !mc.inGameHasFocus) {
                SoundUtil.playSound(ModUtils.NOTIFICATION_SOUND, 0.5F, 0.7F);
            }
            return;
        }

        if (Messages.contains("PARTY_CHAT", message)) {
            String[] split = matcher.group(1).split(" ");
            String player = split[split.length-1];
            String msg = trimMessage(matcher.group(2));
            messageCollector.addEntry(MessageType.PARTY_CHAT, player, msg);
            return;
        }

        if (Messages.contains("WORLD_INVITE", message)) {
            List<String> ignoredWorlds = Arrays.asList(Configuration.CREATIVE.IGNORED_WORLDS);
            if (!Configuration.CREATIVE.SHOW_WORLD_ID.get() && ignoredWorlds.isEmpty()) return;
            try {
                String[] split = messageComponent.getSiblings().get(0).getStyle().getClickEvent().getValue().split(" ");
                String worldId = split[split.length-1];
                String worldName = matcher.group(2);
                if (!ignoredWorlds.isEmpty()) {
                    List<String> ignoredNames = new ArrayList<>(ignoredWorlds)
                            .stream().map(s -> s.replaceFirst(":", "").toLowerCase()).collect(Collectors.toList());
                    if (ignoredWorlds.contains(worldId) || ignoredNames.stream().anyMatch(s -> s.contains(worldName.toLowerCase()))) {
                        event.setCanceled(true);
                        return;
                    }
                }
                if (!Configuration.CREATIVE.SHOW_WORLD_ID.get()) return;
                TextComponentTranslation info = new TextComponentTranslation("mlmod.messages.world_id", "§8§o"+worldId);
                info.setStyle(TextUtil.clickToCopyStyle("/ad "+worldId, "id", false));
                info.appendSibling(new TextComponentTranslation("mlmod.copy")
                        .setStyle(TextUtil.clickToCopyStyle(worldName, "name", false)));
                event.setMessage(messageComponent.createCopy().appendSibling(info));
            }
            catch (Exception e) {
                Main.logger.error("error while reformatting world invite:", e);
            }
        }
    }

    private String trimMessage(String msg) {
        return translatePrefix.stream().filter(msg::endsWith).findFirst()
                .map(p -> StringUtils.removeEnd(msg, p).trim())
                .orElse(msg);
    }
    private boolean isPlayerIgnored(String player) {
        return ignoredPlayers.contains(player.toLowerCase());
    }
    private boolean isChatFormattingEnabled() {
        return Configuration.CHAT_FORMATTING.CHAT_FORMATTING.get() &&
                (!Configuration.CHAT_FORMATTING.CREATIVE_CHAT.isEmpty() || !Configuration.CHAT_FORMATTING.DONATE_CHAT.isEmpty());
    }
}
