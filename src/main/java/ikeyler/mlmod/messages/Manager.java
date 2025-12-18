package ikeyler.mlmod.messages;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.SoundUtil;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.messageCollector;

public class Manager {
    private final List<Message> messageList = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Pattern adPattern = Pattern.compile("/?\\b(ad|ад|id|айди|join)\\s+(\\S+)");
    private String you;
    private final List<String> translatePrefix = Arrays.asList("[Перевести]", "[Translate]");
    private List<String> ignoredPlayers;

    public void addMessages(List<Message> messages) {
        messageList.addAll(messages);
    }
    public void update() {
        you = new TextComponentTranslation("mlmod.you").getUnformattedText();
        updateIgnoredPlayers();
    }
    public void updateIgnoredPlayers() {
        ignoredPlayers = Arrays.stream(Configuration.GENERAL.IGNORED_PLAYERS).map(String::toLowerCase).collect(Collectors.toList());
    }
    public Message getMessage(String message) {
        return messageList.stream()
                .filter(msg -> msg.matches(message)).findFirst().orElse(null);
    }

    public void processMessages(Message message, ClientChatReceivedEvent event) {
        if (message == null) return;
        if (!message.isActive() || (!Configuration.GENERAL.ADS.get() && Messages.AD_MESSAGES.contains(message))) {
            event.setCanceled(true);
            return;
        }

        ITextComponent messageComponent = event.getMessage();
        Matcher matcher = message.getMatcher();

        if (message == Messages.DEV_MODE_JOIN) {
            if (!Configuration.CREATIVE.DEV_MODE_JOIN.get()) event.setCanceled(true);
            ModUtils.enableNightDevMode();
            return;
        }

        if (message == Messages.UNANSWERED_ASKS || message == Messages.UNREAD_MAIL) {
            String cmd = message == Messages.UNANSWERED_ASKS ? "/q" : "/mailgui";
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
        if (message == Messages.CREATIVE_CHAT || message == Messages.DONATE_CHAT) {
            boolean hideMessage = false;
            boolean setMessage = false;
            String[] split = matcher.group(2).split(" ");
            String player = split[split.length-1];
            String msg = trimMessage(matcher.group(3));
            String reply = message == Messages.CREATIVE_CHAT ? "/cc "+player+", " : "/dc "+player+", ";
            MessageType type = message == Messages.CREATIVE_CHAT ? MessageType.CREATIVE_CHAT : MessageType.DONATE_CHAT;
            if (isPlayerIgnored(player)) {
                event.setCanceled(true); player = player+" (blocked)"; hideMessage=true;
            }
            messageCollector.addEntry(type, player, msg);
            if (hideMessage) return;

            List<ITextComponent> siblingList = messageComponent.getSiblings();
            if (Configuration.GENERAL.HIDE_TRANSLATE.get() && translatePrefix.contains(siblingList.get(siblingList.size()-1).getUnformattedText())) {
                messageComponent = new TextComponentString("");
                siblingList.subList(0, siblingList.size()-1).forEach(messageComponent::appendSibling);
                setMessage = true;
            }

            if (isChatFormattingEnabled() && messageComponent.getSiblings().size() > 2) {
                String formatting = message == Messages.CREATIVE_CHAT ? Configuration.CHAT_FORMATTING.CREATIVE_CHAT : Configuration.CHAT_FORMATTING.DONATE_CHAT;
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
                    List<String> adList = new ArrayList<>();
                    while (adMatcher.find()) {
                        String[] spl = adMatcher.group(0).split(" ");
                        String adId = spl[spl.length - 1].replace(",", "");
                        if (!adList.contains(adId))
                            adList.add("/ad " + adId.replace(",", ""));
                    }
                    if (!adList.isEmpty()) {
                        Style adStyle = TextUtil.newStyle().
                                setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodshowmessageads " + String.join(",", adList))).
                                setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.show_world_ads")));
                        TextComponentString adComponent = new TextComponentString(" ⬈");
                        adComponent.setStyle(adStyle);
                        messageComponent.appendSibling(adComponent);
                    }
                }
                setMessage = true;
            }
            if (setMessage) event.setMessage(messageComponent);
            return;
        }

        if (message == Messages.PM || message == Messages.PM_REPLY) {
            boolean hideMessage = false;
            String player = message == Messages.PM ? matcher.group(1) : you;
            String msg = trimMessage(matcher.group(3));
            MessageType type = message == Messages.PM ? MessageType.PRIVATE_MESSAGE : MessageType.PM_REPLY;
            String data = message == Messages.PM ? msg : matcher.group(1)+" -> "+msg;
            if (isPlayerIgnored(player)) {
                event.setCanceled(true); player = player+" (blocked)"; hideMessage=true;
            }
            messageCollector.addEntry(type, player, data);

            if (hideMessage) return;
            if (Configuration.GENERAL.PM_NOTIFICATION.get() && !mc.inGameHasFocus) {
                SoundUtil.playSound(ModUtils.NOTIFICATION_SOUND, 0.5F, 0.7F);
            }
            return;
        }

        if (message == Messages.PARTY_CHAT) {
            String[] split = matcher.group(1).split(" ");
            String player = split[split.length-1];
            String msg = trimMessage(matcher.group(2));
            messageCollector.addEntry(MessageType.PARTY_CHAT, player, msg);
            return;
        }

        if (message == Messages.WORLD_INVITE) {
            List<String> ignoredWorlds = Arrays.asList(Configuration.CREATIVE.IGNORED_WORLDS);
            if (!Configuration.CREATIVE.SHOW_WORLD_ID.get() && ignoredWorlds.isEmpty()) return;
            try {
                String[] split = messageComponent.getSiblings().get(0).getStyle().getClickEvent().getValue().split(" ");
                String worldId = split[split.length-1];
                if (!ignoredWorlds.isEmpty()) {
                    String worldName = matcher.group(2).toLowerCase();
                    List<String> ignoredNames = new ArrayList<>(ignoredWorlds)
                            .stream().map(s -> s.replaceFirst(":", "").toLowerCase()).collect(Collectors.toList());
                    if (ignoredWorlds.contains(worldId) || ignoredNames.stream().anyMatch(s -> s.contains(worldName))) {
                        event.setCanceled(true);
                        return;
                    }
                }
                if (!Configuration.CREATIVE.SHOW_WORLD_ID.get()) return;
                TextComponentTranslation idComp = new TextComponentTranslation("mlmod.messages.world_id", "§8§o"+worldId);
                idComp.setStyle(TextUtil.clickToCopyStyle("/ad "+worldId, false));
                event.setMessage(messageComponent.createCopy().appendSibling(idComp));
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
        return ignoredPlayers.contains(player.toLowerCase()) &&
                !player.equalsIgnoreCase(you);
    }
    private boolean isChatFormattingEnabled() {
        return Configuration.CHAT_FORMATTING.CHAT_FORMATTING.get() &&
                (!Configuration.CHAT_FORMATTING.CREATIVE_CHAT.isEmpty() || !Configuration.CHAT_FORMATTING.DONATE_CHAT.isEmpty());
    }
}
