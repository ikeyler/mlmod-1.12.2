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
import net.minecraft.client.Minecraft;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static ikeyler.mlmod.Main.*;
import static ikeyler.mlmod.util.ModUtils.*;

public class Commands {
    public static final List<Command> commandList = Arrays.asList(
            new ConfigCommand(), new MessagesCommand(), new IgnoreListCommand(), new SoundCommand(),
            new IgnoreCommand(), new HeadCommand(), new NightModeCommand(), new ItemCommand(),
            new PlayerInteractCommand(), new VarsCommand(), new VarSaveCommand(), new RemoveVarCommand(),
            new GetVarCommand(), new ToggleMsgCollectorCommand(), new ShowMessageAdsCommand(), new CopyTextCommand(),
            new ReloadMessagesCommand(), new HelpCommand(), new VarCommand(), new TextCommand(),
            new NumberCommand()
    );
    static Minecraft mc = Minecraft.getMinecraft();
    static void sendPrefixMessage(ITextComponent component) {
        mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(component));
    }
    static TextComponentTranslation translate(String translation, Object... args) {
        return new TextComponentTranslation(translation, args);
    }

    public static class ConfigCommand extends Command {
        public ConfigCommand() {
            super("mlc", false);
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
            super("msgs", false);
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
            super("ignorelist", false);
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
            super("sound", false);
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
            super("mlignore", false);
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
            super("head", false);
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
            super("nightmode", false);
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
            super("item", false);
        }
        @Override
        public void execute(List<String> args) {
            // todo rewrite this govnocode
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
    public static class PlayerInteractCommand extends Command {
        public PlayerInteractCommand() {
            super("mlmodplayerinteract", true);
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            // an awful way to store params
            String[] params = String.join(" ", args).split("§§");
            String player = params[0];
            String msg = params.length > 1 ? params[1] : null;
            String chat = params.length > 2 ? params[2] : null;
            TextComponentString playerComp = new TextComponentString("§7§o"+player);
            playerComp.appendSibling(translate("mlmod.copy"));
            playerComp.setStyle(TextUtil.clickToCopyStyle(player, "name", false));
            TextComponentTranslation menu = translate("mlmod.messages.chat_player_interact", playerComp);
            menu.appendText("\n");
            List<ITextComponent> components = new ArrayList<>();
            if (msg != null && chat != null)
                components.add(translate("mlmod.messages.reply")
                        .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, chat + " "))));
            if (msg != null) {
                components.add(translate("mlmod.messages.copy_message")
                        .setStyle(TextUtil.newStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mlmodcopytext " + msg))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(msg)))));
            }
            String[][] buttons = {
                    {"report", "/report %p"}, {"block", "/mlignore %p"},
                    {"find_messages", "/msgs find %p "}, {"find_who", "/who %p"}};
            for (String[] btn : buttons) {
                ClickEvent.Action action = !btn[0].equals("find_messages") ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
                components.add(translate("mlmod.messages."+btn[0])
                        .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(action, btn[1].replace("%p", player)))));
            }
            components.forEach(component -> menu.appendSibling(component).appendText(" "));
            sendPrefixMessage(menu);
        }
    }
    public static class VarsCommand extends Command {
        public VarsCommand() {
            super("vars", false);
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
            super("varsave", false);
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
    public static class RemoveVarCommand extends Command {
        public RemoveVarCommand() {
            super("mlmodremovevar", true);
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            Variable parsedVar = Variable.fromString(String.join(" ", args));
            if (parsedVar != null && varCollector.removeVariable(parsedVar)) {
                sendPrefixMessage(translate("mlmod.messages.vars.var_removed", parsedVar.getName()));
                return;
            }
            sendPrefixMessage(translate("mlmod.messages.vars.var_not_removed"));
        }
    }
    public static class GetVarCommand extends Command {
        public GetVarCommand() {
            super("mlmodgetvar", true);
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            if (!mc.player.isCreative()) {
                ModUtils.sendCreativeModeNeeded();
                return;
            }
            Variable parsedVar = Variable.fromString(String.join(" ", args));
            ItemStack itemVar;
            if (parsedVar != null && (itemVar = Variable.itemFromVariable(parsedVar)) != null) {
                int slotId = mc.player.inventory.getFirstEmptyStack();
                ItemUtil.updateSlot(itemVar, slotId);
            }
        }
    }
    public static class ToggleMsgCollectorCommand extends Command {
        public ToggleMsgCollectorCommand() {
            super("mlmodtogglemsgcollector", true);
        }
        @Override
        public void execute(List<String> args) {
            Configuration.GENERAL.MESSAGE_COLLECTOR = Configuration.Bool.fromBoolean(!Configuration.GENERAL.MESSAGE_COLLECTOR.get());
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            ModUtils.sendSuccess();
        }
    }
    public static class ShowMessageAdsCommand extends Command {
        public ShowMessageAdsCommand() {
            super("mlmodshowmessageads", true);
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            TextComponentString adsComponent = new TextComponentString("");
            adsComponent.appendSibling(translate("mlmod.messages.world_list"));
            adsComponent.appendText("\n");
            for (String adCmd : String.join(" ", args).split(",")) {
                TextComponentString ad = new TextComponentString("§8- §7"+adCmd);
                ad.setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, adCmd))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translate("mlmod.messages.world_list.join"))));
                ad.appendSibling(translate("mlmod.copy").setStyle(TextUtil.clickToCopyStyle(adCmd, "id", false)));
                ad.appendText("\n");
                adsComponent.appendSibling(ad);
            }
            sendPrefixMessage(adsComponent);
        }
    }
    public static class CopyTextCommand extends Command {
        public CopyTextCommand() {
            super("mlmodcopytext", true);
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            String text = String.join(" ", args);
            TextUtil.copyToClipboard(text);
            sendPrefixMessage(translate("mlmod.messages.text_copied", text));
        }
    }
    public static class ReloadMessagesCommand extends Command {
        public ReloadMessagesCommand() {
            super("rlmsg", false);
        }
        @Override
        public void execute(List<String> args) {
            int loaded = Messages.reloadMessages();
            sendPrefixMessage(new TextComponentTranslation("mlmod.messages.reloaded_messages", "§7"+loaded));
        }
    }
    public static class HelpCommand extends Command {
        public HelpCommand() {
            super("help", false);
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
            super("var", false);
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
            super("text", false);
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
            super("num", false);
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
