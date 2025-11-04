package ikeyler.mlmod.messages;

import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
    private final Pattern adPattern = Pattern.compile("/?(ad|ад|id|айди)\\s+(\\S+)");
    private final String you = new TextComponentTranslation("mlmod.you").getUnformattedText();
    private List<String> ignoredPlayers = new ArrayList<>();

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
        if (message == null) return;
        if (!message.isActive()) {
            event.setCanceled(true);
            return;
        }
        if (Messages.AD_MESSAGES.contains(message) && !Configuration.GENERAL.ADS.get()) {
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
            String[] split = matcher.group(1).split(" ");
            String player = split[split.length-1];
            String msg = trimMessage(matcher.group(2));
            String reply = message == Messages.CREATIVE_CHAT ? "/cc "+player+", " : "/dc "+player+", ";
            if (isPlayerIgnored(player)) {
                event.setCanceled(true); player = player+" (blocked)"; hideMessage=true;
            }
            if (message == Messages.CREATIVE_CHAT) messageCollector.addEntry(MessageType.CREATIVE_CHAT, player, msg);
            else messageCollector.addEntry(MessageType.DONATE_CHAT, player, msg);

            if (Configuration.GENERAL.CHAT_PLAYER_INTERACT.get() && !hideMessage) {
                ITextComponent component = messageComponent.createCopy();
                Style style = messageComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodplayerinteract " + player + ":::" + msg + ":::" + reply));
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
                event.setMessage(component);
            }
            return;
        }

        if (message == Messages.PM || message == Messages.PM_REPLY) {
            boolean hideMessage = false;
            String player = message == Messages.PM ? matcher.group(1) : you;
            String msg = trimMessage(matcher.group(2));

            if (isPlayerIgnored(player)) {
                event.setCanceled(true); player = player+" (blocked)"; hideMessage=true;
            }
            if (message == Messages.PM) messageCollector.addEntry(MessageType.PRIVATE_MESSAGE, player, msg);
            else messageCollector.addEntry(MessageType.PM_REPLY, player, matcher.group(1)+" -> "+msg);

            if (hideMessage) return;
            if (Configuration.GENERAL.PM_NOTIFICATION.get() && !mc.inGameHasFocus) {
                mc.world.playSound(mc.player, mc.player.getPosition(), new SoundEvent(new ResourceLocation("entity.experience_orb.pickup")), SoundCategory.MASTER, 0.5F, 0.7F);
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
        String translatePrefix = "[Перевести]";
        return StringUtils.removeEnd(msg, translatePrefix).trim();
    }
    private boolean isPlayerIgnored(String player) {
        return ignoredPlayers.contains(player.toLowerCase()) &&
                !player.equalsIgnoreCase(you);
    }
}
