package ikeyler.mlmod.util;

import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.*;
import java.util.stream.Collectors;

public class TextUtil {
    public static final List<String> colors = new ArrayList<>(
            Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§c", "§e", "§b", "§a", "§d", "§f", "§l", "§r", "§o", "§k", "§n", "§m", "§r")
    );
    public static final Map<Integer, String> colorMap = Collections.unmodifiableMap(
            Arrays.stream(TextFormatting.values())
                    .filter(TextFormatting::isColor)
                    .map(format -> new AbstractMap.SimpleImmutableEntry<>(format.getColor(), format.toString()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    public static String removeColors(String s) {
        return colors.stream().reduce(s, (str, color) -> str.replace(color, ""));
    }
    public static String replaceColorCodes(String s) {
        return colors.stream().reduce(s, (str, color) -> str.replace(color.replace("§", "&"), color));
    }

    public static Style clickToViewStyle(String clickText) {
        return newStyle()
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.lmb_to_view_text")
                ))
                .withClickEvent(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        clickText
                ));
    }
    public static Style clickToCopyStyle(String copyText, boolean hover) {
        return newStyle()
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        hover ? new StringTextComponent(copyText) : new TranslationTextComponent("mlmod.messages.lmb_to_copy_text")
                ))
                .withClickEvent(new ClickEvent(
                        ClickEvent.Action.COPY_TO_CLIPBOARD,
                        copyText
                ));
    }
    public static Style newStyle() {
        return new StringTextComponent("").getStyle();
    }
    public static String getFormattedText(ITextComponent component) {
        if (component == null) return "";
        StringBuilder builder = new StringBuilder();
        Style style = component.getStyle();
        if (style.getColor() != null) builder.append(colorMap.getOrDefault(style.getColor().getValue(), ""));
        if (style.isBold()) builder.append("§l");
        if (style.isItalic()) builder.append("§o");
        if (style.isUnderlined()) builder.append("§n");
        if (style.isStrikethrough()) builder.append("§m");
        if (style.isObfuscated()) builder.append("§k");
        component.getSiblings().forEach(s -> builder.append(getFormattedText(s)));
        builder.append(component.getContents());
        return builder.toString();
    }
}
