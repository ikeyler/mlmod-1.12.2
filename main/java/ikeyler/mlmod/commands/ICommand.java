package ikeyler.mlmod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

import static ikeyler.mlmod.util.ModUtils.MOD_PREFIX;

public interface ICommand {
    String getName();
    void execute(List<String> args);
    boolean isEnabled();
    void setEnabled(boolean enabled);
    Minecraft mc = Minecraft.getMinecraft();
    default void sendPrefixMessage(ITextComponent component) {
        mc.player.sendMessage(new TextComponentString(MOD_PREFIX).appendSibling(component));
    }
    default void sendSuccess() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.success"));
    }
    default void sendBarSuccess() {
        mc.ingameGUI.setOverlayMessage(new TextComponentTranslation("mlmod.success"), false);
    }
    default void sendIncorrectArguments() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.incorrect_arguments"));
    }
    default void sendCommandError() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.command_error"));
    }
    default void sendCreativeModeNeeded() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.creative_mode_needed"));
    }
    default TextComponentTranslation translate(String translation, Object... args) {
        return new TextComponentTranslation(translation, args);
    }
}
