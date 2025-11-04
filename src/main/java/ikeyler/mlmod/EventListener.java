package ikeyler.mlmod;

import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.messages.MessageType;
import ikeyler.mlmod.messages.Messages;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.*;
import java.util.List;

import static ikeyler.mlmod.Main.*;
import static ikeyler.mlmod.util.TextUtil.MOD_PREFIX;

public class EventListener {
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean messages_updated = false;

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        if (!messages_updated) {Messages.updateMessages();messages_updated=true;}
        messageManager.processMessages(messageManager.getMessage(event.getMessage().getUnformattedText()), event);
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatEvent event) {
        String message = event.getMessage();
        String[] split = message.split(" ");
        String start = split.length > 0 ? split[0] : "";
        List<String> commands = new ArrayList<>(Arrays.asList("/edit", "/var", "/text", "/num", "/msgs"));
        if (commands.contains(start.toLowerCase())) {
            event.setCanceled(true);
            mc.ingameGUI.getChatGUI().addToSentMessages(message);
        }

        switch (start.toLowerCase()) {
            case "/edit":
                if (split.length == 1) {
                    String name = mc.player.getHeldItemMainhand().getDisplayName();
                    Style style = TextUtil.clickToViewStyle(name.replace("§", "&"));
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX + name).setStyle(style));
                    return;
                }
                if (!mc.player.isCreative()) {
                    mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.creative_mode_needed"));
                    return;
                }
                String prevName = mc.player.getHeldItemMainhand().getDisplayName();
                Style edit = TextUtil.clickToViewStyle("/edit " + prevName.replace("§", "&"));
                ItemStack stack = mc.player.getHeldItemMainhand();
                stack.setStackDisplayName(TextUtil.replaceColors(message.replaceFirst(start, "").trim()));
                mc.playerController.sendSlotPacket(mc.player.getHeldItemMainhand(), 36+mc.player.inventory.currentItem);
                mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.edit.old_name", prevName)).setStyle(edit));
                break;

            case "/mlmodplayerinteract":
                event.setCanceled(true);
                if (split.length > 1) {
                    String[] spl = message.replaceFirst("/mlmodplayerinteract ", "").split(":::");
                    String player = spl[0];
                    String msg = spl.length > 1 ? spl[1] : "";
                    String chat = spl.length > 2 ? spl[2] : "/m " + player + " ";
                    TextComponentTranslation menu = new TextComponentTranslation("mlmod.messages.chat_player_interact", player);
                    Style write = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, chat));
                    Style copy = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodcopytext "+msg)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(msg)));
                    Style report = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/report " + player));
                    Style block = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlignore " + player));
                    menu.appendText("\n").appendSibling(new TextComponentTranslation("mlmod.messages.reply").setStyle(write)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.copy_message").setStyle(copy)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.report").setStyle(report)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.block").setStyle(block));
                    menu.setStyle(write);
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(menu));
                }
                break;

            case "/mlignore":
                event.setCanceled(true);
                if (split.length < 2) return;
                String player = split[1];
                List<String> players = new ArrayList<>(Arrays.asList(Configuration.GENERAL.IGNORED_PLAYERS));
                Style add_on_mineland = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore add " + player + " "));
                Style remove_on_mineland = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore remove " + player + " "));
                if (!players.contains(player)) {
                    players.add(player);
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.ignore.player_added", player).appendText(". ").appendSibling(
                            new TextComponentTranslation("mlmod.messages.ignore.add_on_mineland", new TextComponentTranslation("mlmod.mineland"))).setStyle(add_on_mineland)));
                } else {
                    players.remove(player);
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.ignore.player_removed", player).appendText(". ").appendSibling(
                            new TextComponentTranslation("mlmod.messages.ignore.remove_on_mineland", new TextComponentTranslation("mlmod.mineland"))).setStyle(remove_on_mineland)));
                }
                Configuration.GENERAL.IGNORED_PLAYERS = players.toArray(new String[0]);
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
                messageManager.updateIgnoredPlayers();
                break;

            case "/var":
            case "/text":
            case "/num":
                event.setCanceled(true);
                if (!mc.player.isCreative()) {
                    mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.creative_mode_needed"));
                    return;
                }
                ItemStack item = null;
                String[] spl = message.split(" ", 2);
                String name = spl.length > 1 ? spl[1] : "";
                switch (start.toLowerCase()) {
                    case "/var":
                        item = Item.getItemById(378).getDefaultInstance();
                        String json = "{display:{Lore:['{\"italic\":false,\"color\":\"gray\",\"text\":\"Тип переменной, который может хранить что угодно.\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"Значение динамической переменной\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"может быть задано с помощью блока\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"\\\\u0027Установить переменную\\\\u0027, а динамические\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"переменные с тем же именем сохраняют\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"одно и то же значение.\"}','{\"text\":\"\"}','{\"italic\":false,\"color\":\"light_purple\",\"text\":\"Значения:\"}','{\"extra\":[{\"italic\":false,\"color\":\"aqua\",\"text\":\"\\\\u003e \"},{\"italic\":false,\"color\":\"gray\",\"text\":\"Имя (введите в чат, держа предмет в руке.\"}],\"text\":\"\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"Используй имена, такие как %player%\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"для переменных, зависящих от игрока)\"}','{\"extra\":[{\"italic\":false,\"color\":\"aqua\",\"text\":\"\\\\u003e \"},{\"italic\":false,\"color\":\"gray\",\"text\":\"Сохранение переменной (Присесть + Правый клик,\"}],\"text\":\"\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\"держа предмет, тег \\\\u0027СОХРАНИТЬ\\\\u0027 позволяет сохранить\"}','{\"italic\":false,\"color\":\"gray\",\"text\":\" переменную навсегда)\"}'],Name:'{\"italic\":false,\"color\":\"red\",\"text\":\"Динамическая переменная\"}'}}";
                        try {
                            NBTTagCompound nbt = JsonToNBT.getTagFromJson(json);
                            item.setTagCompound(nbt);
                        } catch (Exception e) {
                            logger.error("error while parsing tag: ", e);
                        }
                        break;
                    case "/text":
                        item = Item.getItemById(340).getDefaultInstance();
                        break;
                    case "/num":
                        item = Item.getItemById(341).getDefaultInstance();
                        break;
                }
                if (item != null) {
                    item.setStackDisplayName(TextUtil.replaceColors(name));
                    // не апдейтает инвентарь
                    mc.player.addItemStackToInventory(item);
                }
                break;

            case "/msgs":
                if (split.length == 1) {
                    int totalMessages = messageCollector.readAll().size();
                    TextComponentString component = new TextComponentString(MOD_PREFIX);
                    component.appendSibling(new TextComponentTranslation("mlmod.messages.collector.total", totalMessages));
                    component.appendText("\n").appendSibling(new TextComponentTranslation("mlmod.messages.collector.search_guide"));
                    Style msgsStyle = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "mlmodData.txt"));
                    component.appendText("\n").appendSibling(new TextComponentTranslation("mlmod.messages.collector.info").setStyle(msgsStyle)).appendText("\n");
                    String state = Configuration.GENERAL.MESSAGE_COLLECTOR.equals(Configuration.Bool.TRUE) ? new TextComponentTranslation("mlmod.messages.collector.state_enabled").getUnformattedText() : new TextComponentTranslation("mlmod.messages.collector.state_disabled").getUnformattedText();
                    msgsStyle = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodtogglemsgcollector"));
                    component.appendSibling(new TextComponentString(state).setStyle(msgsStyle));
                    mc.player.sendMessage(component);
                    return;
                }
                if (split.length > 2 && split[1].equalsIgnoreCase("find")) {
                    String query = message.toLowerCase().replaceFirst("/msgs find ", "");
                    MessageType type = Arrays.stream(MessageType.values())
                            .filter(t -> t.getName().equalsIgnoreCase(split[split.length - 1]))
                            .findFirst()
                            .orElse(null);
                    if (type != null)
                        query = query.split(" ").length == 1 ? null : query.substring(0, query.lastIndexOf(" ")).trim();
                    messageCollector.findAsync(query, type, 50, "mc");
                }
                break;

            case "/mlmodtogglemsgcollector":
                event.setCanceled(true);
                Configuration.GENERAL.MESSAGE_COLLECTOR = Configuration.Bool.fromBoolean(!Configuration.GENERAL.MESSAGE_COLLECTOR.get());
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
                mc.player.sendMessage(new TextComponentTranslation("mlmod.success"));
                break;
            case "/mlmodshowmessageads":
                event.setCanceled(true);
                if (split.length < 2) return;
                TextComponentString component = new TextComponentString(MOD_PREFIX);
                component.appendSibling(new TextComponentTranslation("mlmod.messages.world_list"));
                component.appendText("\n");
                for (String c:message.replaceFirst("/mlmodshowmessageads ", "").split(",")) {
                    TextComponentString ad = new TextComponentString("§8- §7"+c);
                    ad.appendText("\n");
                    ad.setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, c)));
                    component.appendSibling(ad);
                }
                mc.player.sendMessage(component);
                break;
            case "/mlmodcopytext":
                event.setCanceled(true);
                if (split.length < 2) return;
                String text = message.replaceFirst("/mlmodcopytext ", "");
                TextUtil.copyToClipboard(text);
                mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.text_copied", text));
                break;
            default:
                break;
        }
    }
    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (Keybinds.hub.isPressed()) mc.player.sendChatMessage("/hub");
        else if (Keybinds.play.isPressed()) mc.player.sendChatMessage("/play");
        else if (Keybinds.build.isPressed()) mc.player.sendChatMessage("/build");
        else if (Keybinds.dev.isPressed()) mc.player.sendChatMessage("/dev");
    }
}


