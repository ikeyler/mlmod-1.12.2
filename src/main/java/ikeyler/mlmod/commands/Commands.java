package ikeyler.mlmod.commands;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.Reference;
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

import java.util.List;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.*;
import static ikeyler.mlmod.util.ModUtils.*;

public class Commands {
    public static class ConfigCommand extends Command {
        public ConfigCommand() {
            super("mlc");
        }
        @Override
        public void execute(List<String> args) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mc.addScheduledTask(ModUtils::openConfigGui);
                }
            }, 100);
        }
    }
    public static class MessagesCommand extends Command {
        public MessagesCommand() {
            super("msgs");
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) {
                int totalMessages = ModUtils.readAllLines(messageCollector.dataFile).size();
                Style fileStyle = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "mlmodData.txt"));
                TextComponentString component = new TextComponentString("");
                component.appendSibling(translate("mlmod.messages.collector.total", totalMessages)).appendText("\n");
                component.appendSibling(translate("mlmod.messages.collector.search_guide")).appendText("\n");
                component.appendSibling(translate("mlmod.messages.collector.info").setStyle(fileStyle)).appendText("\n");
                String state = Configuration.GENERAL.MESSAGE_COLLECTOR.get() ? "enabled" : "disabled";
                component.appendSibling(translate("mlmod.messages.collector.state_"+state).setStyle(
                        TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodtogglemsgcollector"))));
                sendPrefixMessage(component);
                return;
            }
            if (args.size() > 1 && args.get(0).equalsIgnoreCase("find")) {
                String query = String.join(" ", args.subList(1, args.size()));
                MessageType type = Arrays.stream(MessageType.values())
                        .filter(t -> t.getName().equalsIgnoreCase(args.get(args.size()-1)))
                        .findFirst()
                        .orElse(null);
                if (type != null)
                    query = query.split(" ").length == 1 ? null : query.substring(0, query.lastIndexOf(" ")).trim();
                messageCollector.findAsync(query, type, 100);
            }
            else ModUtils.sendIncorrectArguments();
        }
    }
    public static class IgnoreListCommand extends Command {
        public IgnoreListCommand() {
            super("ignorelist");
        }
        @Override
        public void execute(List<String> args) {
            List<String> ignoredPlayers = Arrays.asList(Configuration.GENERAL.IGNORED_PLAYERS);
            ITextComponent ignoreComponent = new TextComponentString("");
            ignoreComponent.appendSibling(translate("mlmod.messages.ignorelist.ignore_list", ignoredPlayers.size()));
            ignoreComponent.appendText("\n");
            for (String player : ignoredPlayers) {
                ignoreComponent.appendText("§8- §7");
                Style style = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlignore "+player));
                style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.ignorelist.click_to_remove")));
                ITextComponent playerComp = new TextComponentString(player).setStyle(style);
                ignoreComponent.appendSibling(playerComp);
                ignoreComponent.appendText("\n");
            }
            ignoreComponent.appendSibling(translate("mlmod.messages.ignorelist.info")
                    .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mlignore "))));
            sendPrefixMessage(ignoreComponent);
        }
    }
    public static class SoundCommand extends Command {
        public SoundCommand() {
            super("sound");
            this.setEnabled(Configuration.CREATIVE.SOUND_COMMAND.get());
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) {
                TextComponentTranslation usage = translate("mlmod.messages.sound.usage");
                usage.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.sound.usage_info"))));
                usage.appendText("\n").appendSibling(translate("mlmod.messages.sound.search_guide"));
                sendPrefixMessage(usage);
                return;
            }
            else if (!args.get(0).equalsIgnoreCase("find")) {
                String sound = args.get(0).toLowerCase();
                if (!SoundUtil.getSoundIds().contains(sound)) {
                    sendPrefixMessage(translate("mlmod.messages.sound.sounds_not_found"));
                    return;
                }
                float pitch = 1;
                float volume = 1;
                try {
                    pitch = args.size() > 1 ? Float.parseFloat(args.get(1)) : pitch;
                    volume = args.size() > 2 ? Float.parseFloat(args.get(2)) : volume;
                } catch (Exception ignore) {
                    ModUtils.sendIncorrectArguments(); return;
                }
                mc.getSoundHandler().stopSounds();
                mc.ingameGUI.setOverlayMessage(translate("mlmod.messages.sound.playing_sound", sound), true);
                SoundUtil.playSound(sound, volume, pitch);
                return;
            }
            String query = String.join(" ", args.subList(1, args.size()));
            List<String> sounds = SoundUtil.findSoundIds(query);
            if (sounds.isEmpty()) {
                sendPrefixMessage(translate("mlmod.messages.sound.sounds_not_found")); return;
            }
            ITextComponent soundComponent = translate("mlmod.messages.sound.sounds_found", sounds.size());
            soundComponent.appendText("\n");
            boolean switchColor = false;
            for (int i = 0; i < sounds.size(); i++) {
                String sound = sounds.get(i);
                String color = switchColor ? "§f" : "§7";
                soundComponent.appendSibling(new TextComponentString(color+sound)
                        .setStyle(TextUtil.newStyle()
                                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sound "+sound))
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.sound.click_to_play_sound")))));
                if (i < sounds.size()-1) soundComponent.appendText(", ");
                switchColor = !switchColor;
            }
            sendPrefixMessage(soundComponent);
        }
    }
    public static class IgnoreCommand extends Command {
        public IgnoreCommand() {
            super("mlignore");
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) {
                ModUtils.sendIncorrectArguments();
                return;
            }
            String player = args.get(0).toLowerCase();
            List<String> players = Arrays.stream(Configuration.GENERAL.IGNORED_PLAYERS).map(String::toLowerCase).collect(Collectors.toList());
            boolean containsPlayer = players.contains(player);
            String ignoreAction = (containsPlayer ? "/ignore remove " : "/ignore add ") + player + " ";
            String ignoreMessage = containsPlayer ? "mlmod.messages.ignore.player_removed" : "mlmod.messages.ignore.player_added";
            Style ignoreStyle = TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ignoreAction));
            if (!containsPlayer) players.add(player);
            else players.remove(player);
            Configuration.GENERAL.IGNORED_PLAYERS = players.toArray(new String[0]);
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            messageManager.update();
            sendPrefixMessage(translate(ignoreMessage, player).setStyle(ignoreStyle));
        }
    }
    public static class HeadCommand extends Command {
        public HeadCommand() {
            super("head");
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) {
                sendPrefixMessage(translate("mlmod.messages.head.usage"));
                return;
            }
            if (!mc.player.isCreative()) {
                ModUtils.sendCreativeModeNeeded();
                return;
            }
            try {
                String headName = args.get(0).toLowerCase();
                int slotId = mc.player.inventory.getFirstEmptyStack();
                ItemStack head = ItemUtil.getPlayerHead(headName);
                ItemUtil.updateSlot(head, slotId);
                sendPrefixMessage(translate("mlmod.messages.head.head_given", "§7"+headName));
            } catch (Exception e) {
                ModUtils.sendCommandError();
                Main.logger.error(e);
            }
        }
    }
    public static class NightModeCommand extends Command {
        public NightModeCommand() {
            super("nightmode");
        }
        @Override
        public void execute(List<String> args) {
            ModUtils.nightModeCommand();
        }
    }
    public static class ItemCommand extends Command {
        List<String> actionList = Arrays.asList(
                "name", "addlore", "removelore", "editlore", "enchant", "unenchant",
                "nbt", "enchlist", "break", "unbreak", "dur", "durability");
        public ItemCommand() {
            super("item");
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) {
                new ChatEditor(mc.player.getHeldItemMainhand()).printChatEditor();
                return;
            }
            String action = args.get(0).toLowerCase();
            if (!actionList.contains(action)) {
                ModUtils.sendIncorrectArguments();
                return;
            }
            ItemStack itemStack = mc.player.getHeldItemMainhand();
            String subArg = args.size() > 1 ? String.join(" ", args.subList(1, args.size())) : "";
            List<String> subArgs = Arrays.asList(subArg.split(" "));
            switch (action) {
                case "name":
                    String oldName = itemStack.getDisplayName();
                    ItemEditor.renameItem(itemStack, TextUtil.replaceColorCodes(subArg));
                    sendPrefixMessage(translate("mlmod.messages.itemeditor.old_name", oldName)
                            .setStyle(TextUtil.clickToViewStyle(oldName.replace("§", "&"))));
                    break;
                case "addlore":
                    ItemEditor.addLore(itemStack, TextUtil.replaceColorCodes(subArg));
                    break;
                case "editlore":
                    try {
                        int loreIndex = Integer.parseInt(subArgs.get(0));
                        ItemEditor.editLore(itemStack, loreIndex, TextUtil.replaceColorCodes(subArg.substring(subArg.indexOf(" ")+1)));
                    } catch (Exception ignore) {
                        ModUtils.sendIncorrectArguments();
                        return;
                    }
                    break;
                case "removelore":
                    if (subArgs.isEmpty()) {
                        ItemEditor.clearLore(itemStack);
                    }
                    else {
                        try {
                            int loreIndex = Integer.parseInt(subArgs.get(0));
                            ItemEditor.removeLore(itemStack, loreIndex);
                        } catch (Exception ignore) {
                            ModUtils.sendCommandError();
                            return;
                        }
                    }
                    break;
                case "nbt":
                    String nbt = itemStack.hasTagCompound() ? itemStack.getTagCompound().toString() : "{}";
                    sendPrefixMessage(new TextComponentString(nbt).setStyle(TextUtil.clickToCopyStyle(nbt, "text", false)));
                    return;
                case "enchlist":
                    List<String> enchantments = Enchantment.REGISTRY.getKeys().stream().map(ResourceLocation::getResourcePath).collect(Collectors.toList());
                    ITextComponent enchComp = new TextComponentString("");
                    boolean switchEnchColor = false;
                    for (int i = 0; i < enchantments.size(); i++) {
                        String ench = enchantments.get(i);
                        String color = switchEnchColor ? "§f" : "§7";
                        enchComp.appendSibling(new TextComponentString(color+ench+" ("+Enchantment.getEnchantmentByLocation(ench).getTranslatedName(1)+color+")")
                                .setStyle(TextUtil.newStyle()
                                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item enchant "+ench+" "))
                                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.itemeditor.click_to_enchant")))));
                        if (i < enchantments.size()-1) {enchComp.appendText(", ");}
                        switchEnchColor = !switchEnchColor;
                    }
                    sendPrefixMessage(enchComp);
                    return;
                case "enchant":
                    try {
                        Enchantment ench = Enchantment.getEnchantmentByLocation(subArgs.get(0).toLowerCase());
                        int level = Integer.parseInt(subArgs.get(1));
                        ItemEditor.addEnchantment(itemStack, ench, level);
                    } catch (Exception ignore) {
                        ModUtils.sendIncorrectArguments();
                        return;
                    }
                    break;
                case "unenchant":
                    if (subArgs.isEmpty()) {
                        ItemEditor.removeEnchantment(itemStack, null);
                    }
                    else {
                        try {
                            Enchantment ench = Enchantment.getEnchantmentByLocation(subArgs.get(0).toLowerCase());
                            if (ench == null) {
                                sendPrefixMessage(translate("mlmod.messages.itemeditor.no_ench_on_item"));
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
                        itemStack.setItemDamage(Integer.parseInt(subArgs.get(0)));
                    } catch (Exception ignore) {
                        ModUtils.sendIncorrectArguments();
                        return;
                    }
                    break;
            }
            mc.playerController.sendSlotPacket(itemStack, 36+mc.player.inventory.currentItem);
            ModUtils.sendBarSuccess();
        }
    }
    public static class VarsCommand extends Command {
        public VarsCommand() {
            super("vars");
        }
        @Override
        public void execute(List<String> args) {
            List<Variable> vars = varCollector.readVariables();
            ITextComponent varComponent = new TextComponentString("");
            varComponent.appendSibling(translate("mlmod.messages.vars.var_list", vars.size())).appendText("\n");
            for (Variable variable : vars) {
                String stringVar = variable.getType()+VAR_SEPARATOR+variable.getName()+VAR_SEPARATOR+variable.getNbt();
                varComponent.appendSibling(new TextComponentString("§c- §7")
                        .setStyle(TextUtil.newStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodremovevar "+stringVar))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.vars.click_to_remove")))));
                varComponent.appendSibling(translate("mlmod.var."+variable.getType().name().toLowerCase())
                        .appendText("§7: "+variable.getFixedName())
                        .setStyle(TextUtil.newStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodgetvar "+stringVar))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.vars.click_to_get")))));
                varComponent.appendText("\n");
            }
            varComponent.appendSibling(translate("mlmod.messages.vars.info")
                    .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/varsave"))));
            sendPrefixMessage(varComponent);
        }
    }
    public static class VarSaveCommand extends Command {
        public VarSaveCommand() {
            super("varsave");
        }
        @Override
        public void execute(List<String> args) {
            Variable variable = Variable.fromItem(mc.player.getHeldItemMainhand());
            if (variable == null) {
                sendPrefixMessage(translate("mlmod.messages.vars.var_not_saved"));
                return;
            }
            varCollector.addVariable(variable);
            sendPrefixMessage(translate("mlmod.messages.vars.var_saved", variable.getType().name()));
        }
    }
    public static class ReloadMessagesCommand extends Command {
        public ReloadMessagesCommand() {
            super("rlmsg");
        }
        @Override
        public void execute(List<String> args) {
            int loaded = Messages.reloadMessages();
            sendPrefixMessage(new TextComponentTranslation("mlmod.messages.reloaded_messages", "§7"+loaded));
        }
    }
    public static class HelpCommand extends Command {
        public HelpCommand() {
            super("help");
        }
        @Override
        public void execute(List<String> args) {
            int helpLines = 11;
            ITextComponent help = new TextComponentString("");
            for (int i = 1; i < helpLines; i++)
                help.appendSibling(new TextComponentTranslation("mlmod.messages.help." + i)).appendText("\n");
            mc.player.sendMessage(help);
        }
    }
    public static class VarCommand extends Command {
        public VarCommand() {
            super("var");
        }
        @Override
        public void execute(List<String> args) {
            if (!mc.player.isCreative()) {
                ModUtils.sendCreativeModeNeeded(); return;
            }
            ItemStack item = ItemUtil.getDynamicVar(false);
            String name = !args.isEmpty() ? String.join(" ", args) : "";
            item.setStackDisplayName(TextUtil.replaceColorCodes(name));
            int slotId = mc.player.inventory.getFirstEmptyStack();
            ItemUtil.updateSlot(item, slotId);
            mc.ingameGUI.setOverlayMessage(translate("mlmod.messages.var.var_given"), false);
        }
    }
    public static class TextCommand extends Command {
        public TextCommand() {
            super("text");
        }
        @Override
        public void execute(List<String> args) {
            if (!mc.player.isCreative()) {
                ModUtils.sendCreativeModeNeeded(); return;
            }
            ItemStack item = Items.BOOK.getDefaultInstance();
            String name = !args.isEmpty() ? String.join(" ", args) : "";
            item.setStackDisplayName(TextUtil.replaceColorCodes(name));
            ItemEditor.setLore(item, Arrays.asList(translate("mlmod.var.text.desc").getFormattedText().split("\\\\n")));
            int slotId = mc.player.inventory.getFirstEmptyStack();
            ItemUtil.updateSlot(item, slotId);
            mc.ingameGUI.setOverlayMessage(translate("mlmod.messages.var.var_given"), false);
        }
    }
    public static class NumberCommand extends Command {
        public NumberCommand() {
            super("num");
        }
        @Override
        public void execute(List<String> args) {
            if (!mc.player.isCreative()) {
                ModUtils.sendCreativeModeNeeded(); return;
            }
            ItemStack item = Items.SLIME_BALL.getDefaultInstance();
            String name = !args.isEmpty() ? String.join(" ", args) : "";
            item.setStackDisplayName(TextUtil.replaceColorCodes(name));
            ItemEditor.setLore(item, Arrays.asList(translate("mlmod.var.number.desc").getFormattedText().split("\\\\n")));
            int slotId = mc.player.inventory.getFirstEmptyStack();
            ItemUtil.updateSlot(item, slotId);
            mc.ingameGUI.setOverlayMessage(translate("mlmod.messages.var.var_given"), false);
        }
    }
}
