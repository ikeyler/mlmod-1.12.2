package ikeyler.mlmod.itemeditor;

import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ikeyler.mlmod.util.ModUtils.MOD_PREFIX;


public class ChatEditor {

    private final ItemStack item;
    public ChatEditor(ItemStack item) {
        this.item = item;
    }

    public void printChatEditor() {
        if (item.equals(Items.AIR.getDefaultInstance())) {
            ModUtils.sendMessage(new TranslationTextComponent("mlmod.messages.itemeditor.empty_hand"));
            return;
        }
        StringTextComponent editor = new StringTextComponent("\n");
        String itemName = TextUtil.getFormattedText(item.getHoverName());

        Style name = TextUtil.clickToViewStyle(itemName.replace("§", "&"));
        Style rename = TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.rename")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item name "));
        String breakable = ItemEditor.isUnbreakable(item) ? "mlmod.messages.itemeditor.unbreakable" : "mlmod.messages.itemeditor.breakable";

        // doing some appends (govnocode?)
        editor.append(MOD_PREFIX).append(new TranslationTextComponent("mlmod.messages.itemeditor.editing", "§7["+itemName+"§7]").setStyle(name));
        editor.append(" ").append(new TranslationTextComponent("mlmod.messages.itemeditor.button_edit").setStyle(rename));
        editor.append("\n").append(getLore());
        editor.append(" ").append(getLoreButtons()).append("\n");
        editor.append(getEnchantments());
        editor.append(" ").append(getEnchButtons());
        editor.append("\n").append(new TranslationTextComponent("mlmod.messages.itemeditor.view_nbt")
                .setStyle(TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/item nbt"))));
        editor.append(" ").append(new TranslationTextComponent(breakable)
                .setStyle(TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/item break"))));
        editor.append(" ").append(new TranslationTextComponent("mlmod.messages.itemeditor.editdurability")
                .setStyle(TextUtil.newStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item dur "))));

        ModUtils.sendMessage(editor);
    }

    private ITextComponent getLore() {
        List<String> itemLore = ItemEditor.getLore(item);
        if (!itemLore.isEmpty()) {
            StringTextComponent lore = new StringTextComponent("");
            AtomicInteger count = new AtomicInteger(1);
            itemLore.forEach(s -> {
                lore.append("§8"+count+". ");
                lore.append(new StringTextComponent(s).setStyle(TextUtil.clickToViewStyle(s.replace("§", "&"))));
                lore.append("\n");
                count.getAndIncrement();
            });
            return lore;
        }
        return new TranslationTextComponent("mlmod.messages.itemeditor.no_lore");
    }

    private ITextComponent getLoreButtons() {
        ITextComponent buttons = new StringTextComponent("");

        TranslationTextComponent addLore = new TranslationTextComponent("mlmod.messages.itemeditor.button_add");
        TranslationTextComponent editLore = new TranslationTextComponent("mlmod.messages.itemeditor.button_edit");
        TranslationTextComponent removeLore = new TranslationTextComponent("mlmod.messages.itemeditor.button_remove");
        addLore.setStyle(TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.addlore")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item addlore ")));
        editLore.setStyle(TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.editlore")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item editlore ")));
        removeLore.setStyle(TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.removelore")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item removelore ")));

        return buttons.copy().append(editLore).append(" ").append(addLore).append(" ").append(removeLore);
    }

    private ITextComponent getEnchButtons() {
        ITextComponent buttons = new StringTextComponent("");

        TranslationTextComponent addEnch = new TranslationTextComponent("mlmod.messages.itemeditor.button_add");
        TranslationTextComponent enchInfo = new TranslationTextComponent("mlmod.messages.itemeditor.button_info");
        TranslationTextComponent removeEnch = new TranslationTextComponent("mlmod.messages.itemeditor.button_remove");
        addEnch.setStyle(TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.addenchant")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item enchant ")));
        enchInfo.setStyle(TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.enchant_info")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/item enchlist")));
        removeEnch.setStyle(TextUtil.newStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TranslationTextComponent("mlmod.messages.itemeditor.unenchant")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item unenchant ")));

        return buttons.copy().append(addEnch).append(" ").append(removeEnch).append(" ").append(enchInfo);
    }

    private ITextComponent getEnchantments() {
        Map<Enchantment, Integer> itemEnch = EnchantmentHelper.getEnchantments(item);
        if (!itemEnch.isEmpty()) {
            StringTextComponent enchantments = new StringTextComponent("");
            enchantments.append("§8- ");
            AtomicBoolean first = new AtomicBoolean(true);
            itemEnch.forEach((ench, level) -> {
                if (!first.get()) enchantments.append(", ");
                first.set(false);
                enchantments.append(ench.getFullname(level));
            });
            return enchantments;
        }
        return new TranslationTextComponent("mlmod.messages.itemeditor.no_ench");
    }
}
