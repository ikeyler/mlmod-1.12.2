package ikeyler.mlmod.commands;

import ikeyler.mlmod.Reference;
import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.ItemUtil;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.TextUtil;
import ikeyler.mlmod.variables.Variable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

import static ikeyler.mlmod.Main.varCollector;

public class UtilCommands {
    public static class PlayerInteractCommand extends UtilCommand {
        public PlayerInteractCommand() {
            super("mlmodplayerinteract");
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
    public static class RemoveVarCommand extends UtilCommand {
        public RemoveVarCommand() {
            super("mlmodremovevar");
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
    public static class GetVarCommand extends UtilCommand {
        public GetVarCommand() {
            super("mlmodgetvar");
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            if (!mc.player.isCreative()) {
                sendCreativeModeNeeded();
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
    public static class ToggleMsgCollectorCommand extends UtilCommand {
        public ToggleMsgCollectorCommand() {
            super("mlmodtogglemsgcollector");
        }
        @Override
        public void execute(List<String> args) {
            Configuration.GENERAL.MESSAGE_COLLECTOR = Configuration.Bool.fromBoolean(!Configuration.GENERAL.MESSAGE_COLLECTOR.get());
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            sendSuccess();
        }
    }
    public static class ShowMessageAdsCommand extends UtilCommand {
        public ShowMessageAdsCommand() {
            super("mlmodshowmessageads");
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
    public static class CopyTextCommand extends UtilCommand {
        public CopyTextCommand() {
            super("mlmodcopytext");
        }
        @Override
        public void execute(List<String> args) {
            if (args.isEmpty()) return;
            String text = String.join(" ", args);
            TextUtil.copyToClipboard(text);
            sendPrefixMessage(translate("mlmod.messages.text_copied", text));
        }
    }
}
