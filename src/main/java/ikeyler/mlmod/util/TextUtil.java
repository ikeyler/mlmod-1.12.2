package ikeyler.mlmod.util;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtil {
    public static final String MOD_PREFIX = "§8» §f";
    public static final List<String> colors = new ArrayList<>(
            Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§c", "§e", "§b", "§a", "§d", "§f", "§l", "§r", "§o", "§k", "§n", "§m", "§r")
    );
    public static String removeColors(String s) {
        return colors.stream().reduce(s, (str, color) -> str.replace(color, ""));
    }
    public static String replaceColors(String s) {
        return colors.stream().reduce(s, (str, color) -> str.replace(color.replace("§", "&"), color));
    }
    public static Style clickToViewStyle(String clickText) {
        return new Style()
                .setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new TextComponentTranslation("mlmod.messages.edit.lmb_to_view_text")
                ))
                .setClickEvent(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        clickText
                ));
    }
    public static Style newStyle() {
        return new TextComponentString("").getStyle();
    }
    public static void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }
}
