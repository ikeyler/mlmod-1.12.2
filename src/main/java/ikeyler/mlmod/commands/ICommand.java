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
    default TextComponentTranslation translate(String translation, Object... args) {
        return new TextComponentTranslation(translation, args);
    }
}
