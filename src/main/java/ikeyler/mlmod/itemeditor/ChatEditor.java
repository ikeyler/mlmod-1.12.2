package ikeyler.mlmod.itemeditor;

import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
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
        Minecraft mc = Minecraft.getMinecraft();
        if (item.isItemEqual(Items.AIR.getDefaultInstance())) {
            mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.itemeditor.empty_hand"));
            return;
        }
        TextComponentString editor = new TextComponentString("\n");
        String itemName = item.getDisplayName();

        Style name = TextUtil.clickToViewStyle(itemName.replace("§", "&"));
        Style rename = TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TextComponentTranslation("mlmod.messages.itemeditor.rename")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item name "));
        String breakable = ItemEditor.isUnbreakable(item) ? "mlmod.messages.itemeditor.unbreakable" : "mlmod.messages.itemeditor.breakable";

        // doing some appends (govnocode?)
        editor.appendText(MOD_PREFIX).appendSibling(new TextComponentTranslation("mlmod.messages.itemeditor.editing", "§7["+itemName+"§7]").setStyle(name));
        editor.appendText(" ").appendSibling(new TextComponentTranslation("mlmod.messages.itemeditor.button_edit").setStyle(rename));
        editor.appendText("\n").appendSibling(getLore());
        editor.appendText(" ").appendSibling(getLoreButtons()).appendText("\n");
        editor.appendSibling(getEnchantments());
        editor.appendText(" ").appendSibling(getEnchButtons());
        editor.appendText("\n").appendSibling(new TextComponentTranslation("mlmod.messages.itemeditor.view_nbt")
                .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/item nbt"))));
        editor.appendText(" ").appendSibling(new TextComponentTranslation(breakable)
                .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/item break"))));
        editor.appendText(" ").appendSibling(new TextComponentTranslation("mlmod.messages.itemeditor.editdurability")
                .setStyle(TextUtil.newStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item dur "))));

        mc.player.sendMessage(editor);
    }

    private ITextComponent getLore() {
        List<String> itemLore = ItemEditor.getLore(item);
        if (!itemLore.isEmpty()) {
            ITextComponent lore = new TextComponentString("");
            AtomicInteger count = new AtomicInteger(1);
            itemLore.forEach(s -> {
                lore.appendText("§8"+count+". ");
                lore.appendSibling(new TextComponentString(s).setStyle(TextUtil.clickToViewStyle(s.replace("§", "&"))));
                lore.appendText("\n");
                count.getAndIncrement();
            });
            return lore;
        }
        return new TextComponentTranslation("mlmod.messages.itemeditor.no_lore");
    }

    private ITextComponent getLoreButtons() {
        ITextComponent buttons = new TextComponentString("");

        ITextComponent addLore = new TextComponentTranslation("mlmod.messages.itemeditor.button_add");
        ITextComponent editLore = new TextComponentTranslation("mlmod.messages.itemeditor.button_edit");
        ITextComponent removeLore = new TextComponentTranslation("mlmod.messages.itemeditor.button_remove");
        addLore.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new TextComponentTranslation("mlmod.messages.itemeditor.addlore")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item addlore ")));
        editLore.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new TextComponentTranslation("mlmod.messages.itemeditor.editlore")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item editlore ")));
        removeLore.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TextComponentTranslation("mlmod.messages.itemeditor.removelore")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item removelore ")));

        buttons.appendSibling(editLore).appendText(" ").appendSibling(addLore).appendText(" ").appendSibling(removeLore);
        return buttons;
    }

    private ITextComponent getEnchButtons() {
        ITextComponent buttons = new TextComponentString("");

        ITextComponent addEnch = new TextComponentTranslation("mlmod.messages.itemeditor.button_add");
        ITextComponent enchInfo = new TextComponentTranslation("mlmod.messages.itemeditor.button_info");
        ITextComponent removeEnch = new TextComponentTranslation("mlmod.messages.itemeditor.button_remove");
        addEnch.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TextComponentTranslation("mlmod.messages.itemeditor.addenchant")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item enchant ")));
        enchInfo.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TextComponentTranslation("mlmod.messages.itemeditor.enchant_info")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/item enchlist")));
        removeEnch.setStyle(TextUtil.newStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new TextComponentTranslation("mlmod.messages.itemeditor.unenchant")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/item unenchant ")));

        buttons.appendSibling(addEnch).appendText(" ").appendSibling(removeEnch).appendText(" ").appendSibling(enchInfo);
        return buttons;
    }

    private ITextComponent getEnchantments() {
        Map<Enchantment, Integer> itemEnch = EnchantmentHelper.getEnchantments(item);
        if (!itemEnch.isEmpty()) {
            ITextComponent enchantments = new TextComponentString("");
            enchantments.appendText("§8- ");
            AtomicBoolean first = new AtomicBoolean(true);
            itemEnch.forEach((ench, level) -> {
                if (!first.get()) enchantments.appendText(", ");
                first.set(false);
                enchantments.appendSibling(new TextComponentString(ench.getTranslatedName(level)));
            });
            return enchantments;
        }
        return new TextComponentTranslation("mlmod.messages.itemeditor.no_ench");
    }
}
