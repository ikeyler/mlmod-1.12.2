package ikeyler.mlmod;

import ikeyler.mlmod.cfg.ClothConfig;
import ikeyler.mlmod.cfg.Config;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.*;
import static ikeyler.mlmod.util.ModUtils.MOD_PREFIX;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ChatListener {
    private final Minecraft mc = Minecraft.getInstance();
    private boolean messagesUpdated = false;
    private final List<String> commands = new ArrayList<>(
            Arrays.asList("/mlc", "/item", "/var", "/text", "/num", "/msgs", "/ignorelist", "/head", "/nightmode", "/vars", "/varsave"));

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        if (!messagesUpdated && (messagesUpdated=true)) {
            Messages.updateMessages();
        }
        messageManager.processMessages(messageManager.getMessage(event.getMessage().getString()), event);
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatEvent event) {
        String message = event.getMessage();
        String[] split = message.split(" ");
        String start = split.length > 0 ? split[0] : "";
        if (commands.contains(start.toLowerCase())) {
            event.setCanceled(true);
            mc.gui.getChat().addRecentChat(message);
        }

        if (message.startsWith("!") && Config.EXCL_MARK_TO_CHAT.get() != Config.CHAT_MODE.OFF) {
            String newMessage = message.replaceFirst("!", "").trim();
            if (newMessage.isEmpty()) return;
            event.setCanceled(true);
            mc.gui.getChat().addRecentChat(message);
            String chatType = Config.EXCL_MARK_TO_CHAT.get() == Config.CHAT_MODE.CC ? "/cc" : "/dc";
            mc.player.chat(chatType+" "+newMessage);
            return;
        }

        switch (start.toLowerCase()) {
            case "/mlc":
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mc.execute(() -> {
                            Screen parent = mc.screen;
                            Screen build = ClothConfig.buildConfigScreen(parent);
                            mc.setScreen(build);
                        });
                    }
                }, 100);
                break;

            case "/mlmodplayerinteract":
                event.setCanceled(true);
                if (split.length > 1) {
                    String[] spl = message.replaceFirst("/mlmodplayerinteract ", "").split(":::");
                    String player = spl[0];
                    String msg = spl.length > 1 ? spl[1] : "";
                    String chat = spl.length > 2 ? spl[2] : "/m " + player + " ";
                    TranslationTextComponent menu = new TranslationTextComponent("mlmod.messages.chat_player_interact", "§7§o"+player);
                    Style write = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, chat));
                    Style copy = TextUtil.clickToCopyStyle(msg, true);
                    Style report = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/report " + player));
                    Style block = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlignore " + player));
                    Style find = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msgs find " + player));
                    Style who = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/who " + player));
                    menu.append("\n").append(new TranslationTextComponent("mlmod.messages.reply").setStyle(write)).append(" ")
                            .append(new TranslationTextComponent("mlmod.messages.copy_message").setStyle(copy)).append(" ")
                            .append(new TranslationTextComponent("mlmod.messages.report").setStyle(report)).append(" ")
                            .append(new TranslationTextComponent("mlmod.messages.block").setStyle(block)).append(" ")
                            .append(new TranslationTextComponent("mlmod.messages.find_messages").setStyle(find)).append(" ")
                            .append(new TranslationTextComponent("mlmod.messages.find_who").setStyle(who));
                    ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).append(menu));
                }
                break;

            case "/mlignore":
                event.setCanceled(true);
                if (split.length < 2) return;
                String player = split[1];
                List<String> players = new ArrayList<>(Config.IGNORED_PLAYERS.get());
                Style add_on_mineland = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore add " + player + " "));
                Style remove_on_mineland = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore remove " + player + " "));
                if (!players.contains(player)) {
                    players.add(player);
                    ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).append(new TranslationTextComponent("mlmod.messages.ignore.player_added", player).append(". ").append(
                            new TranslationTextComponent("mlmod.messages.ignore.add_on_mineland", new TranslationTextComponent("mlmod.mineland"))).withStyle(add_on_mineland)));
                } else {
                    players.remove(player);
                    ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).append(new TranslationTextComponent("mlmod.messages.ignore.player_removed", player).append(". ").append(
                            new TranslationTextComponent("mlmod.messages.ignore.remove_on_mineland", new TranslationTextComponent("mlmod.mineland"))).withStyle(remove_on_mineland)));
                }
                Config.IGNORED_PLAYERS.set(players);
                Config.spec.save();
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
                        item = Items.BOOK.getDefaultInstance();
                        break;
                    case "/num":
                        item = Items.SLIME_BALL.getDefaultInstance();
                        break;
                }
                if (item != null) {
                    item.setHoverName(new StringTextComponent(TextUtil.replaceColorCodes(name)));
                    mc.player.addItem(item);
                    mc.setScreen(new InventoryScreen(mc.player));
                }
                break;

            case "/msgs":
                if (split.length == 1) {
                    int totalMessages = messageCollector.readAll().size();
                    StringTextComponent component = new StringTextComponent(MOD_PREFIX);
                    component.append(new TranslationTextComponent("mlmod.messages.collector.total", totalMessages));
                    component.append("\n").append(new TranslationTextComponent("mlmod.messages.collector.search_guide"));
                    Style msgsStyle = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "mlmodData.txt"));
                    component.append("\n").append(new TranslationTextComponent("mlmod.messages.collector.info").setStyle(msgsStyle)).append("\n");
                    String state = Config.MESSAGE_COLLECTOR.get() ? new TranslationTextComponent("mlmod.messages.collector.state_enabled").getString() : new TranslationTextComponent("mlmod.messages.collector.state_disabled").getString();
                    msgsStyle = TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodtogglemsgcollector"));
                    component.append(new StringTextComponent(state).setStyle(msgsStyle));
                    ModUtils.sendMessage(component);
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
                List<? extends String> ignoredPlayers = Config.IGNORED_PLAYERS.get();
                StringTextComponent ignoreComponent = new StringTextComponent(MOD_PREFIX);
                ignoreComponent.append(new TranslationTextComponent("mlmod.messages.ignorelist.ignore_list", ignoredPlayers.size()));
                ignoreComponent.append("\n");
                for (String pl:ignoredPlayers) {
                    ignoreComponent.append("§8- §7").append(new StringTextComponent(pl)
                            .setStyle(TextUtil.newStyle()
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlignore "+pl))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.ignorelist.click_to_remove")))));
                    ignoreComponent.append("\n");
                }
                ignoreComponent.append(new TranslationTextComponent("mlmod.messages.ignorelist.info")
                        .setStyle(TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mlignore "))));
                ModUtils.sendMessage(ignoreComponent);
                break;

            case "/sound":
                if (!Config.SOUND_COMMAND.get()) return;

                event.setCanceled(true);
                mc.gui.getChat().addRecentChat(message);
                if (split.length == 1) {
                    ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).
                            append(new TranslationTextComponent("mlmod.messages.sound.usage")).setStyle(
                                    TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.sound.usage_info")))
                            )
                            .append("\n").append(new TranslationTextComponent("mlmod.messages.sound.search_guide"))
                            .append("\n").append(new TranslationTextComponent("mlmod.messages.sound.info")));
                    return;
                }
                else if (split.length > 1 && !split[1].equalsIgnoreCase("find")) {
                    String sound = split[1].toLowerCase();
                    if (!SoundUtil.getSoundIds().contains(sound)) {
                        ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.sound.sounds_not_found"));
                        return;
                    }
                    float pitch = 1;
                    float volume = 1;
                    try {
                        pitch = split.length > 2 ? Float.parseFloat(split[2]) : pitch;
                        volume = split.length > 3 ? Float.parseFloat(split[3]) : volume;
                    } catch (Exception ignore) {ModUtils.sendIncorrectArguments(); return;}
                    mc.getSoundManager().stop();
                    mc.gui.setOverlayMessage(new TranslationTextComponent("mlmod.messages.sound.playing_sound", sound), true);
                    SoundUtil.playSound(sound, volume, pitch);
                    return;
                }

                String query = message.toLowerCase().replaceFirst("/sound find ", "");
                List<String> sounds = SoundUtil.findSoundIds(query);
                if (sounds.isEmpty()) {
                    ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.sound.sounds_not_found"));
                    return;
                }
                StringTextComponent soundComponent = new StringTextComponent(MOD_PREFIX);
                soundComponent.append(new TranslationTextComponent("mlmod.messages.sound.sounds_found", sounds.size()));
                soundComponent.append("\n");
                boolean switchSoundColor = false;
                for (int i = 0; i < sounds.size(); i++) {
                    String sound = sounds.get(i);
                    String color = switchSoundColor ? "§f" : "§7";
                    soundComponent.append(new StringTextComponent(color+sound)
                            .setStyle(TextUtil.newStyle()
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sound "+sound))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.sound.click_to_play_sound")))));
                    if (i < sounds.size()-1) {soundComponent.append(", ");}
                    switchSoundColor = !switchSoundColor;
                }
                ModUtils.sendMessage(soundComponent);
                break;

            case "/head":
                if (split.length == 1) {
                    ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).append(new TranslationTextComponent("mlmod.messages.head.usage")));
                    return;
                }
                if (!mc.player.isCreative()) {
                    ModUtils.sendCreativeModeNeeded();
                    return;
                }
                try {
                    String headName = split[1].toLowerCase();
                    mc.player.addItem(ItemUtil.getPlayerHead(headName));
                    ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).append(new TranslationTextComponent("mlmod.messages.head.head_given", "§7"+headName)));
                } catch (Exception e) {ModUtils.sendCommandError(); logger.error(e);}
                break;

            case "/nightmode":
                ModUtils.nightModeCommand();
                break;

            case "/item":
                if (split.length == 1) {
                    ChatEditor chatEditor = new ChatEditor(mc.player.getMainHandItem());
                    chatEditor.printChatEditor();
                    return;
                }
                List<String> actionList = new ArrayList<>(
                        Arrays.asList("name", "addlore", "removelore", "editlore", "enchant", "unenchant", "nbt", "enchlist", "break", "unbreak",
                                "dur", "durability"));
                String action = split[1].toLowerCase();
                ItemStack itemStack = mc.player.getMainHandItem();

                if (!actionList.contains(action)) {
                    ModUtils.sendIncorrectArguments();
                    return;
                }
                String arg = message.indexOf(" ", 6) != -1 ? message.substring(message.indexOf(" ", 6)).trim() : "";
                String[] args = arg.split(" ");
                switch (action) {
                    case "name":
                        String oldName = itemStack.getHoverName().getString();
                        ItemEditor.renameItem(itemStack, TextUtil.replaceColorCodes(arg));
                        ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX).append(new TranslationTextComponent("mlmod.messages.itemeditor.old_name", oldName)
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
                        String nbt = itemStack.hasTag() ? itemStack.getTag().toString() : "{}";
                        ModUtils.sendMessage(new StringTextComponent(MOD_PREFIX+nbt).setStyle(TextUtil.clickToCopyStyle(nbt, false)));
                        return;
                    case "enchlist":
                        List<String> enchantments = ForgeRegistries.ENCHANTMENTS.getKeys().stream().map(ResourceLocation::getPath).collect(Collectors.toList());
                        StringTextComponent enchComp = new StringTextComponent(MOD_PREFIX);
                        boolean switchEnchColor = false;
                        for (int i = 0; i < enchantments.size(); i++) {
                            String ench = enchantments.get(i);
                            String color = switchEnchColor ? "§f" : "§7";
                            enchComp.append(new StringTextComponent(color+ench+" ("+
                                    ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(ench)).getFullname(1).getString()+color+")")
                                    .setStyle(TextUtil.newStyle()
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item enchant "+ench+" "))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.itemeditor.click_to_enchant")))));
                            if (i < enchantments.size()-1) {enchComp.append(", ");}
                            switchEnchColor = !switchEnchColor;
                        }
                        ModUtils.sendMessage(enchComp);
                        return;
                    case "enchant":
                        try {
                            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation((args[0].toLowerCase())));
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
                                Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation((args[0].toLowerCase())));
                                if (ench == null) {
                                    ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.itemeditor.no_ench_on_item"));
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
                            itemStack.setDamageValue(Integer.parseInt(args[0]));
                        } catch (Exception ignore) {
                            ModUtils.sendIncorrectArguments();
                            return;
                        }
                        break;
                }
                ModUtils.sendBarSuccess();
                ItemUtil.updateInventory(36+mc.player.inventory.selected, itemStack);
                break;

            case "/vars":
                List<Variable> vars = varCollector.readVariables();
                StringTextComponent varComponent = new StringTextComponent(MOD_PREFIX);
                varComponent.append(new TranslationTextComponent("mlmod.messages.vars.var_list", vars.size()));
                varComponent.append("\n");
                for (Variable variable:vars) {
                    String stringVar = variable.getType()+"::"+variable.getName();
                    varComponent.append(new StringTextComponent("§c- §7")
                                    .setStyle(TextUtil.newStyle()
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/removevar "+stringVar))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.vars.click_to_remove")))))
                            .append(new TranslationTextComponent(variable.getType().getTranslation()).append("§7: "+variable.getName())
                                    .setStyle(TextUtil.newStyle()
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/getvar "+stringVar))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("mlmod.messages.vars.click_to_get")))));
                    varComponent.append("\n");
                }
                varComponent.append(new TranslationTextComponent("mlmod.messages.vars.info")
                        .setStyle(TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/varsave"))));
                ModUtils.sendMessage(varComponent);
                break;

            case "/varsave":
                Variable variable = Variable.fromItem(mc.player.getMainHandItem());
                if (variable == null) {
                    ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.vars.var_not_saved"));
                    return;
                }
                varCollector.addVariable(variable);
                ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.vars.var_saved", variable.getType().name()));
                break;

            case "/removevar":
                event.setCanceled(true);
                Variable parsedVar = Variable.fromString(message.replaceFirst("/removevar ", ""));
                if (parsedVar != null && varCollector.removeVariable(parsedVar)) {
                    ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.vars.var_removed", parsedVar.getName()));
                    return;
                }
                ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.vars.var_not_removed"));
                break;

            case "/getvar":
                event.setCanceled(true);
                if (!mc.player.isCreative()) {
                    ModUtils.sendCreativeModeNeeded();
                    return;
                }
                Variable parsedVar2 = Variable.fromString(message.replaceFirst("/getvar ", ""));
                if (parsedVar2 != null) {
                    mc.player.addItem(Variable.itemFromVariable(parsedVar2));
                    // still finding a way to update inventory!!!
                }
                break;

            case "/mlmodtogglemsgcollector":
                event.setCanceled(true);
                Config.MESSAGE_COLLECTOR.set(!Config.MESSAGE_COLLECTOR.get());
                Config.spec.save();
                ModUtils.sendMessage(new TranslationTextComponent("mlmod.success"));
                break;
            case "/mlmodshowmessageads":
                event.setCanceled(true);
                if (split.length < 2) return;
                StringTextComponent component = new StringTextComponent(MOD_PREFIX);
                component.append(new TranslationTextComponent("mlmod.messages.world_list"));
                component.append("\n");
                for (String c:message.replaceFirst("/mlmodshowmessageads ", "").split(",")) {
                    StringTextComponent ad = new StringTextComponent("§8- §7"+c);
                    ad.append("\n");
                    ad.setStyle(TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, c)));
                    component.append(ad);
                }
                ModUtils.sendMessage(component);
                break;
            default:
                break;
        }
    }
}
