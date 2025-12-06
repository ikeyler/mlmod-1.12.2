package ikeyler.mlmod.messages;

import ikeyler.mlmod.cfg.Config;
import ikeyler.mlmod.Main;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.SoundUtil;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.messageCollector;

public class Manager {
    private final List<Message> messageList = new ArrayList<>();
    private final Minecraft mc = Minecraft.getInstance();
    private final Pattern adPattern = Pattern.compile("/?\\b(ad|ад|id|айди|join)\\s+(\\S+)");
    private String you = "you";
    private final String translatePrefix = "[Перевести]";
    private List<String> ignoredPlayers = new ArrayList<>();
    private final List<Message> ccDisabledMessages = Arrays.asList(
            Messages.CC_DISABLED, Messages.CC_DISABLED2, Messages.CC_DISABLED3,
            Messages.CC_DISABLED4, Messages.CC_DISABLED5
    );
    private boolean init;
    public void updateIgnoredPlayers() {
        ignoredPlayers = Config.IGNORED_PLAYERS.get().stream().map(String::toLowerCase).collect(Collectors.toList());
    }
    public void addMessages(List<Message> messages) {
        messageList.addAll(messages);
    }
    public Message getMessage(String message) {
        return messageList.stream()
                .filter(msg -> msg.matches(message)).findFirst().orElse(null);
    }
    public void processMessages(Message message, ClientChatReceivedEvent event) {
        if (!init && (init=true)) {
            you = new TranslationTextComponent("mlmod.you").getString();
            updateIgnoredPlayers();
        }
        if (message == null) return;
        if (!message.isActive()) {
            event.setCanceled(true);
            return;
        }
        if (!Config.ADS.get() && Messages.AD_MESSAGES.contains(message)) {
            event.setCanceled(true);
            return;
        }
        if (ccDisabledMessages.contains(message)) {
            if (message == Messages.CC_DISABLED5) {
                event.setMessage(new TranslationTextComponent("mlmod.messages.use_excl_mark_to_cc"));
                return;
            }
            event.setCanceled(true);
            return;
        }

        StringTextComponent messageComponent = (StringTextComponent) event.getMessage();
        Matcher matcher = message.getMatcher();

        if (message == Messages.DEV_MODE_JOIN) {
            if (!Config.DEV_MODE_JOIN.get()) event.setCanceled(true);
            ModUtils.enableNightDevMode();
            return;
        }

        if (message == Messages.UNANSWERED_ASKS || message == Messages.UNREAD_MAIL) {
            String cmd = message == Messages.UNANSWERED_ASKS ? "/q" : "/mailgui";
            TranslationTextComponent component = new TranslationTextComponent("mlmod.messages.open_component");
            component.withStyle(component.getStyle()
                    .withClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            cmd
                    )));
            event.setMessage(messageComponent.copy().append(" ").append(component));
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
            if (Config.HIDE_TRANSLATE.get() && siblingList.get(siblingList.size()-1).getString().equalsIgnoreCase(translatePrefix)) {
                messageComponent = new StringTextComponent("");
                siblingList.subList(0, siblingList.size()-1).forEach(messageComponent::append);
                setMessage = true;
            }

            if (isChatFormattingEnabled() && messageComponent.getSiblings().size() > 2) {
                String formatting = message == Messages.CREATIVE_CHAT ? Config.FORMATTING_CC.get() : Config.FORMATTING_DC.get();
                if (formatting != null && !formatting.isEmpty()) {
                    StringTextComponent formattedComponent = new StringTextComponent("");
                    formattedComponent.append(TextUtil.replaceColorCodes(formatting) + " ");
                    List<ITextComponent> componentList = messageComponent.getSiblings();
                    componentList.subList(2, componentList.size())
                            .forEach(formattedComponent::append);
                    setMessage = true;
                    messageComponent = formattedComponent;
                }
            }

            if (Config.CHAT_PLAYER_INTERACT.get()) {
                Style style = messageComponent.getStyle()
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.chat_player_interact.click", player)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodplayerinteract " + player + ":::" + msg + ":::" + reply));
                messageComponent.setStyle(style);
                Matcher adMatcher = adPattern.matcher(msg.toLowerCase());
                List<String> adList = new ArrayList<>();
                while (adMatcher.find()) {String[] spl = adMatcher.group(0).split(" "); adList.add("/ad "+spl[spl.length-1].replace(",", ""));}
                if (!adList.isEmpty()) {
                    Style adStyle = TextUtil.newStyle().
                            withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodshowmessageads " + String.join(",", adList))).
                            withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.show_world_ads")));
                    StringTextComponent adComponent = new StringTextComponent(" ⬈");
                    adComponent.setStyle(adStyle);
                    messageComponent.append(adComponent);
                }
                setMessage = true;
            }
            if (setMessage) event.setMessage(messageComponent);
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
            if (Config.PM_NOTIFICATION.get() && GLFW.glfwGetWindowAttrib(mc.getWindow().getWindow(), GLFW.GLFW_FOCUSED) == 0) {
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

        if (message == Messages.WORLD_INVITE && Config.SHOW_WORLD_ID.get()) {
            try {
                String[] split = messageComponent.getSiblings().get(0).getStyle().getClickEvent().getValue().split(" ");
                String worldID = split[split.length-1];
                event.setMessage(messageComponent.copy().append("§8(ID: "+worldID+")"));
            }
            catch (Exception e) {
                Main.logger.error("error while reformatting world invite:", e);
            }
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
        return Config.CHAT_FORMATTING.get() &&
                (!Config.FORMATTING_CC.get().isEmpty() || !Config.FORMATTING_DC.get().isEmpty());
    }
}
