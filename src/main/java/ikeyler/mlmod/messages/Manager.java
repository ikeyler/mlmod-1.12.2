package ikeyler.mlmod.messages;

import ikeyler.mlmod.cfg.Configuration;
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
import static ikeyler.mlmod.Main.logger;

public class Manager {
    private final List<Message> messageList = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Pattern adPattern = Pattern.compile("/?\\b(ad|ад|id|айди|join)\\s+(\\S+)");
    private String you = "you";
    private final String translatePrefix = "[Перевести]";
    private List<String> ignoredPlayers = new ArrayList<>();
    private final List<Message> ccDisabledMessages = Arrays.asList(
            Messages.CC_DISABLED, Messages.CC_DISABLED2, Messages.CC_DISABLED3,
            Messages.CC_DISABLED4, Messages.CC_DISABLED5
    );
    private boolean init;

    public void addMessages(List<Message> messages) {
        messageList.addAll(messages);
    }
    public void updateIgnoredPlayers() {
        ignoredPlayers = Arrays.stream(Configuration.GENERAL.IGNORED_PLAYERS).map(String::toLowerCase).collect(Collectors.toList());
    }
    public Message getMessage(String message) {
        if (Messages.CREATIVE_CHAT.matches(message)) return Messages.CREATIVE_CHAT;
        if (Messages.DONATE_CHAT.matches(message)) return Messages.DONATE_CHAT;

        return messageList.stream()
                .filter(msg -> msg.matches(message)).findFirst().orElse(null);
    }

    public void processMessages(Message message, ClientChatReceivedEvent event) {
        if (!init && (init=true)) {
            you = new TextComponentTranslation("mlmod.you").getUnformattedText();
            updateIgnoredPlayers();
        }
        if (message == null) return;
        if (!message.isActive() || (!Configuration.GENERAL.ADS.get() && Messages.AD_MESSAGES.contains(message))) {
            event.setCanceled(true);
            return;
        }
        if (ccDisabledMessages.contains(message)) {
            if (message == Messages.CC_DISABLED5) {
                event.setMessage(new TextComponentTranslation("mlmod.messages.use_excl_mark_to_cc"));
                return;
            }
            event.setCanceled(true);
            return;
        }

        ITextComponent messageComponent = event.getMessage();
        Matcher matcher = message.getMatcher();

        if (message == Messages.UNANSWERED_ASKS || message == Messages.UNREAD_MAIL) {
            String cmd = message == Messages.UNANSWERED_ASKS ? "/q" : "/mailgui";
            TextComponentTranslation component = new TextComponentTranslation("mlmod.messages.open_component");
            component.setStyle(component.getStyle()
                    .setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            cmd
                    )));
            event.setMessage(event.getMessage().createCopy().appendText(" ").appendSibling(component));
            return;
        }

        if (message == Messages.CREATIVE_CHAT || message == Messages.DONATE_CHAT) {
            boolean hideMessage = false;
            boolean setMessage = false;
            String[] split = matcher.group(1).split(" ");
            String player = split[split.length-1];
            String msg = trimMessage(matcher.group(2));
            String reply = message == Messages.CREATIVE_CHAT ? "/cc "+player+", " : "/dc "+player+", ";
            MessageType type = message == Messages.CREATIVE_CHAT ? MessageType.CREATIVE_CHAT : MessageType.DONATE_CHAT;
            if (isPlayerIgnored(player)) {
                event.setCanceled(true); player = player+" (blocked)"; hideMessage=true;
            }
            messageCollector.addEntry(type, player, msg);
            if (hideMessage) return;

            List<ITextComponent> siblingList = messageComponent.getSiblings();
            if (Configuration.GENERAL.HIDE_TRANSLATE.get() && siblingList.get(siblingList.size()-1).getUnformattedText().equalsIgnoreCase(translatePrefix)) {
                messageComponent = new TextComponentString("");
                siblingList.subList(0, siblingList.size()-1).forEach(messageComponent::appendSibling);
                setMessage = true;
            }

            if (isChatFormattingEnabled() && messageComponent.getSiblings().size() > 2) {
                String formatting = message == Messages.CREATIVE_CHAT ? Configuration.CHAT_FORMATTING.CREATIVE_CHAT : Configuration.CHAT_FORMATTING.DONATE_CHAT;
                if (formatting != null && !formatting.isEmpty()) {
                    ITextComponent formattedComponent = new TextComponentString("");
                    formattedComponent.appendText(TextUtil.replaceWithColorCodes(formatting) + " ");
                    List<ITextComponent> componentList = messageComponent.getSiblings();
                    componentList.subList(2, componentList.size())
                            .forEach(formattedComponent::appendSibling);
                    setMessage = true;
                    messageComponent = formattedComponent;
                }
            }

            if (Configuration.GENERAL.CHAT_PLAYER_INTERACT.get()) {
                ITextComponent component = messageComponent.createCopy();
                Style style = messageComponent.getStyle()
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.chat_player_interact.click", player)))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodplayerinteract " + player + ":::" + msg + ":::" + reply));
                component.setStyle(style);
                Matcher adMatcher = adPattern.matcher(msg.toLowerCase());
                List<String> adList = new ArrayList<>();
                while (adMatcher.find()) {String[] spl = adMatcher.group(0).split(" "); adList.add("/ad "+spl[spl.length-1].replace(",", ""));}
                if (!adList.isEmpty()) {
                    Style adStyle = TextUtil.newStyle().
                            setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodshowmessageads " + String.join(",", adList))).
                            setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.show_world_ads")));
                    TextComponentString adComponent = new TextComponentString(" ⬈");
                    adComponent.setStyle(adStyle);
                    component.appendSibling(adComponent);
                }
                setMessage = true;
                messageComponent = component;
            }
            if (setMessage) event.setMessage(messageComponent);
            return;
        }

        if (message == Messages.PM || message == Messages.PM_REPLY) {
            boolean hideMessage = false;
            String player = message == Messages.PM ? matcher.group(1) : you;
            String msg = trimMessage(matcher.group(2));
            MessageType type = message == Messages.PM ? MessageType.PRIVATE_MESSAGE : MessageType.PM_REPLY;
            String data = message == Messages.PM ? msg : matcher.group(1)+" -> "+msg;
            if (isPlayerIgnored(player)) {
                event.setCanceled(true); player = player+" (blocked)"; hideMessage=true;
            }
            messageCollector.addEntry(type, player, data);

            if (hideMessage) return;
            if (Configuration.GENERAL.PM_NOTIFICATION.get() && !mc.inGameHasFocus) {
                SoundUtil.playSound(TextUtil.NOTIFICATION_SOUND, 0.5F, 0.7F);
            }
            return;
        }

        if (message == Messages.WORLD_INVITE && Configuration.CREATIVE.SHOW_WORLD_ID.get()) {
            try {
                String[] split = messageComponent.getSiblings().get(0).getStyle().getClickEvent().getValue().split(" ");
                String worldID = split[split.length-1];
                event.setMessage(messageComponent.createCopy().appendText("§8(ID: "+worldID+")"));
            } catch (Exception e) {
                logger.error("error while reformatting world invite:", e);}
        }
    }

    private String trimMessage(String msg) {
        return StringUtils.removeEnd(msg, " "+translatePrefix).trim();
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
