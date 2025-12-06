package ikeyler.mlmod;

import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.itemeditor.ChatEditor;
import ikeyler.mlmod.itemeditor.ItemEditor;
import ikeyler.mlmod.messages.MessageType;
import ikeyler.mlmod.messages.Messages;
import ikeyler.mlmod.util.ItemUtil;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.SoundUtil;
import ikeyler.mlmod.util.TextUtil;
import ikeyler.mlmod.variables.Variable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.*;
import static ikeyler.mlmod.util.ModUtils.MOD_PREFIX;

public class ChatListener {
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean messagesUpdated = false;
    private final List<String> commands = new ArrayList<>(
            Arrays.asList("/item", "/var", "/text", "/num", "/msgs", "/ignorelist", "/head", "/nightmode", "/vars", "/varsave"));

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        if (!messagesUpdated && (messagesUpdated=true)) {
            Messages.updateMessages();
        }
        messageManager.processMessages(messageManager.getMessage(event.getMessage().getUnformattedText()), event);
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatEvent event) {
        String message = event.getMessage();
        String[] split = message.split(" ");
        String start = split.length > 0 ? split[0] : "";

        if (message.startsWith("!") && Configuration.GENERAL.EXCL_MARK_TO_CHAT != Configuration.CHAT_MODE.OFF) {
            String newMessage = message.replaceFirst("!", "").trim();
            if (newMessage.isEmpty()) return;
            event.setCanceled(true);
            mc.ingameGUI.getChatGUI().addToSentMessages(message);
            String chatType = Configuration.GENERAL.EXCL_MARK_TO_CHAT == Configuration.CHAT_MODE.CC ? "/cc" : "/dc";
            mc.player.sendChatMessage(chatType+" "+newMessage);
            return;
        }

        if (commands.contains(start.toLowerCase())) {
            event.setCanceled(true);
            mc.ingameGUI.getChatGUI().addToSentMessages(message);
        }

        switch (start.toLowerCase()) {
            case "/mlmodplayerinteract":
                event.setCanceled(true);
                if (split.length > 1) {
                    String[] spl = message.replaceFirst("/mlmodplayerinteract ", "").split(":::");
                    String player = spl[0];
                    String msg = spl.length > 1 ? spl[1] : "";
                    String chat = spl.length > 2 ? spl[2] : "/m " + player + " ";
                    TextComponentTranslation menu = new TextComponentTranslation("mlmod.messages.chat_player_interact", "§7§o"+player);
                    Style write = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, chat));
                    Style copy = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodcopytext "+msg)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(msg)));
                    Style report = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/report " + player));
                    Style block = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlignore " + player));
                    Style find = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msgs find " + player));
                    Style who = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/who " + player));
                    menu.appendText("\n").appendSibling(new TextComponentTranslation("mlmod.messages.reply").setStyle(write)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.copy_message").setStyle(copy)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.report").setStyle(report)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.block").setStyle(block)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.find_messages").setStyle(find)).appendText(" ")
                            .appendSibling(new TextComponentTranslation("mlmod.messages.find_who").setStyle(who));
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
                if (!mc.player.isCreative()) {
                    ModUtils.sendCreativeModeNeeded();
                    return;
                }
                ItemStack item = null;
                String[] spl = message.split(" ", 2);
                String name = spl.length > 1 ? spl[1] : "";
                switch (start.toLowerCase()) {
                    case "/var":
                        item = ItemUtil.getDynamicVar(false);
                        break;
                    case "/text":
                        item = Item.getItemById(340).getDefaultInstance();
                        break;
                    case "/num":
                        item = Item.getItemById(341).getDefaultInstance();
                        break;
                }
                if (item != null) {
                    item.setStackDisplayName(TextUtil.replaceColorCodes(name));
                    mc.player.addItemStackToInventory(item);
                    mc.displayGuiScreen(new GuiInventory(mc.player));
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

            case "/ignorelist":
                List<String> ignoredPlayers = Arrays.asList(Configuration.GENERAL.IGNORED_PLAYERS);
                ITextComponent ignoreComponent = new TextComponentString(MOD_PREFIX);
                ignoreComponent.appendSibling(new TextComponentTranslation("mlmod.messages.ignorelist.ignore_list", ignoredPlayers.size()));
                ignoreComponent.appendText("\n");
                for (String pl:ignoredPlayers) {
                    ignoreComponent.appendText("§8- §7").appendSibling(new TextComponentString(pl)
                            .setStyle(TextUtil.newStyle()
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlignore "+pl))
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.ignorelist.click_to_remove")))));
                    ignoreComponent.appendText("\n");
                }
                ignoreComponent.appendSibling(new TextComponentTranslation("mlmod.messages.ignorelist.info")
                        .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mlignore "))));
                mc.player.sendMessage(ignoreComponent);
                break;

            case "/sound":
                if (!Configuration.CREATIVE.SOUND_COMMAND.get()) return;

                event.setCanceled(true);
                mc.ingameGUI.getChatGUI().addToSentMessages(message);
                if (split.length == 1) {
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX).
                            appendSibling(new TextComponentTranslation("mlmod.messages.sound.usage")).setStyle(
                                    TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.sound.usage_info")))
                            )
                            .appendText("\n").appendSibling(new TextComponentTranslation("mlmod.messages.sound.search_guide"))
                    );
                    return;
                }
                else if (split.length > 1 && !split[1].equalsIgnoreCase("find")) {
                    String sound = split[1].toLowerCase();
                    if (!SoundUtil.getSoundIds().contains(sound)) {
                        mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.sound.sounds_not_found"));
                        return;
                    }
                    float pitch = 1;
                    float volume = 1;
                    try {
                        pitch = split.length > 2 ? Float.parseFloat(split[2]) : pitch;
                        volume = split.length > 3 ? Float.parseFloat(split[3]) : volume;
                    } catch (Exception ignore) {ModUtils.sendIncorrectArguments(); return;}
                    mc.getSoundHandler().stopSounds();
                    mc.ingameGUI.setOverlayMessage(new TextComponentTranslation("mlmod.messages.sound.playing_sound", sound), true);
                    SoundUtil.playSound(sound, volume, pitch);
                    return;
                }

                String query = message.toLowerCase().replaceFirst("/sound find ", "");
                List<String> sounds = SoundUtil.findSoundIds(query);
                if (sounds.isEmpty()) {mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.sound.sounds_not_found")); return;}
                ITextComponent soundComponent = new TextComponentString(MOD_PREFIX).appendSibling(
                        new TextComponentTranslation("mlmod.messages.sound.sounds_found", sounds.size()));
                soundComponent.appendText("\n");
                boolean switchSoundColor = false;
                for (int i = 0; i < sounds.size(); i++) {
                    String sound = sounds.get(i);
                    String color = switchSoundColor ? "§f" : "§7";
                    soundComponent.appendSibling(new TextComponentString(color+sound)
                            .setStyle(TextUtil.newStyle()
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sound "+sound))
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.sound.click_to_play_sound")))));
                    if (i < sounds.size()-1) {soundComponent.appendText(", ");}
                    switchSoundColor = !switchSoundColor;
                }
                mc.player.sendMessage(soundComponent);
                break;

            case "/head":
                if (split.length == 1) {
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.head.usage")));
                    return;
                }
                if (!mc.player.isCreative()) {
                    ModUtils.sendCreativeModeNeeded();
                    return;
                }
                try {
                    String headName = split[1].toLowerCase();
                    mc.player.addItemStackToInventory(ItemUtil.getPlayerHead(headName));
                    mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.head.head_given", "§7"+headName)));
                } catch (Exception e) {ModUtils.sendCommandError(); logger.error(e);}
                break;

            case "/nightmode":
                ModUtils.nightModeCommand();
                break;

            case "/item":
                if (split.length == 1) {
                    ChatEditor chatEditor = new ChatEditor(mc.player.getHeldItemMainhand());
                    chatEditor.printChatEditor();
                    return;
                }
                List<String> actionList = new ArrayList<>(
                        Arrays.asList("name", "addlore", "removelore", "editlore", "enchant", "unenchant", "nbt", "enchlist", "break", "unbreak",
                                "dur", "durability"));
                String action = split[1].toLowerCase();
                ItemStack itemStack = mc.player.getHeldItemMainhand();

                if (!actionList.contains(action)) {
                    ModUtils.sendIncorrectArguments();
                    return;
                }
                String arg = message.indexOf(" ", 6) != -1 ? message.substring(message.indexOf(" ", 6)).trim() : "";
                String[] args = arg.split(" ");
                switch (action) {
                    case "name":
                        String oldName = itemStack.getDisplayName();
                        ItemEditor.renameItem(itemStack, TextUtil.replaceColorCodes(arg));
                        mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.itemeditor.old_name", oldName)
                                .setStyle(TextUtil.clickToViewStyle(oldName.replace("§", "&")))));
                        break;
                    case "addlore":
                        ItemEditor.addLore(itemStack, TextUtil.replaceColorCodes(arg));
                        break;
                    case "editlore":
                        try {
                            int loreIndex = Integer.parseInt(args[0]);
                            ItemEditor.editLore(itemStack, loreIndex, TextUtil.replaceColorCodes(arg.substring(arg.indexOf(" ")+1)));
                        } catch (Exception ignore) {
                            ModUtils.sendIncorrectArguments();
                            return;
                        }
                        break;
                    case "removelore":
                        if (arg.isEmpty()) {
                            ItemEditor.clearLore(itemStack);
                        }
                        else {
                            try {
                                int loreIndex = Integer.parseInt(args[0]);
                                ItemEditor.removeLore(itemStack, loreIndex);
                            } catch (Exception ignore) {
                                ModUtils.sendCommandError();
                                return;
                            }
                        }
                        break;
                    case "nbt":
                        String nbt = itemStack.hasTagCompound() ? itemStack.getTagCompound().toString() : "{}";
                        mc.player.sendMessage(new TextComponentString(MOD_PREFIX+nbt).setStyle(TextUtil.clickToCopyStyle(nbt)));
                        return;
                    case "enchlist":
                        List<String> enchantments = Enchantment.REGISTRY.getKeys().stream().map(ResourceLocation::getResourcePath).collect(Collectors.toList());
                        ITextComponent enchComp = new TextComponentString(MOD_PREFIX);
                        boolean switchEnchColor = false;
                        for (int i = 0; i < enchantments.size(); i++) {
                            String ench = enchantments.get(i);
                            String color = switchEnchColor ? "§f" : "§7";
                            enchComp.appendSibling(new TextComponentString(color+ench+" ("+Enchantment.getEnchantmentByLocation(ench).getTranslatedName(1)+color+")")
                                    .setStyle(TextUtil.newStyle()
                                            .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item enchant "+ench+" "))
                                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.itemeditor.click_to_enchant")))));
                            if (i < enchantments.size()-1) {enchComp.appendText(", ");}
                            switchEnchColor = !switchEnchColor;
                        }
                        mc.player.sendMessage(enchComp);
                        return;
                    case "enchant":
                        try {
                            Enchantment ench = Enchantment.getEnchantmentByLocation(args[0].toLowerCase());
                            int level = Integer.parseInt(args[1]);
                            ItemEditor.addEnchantment(itemStack, ench, level);
                        } catch (Exception ignore) {
                            ModUtils.sendIncorrectArguments();
                            return;
                        }
                        break;
                    case "unenchant":
                        if (arg.isEmpty()) {
                            ItemEditor.removeEnchantment(itemStack, null);
                        }
                        else {
                            try {
                                Enchantment ench = Enchantment.getEnchantmentByLocation(args[0].toLowerCase());
                                if (ench == null) {
                                    mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.itemeditor.no_ench_on_item"));
                                    return;
                                }
                                ItemEditor.removeEnchantment(itemStack, ench);
                            } catch (Exception ignore) {
                                ModUtils.sendCommandError();
                                return;
                            }
                        }
                        break;
                    case "break":
                    case "unbreak":
                        ItemEditor.setUnbreakable(itemStack, !ItemEditor.isUnbreakable(itemStack));
                        break;
                    case "dur":
                    case "durability":
                        try {
                            itemStack.setItemDamage(Integer.parseInt(args[0]));
                        } catch (Exception ignore) {
                            ModUtils.sendIncorrectArguments();
                            return;
                        }
                        break;
                }
                ModUtils.sendBarSuccess();
                mc.playerController.sendSlotPacket(itemStack, 36+mc.player.inventory.currentItem);
                break;

            case "/vars":
                List<Variable> vars = varCollector.readVariables();
                ITextComponent varComponent = new TextComponentString(MOD_PREFIX);
                varComponent.appendSibling(new TextComponentTranslation("mlmod.messages.vars.var_list", vars.size()));
                varComponent.appendText("\n");
                for (Variable variable:vars) {
                    String stringVar = variable.getType()+"::"+variable.getName();
                    varComponent.appendSibling(new TextComponentString("§c- §7")
                            .setStyle(TextUtil.newStyle()
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/removevar "+stringVar))
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.vars.click_to_remove")))))
                            .appendSibling(new TextComponentTranslation(variable.getType().getTranslation()).appendText("§7: "+variable.getName())
                            .setStyle(TextUtil.newStyle()
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/getvar "+stringVar))
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("mlmod.messages.vars.click_to_get")))));
                    varComponent.appendText("\n");
                }
                varComponent.appendSibling(new TextComponentTranslation("mlmod.messages.vars.info")
                        .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/varsave"))));
                mc.player.sendMessage(varComponent);
                break;

            case "/varsave":
                Variable variable = Variable.fromItem(mc.player.getHeldItemMainhand());
                if (variable == null) {
                    mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.vars.var_not_saved"));
                    return;
                }
                varCollector.addVariable(variable);
                mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.vars.var_saved", variable.getType().name()));
                break;

            case "/removevar":
                event.setCanceled(true);
                Variable parsedVar = Variable.fromString(message.replaceFirst("/removevar ", ""));
                if (parsedVar != null && varCollector.removeVariable(parsedVar)) {
                    mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.vars.var_removed", parsedVar.getName()));
                    return;
                }
                mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.vars.var_not_removed"));
                break;

            case "/getvar":
                event.setCanceled(true);
                if (!mc.player.isCreative()) {
                    ModUtils.sendCreativeModeNeeded();
                    return;
                }
                Variable parsedVar2 = Variable.fromString(message.replaceFirst("/getvar ", ""));
                if (parsedVar2 != null) {
                    mc.player.addItemStackToInventory(Variable.itemFromVariable(parsedVar2));
                    // still finding a way to update inventory!!!
                }
                break;

            case "/mlmodtogglemsgcollector":
                event.setCanceled(true);
                Configuration.GENERAL.MESSAGE_COLLECTOR = Configuration.Bool.fromBoolean(!Configuration.GENERAL.MESSAGE_COLLECTOR.get());
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
                ModUtils.sendSuccess();
                break;
            case "/mlmodshowmessageads":
                event.setCanceled(true);
                if (split.length < 2) return;
                TextComponentString adComponent = new TextComponentString(MOD_PREFIX);
                adComponent.appendSibling(new TextComponentTranslation("mlmod.messages.world_list"));
                adComponent.appendText("\n");
                for (String c:message.replaceFirst("/mlmodshowmessageads ", "").split(",")) {
                    TextComponentString ad = new TextComponentString("§8- §7"+c);
                    ad.appendText("\n");
                    ad.setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, c)));
                    adComponent.appendSibling(ad);
                }
                mc.player.sendMessage(adComponent);
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
}
