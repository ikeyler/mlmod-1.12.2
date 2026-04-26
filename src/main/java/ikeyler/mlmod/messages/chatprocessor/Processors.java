package ikeyler.mlmod.messages.chatprocessor;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.messages.Message;
import ikeyler.mlmod.messages.MessageType;
import ikeyler.mlmod.messages.Messages;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class Processors {
    public static class HideProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            if (!message.isActive() || Messages.contains("HIDE", message) || (!Configuration.GENERAL.ADS.get() && ctx.adMessages.contains(message))) {
                event.setCanceled(true);
                return true;
            }
            return false;
        }
    }
    public static class ActionBarProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            if (Configuration.GENERAL.MESSAGES_IN_ACTIONBAR.get() && ctx.actionbarMessages.contains(message)) {
                event.setCanceled(true);
                Minecraft mc = Minecraft.getMinecraft();
                mc.ingameGUI.setOverlayMessage(event.getMessage(), false);
                return true;
            }
            return false;
        }
    }
    public static class DevModeProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            if (Messages.contains("DEV_MODE_JOIN", message)) {
                if (!Configuration.CREATIVE.DEV_MODE_JOIN.get())
                    event.setCanceled(true);
                ModUtils.enableNightDevMode();
                return true;
            }
            return false;
        }
    }
    public static class UnsAskMailProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            boolean isUnsAsk = Messages.contains("UNANSWERED_ASKS", message);
            boolean isUnreadMail = Messages.contains("UNREAD_MAIL", message);
            if (isUnsAsk || isUnreadMail) {
                String cmd = isUnsAsk ? "/q" : "/mailgui";
                TextComponentTranslation button = new TextComponentTranslation("mlmod.messages.open_component");
                button.setStyle(button.getStyle()
                        .setClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                cmd
                        )));
                event.setMessage(event.getMessage().createCopy().appendText(" ").appendSibling(button));
                return true;
            }
            return false;
        }
    }
    public static class CreativeDonateChatProcessor implements ChatProcessor {
        private boolean isChatFormattingEnabled() {
            return Configuration.CHAT_FORMATTING.CHAT_FORMATTING.get() &&
                    (!Configuration.CHAT_FORMATTING.CREATIVE_CHAT.isEmpty() || !Configuration.CHAT_FORMATTING.DONATE_CHAT.isEmpty());
        }
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            boolean isCreativeChat = Messages.contains("CREATIVE_CHAT", message);
            boolean isDonateChat = Messages.contains("DONATE_CHAT", message);
            if (isCreativeChat || isDonateChat) {
                ITextComponent messageComponent = event.getMessage();
                Matcher matcher = message.getMatcher();
                boolean setMessage = false;
                String[] split = matcher.group(2).split(" ");
                String player = split[split.length - 1];
                String msg = TextUtil.trimMessage(matcher.group(3), ctx.translatePrefix);
                String reply = isCreativeChat ? "/cc " + player + ", " : "/dc " + player + ", ";
                MessageType type = isCreativeChat ? MessageType.CREATIVE_CHAT : MessageType.DONATE_CHAT;
                ctx.messageCollector.addEntry(type, player, msg);
                if (ModUtils.isPlayerIgnored(player)) {
                    event.setCanceled(true);
                    return true;
                }
                List<ITextComponent> siblingList = messageComponent.getSiblings();
                if (Configuration.GENERAL.HIDE_TRANSLATE.get() && ctx.translatePrefix.contains(siblingList.get(siblingList.size() - 1).getUnformattedText())) {
                    messageComponent = new TextComponentString("");
                    siblingList.subList(0, siblingList.size() - 1).forEach(messageComponent::appendSibling);
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
                        Matcher adMatcher = ctx.adPattern.matcher(msg.toLowerCase());
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
                return true;
            }
            return false;
        }
    }
    public static class PMProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            boolean isPM = Messages.contains("PM", message);
            boolean isPMReply = Messages.contains("PM_REPLY", message);
            if (isPM || isPMReply) {
                Minecraft mc = Minecraft.getMinecraft();
                Matcher matcher = message.getMatcher();
                String player = matcher.group(1);
                String msg = TextUtil.trimMessage(matcher.group(3), ctx.translatePrefix);
                MessageType type = isPM ? MessageType.PRIVATE_MESSAGE : MessageType.PM_REPLY;
                String data = isPM ? msg : matcher.group(1) + " -> " + msg;
                ctx.messageCollector.addEntry(type, player, data);
                if (ModUtils.isPlayerIgnored(player)) {
                    event.setCanceled(true);
                    return true;
                }
                if (Configuration.GENERAL.PM_NOTIFICATION.get() && !mc.inGameHasFocus) {
                    SoundUtil.playSound(ModUtils.NOTIFICATION_SOUND, 0.5F, 0.7F);
                }
                return true;
            }
            return false;
        }
    }
    public static class PartyChatProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            if (Messages.contains("PARTY_CHAT", message)) {
                Matcher matcher = message.getMatcher();
                String[] split = matcher.group(1).split(" ");
                String player = split[split.length-1];
                String msg = TextUtil.trimMessage(matcher.group(2), ctx.translatePrefix);
                ctx.messageCollector.addEntry(MessageType.PARTY_CHAT, player, msg);
                return true;
            }
            return false;
        }
    }
    public static class WorldInviteProcessor implements ChatProcessor {
        @Override
        public boolean process(Context ctx, Message message, ClientChatReceivedEvent event) {
            if (Messages.contains("WORLD_INVITE", message)) {
                List<String> ignoredWorlds = Arrays.asList(Configuration.CREATIVE.IGNORED_WORLDS);
                if (!Configuration.CREATIVE.SHOW_WORLD_ID.get() && ignoredWorlds.isEmpty()) return true;
                try {
                    ITextComponent messageComponent = event.getMessage();
                    Matcher matcher = message.getMatcher();
                    String[] split = messageComponent.getSiblings().get(0).getStyle().getClickEvent().getValue().split(" ");
                    String worldId = split[split.length-1];
                    String worldName = matcher.group(2);
                    if (!ignoredWorlds.isEmpty()) {
                        List<String> ignoredNames = new ArrayList<>(ignoredWorlds)
                                .stream().map(s -> s.replaceFirst(":", "").toLowerCase()).collect(Collectors.toList());
                        if (ignoredWorlds.contains(worldId) || ignoredNames.stream().anyMatch(s -> s.contains(worldName.toLowerCase()))) {
                            event.setCanceled(true);
                            return true;
                        }
                    }
                    if (!Configuration.CREATIVE.SHOW_WORLD_ID.get()) return true;
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
            return false;
        }
    }
}
