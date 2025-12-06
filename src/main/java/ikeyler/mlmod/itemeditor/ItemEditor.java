package ikeyler.mlmod.itemeditor;

import ikeyler.mlmod.util.TextUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEditor {
    // screw viaversion!
    private static final String vvLore = "VV|Protocol1_13_2To1_14|Lore";

    public static List<String> getLore(ItemStack stack) {
        List<String> lore = new ArrayList<>();
        if (stack.hasTag() && stack.getTag().contains("display")) {
            CompoundNBT display = stack.getTag().getCompound("display");
            if (display.contains("Lore")) {
                ListNBT tag = display.getList("Lore", 8);
                for (int i = 0; i < tag.size(); i++) {
                    lore.add(TextUtil.getFormattedText(ITextComponent.Serializer.fromJson(tag.getString(i))));
                }
            }
        }
        return lore;
    }
    public static void setLore(ItemStack stack, List<String> lore) {
        CompoundNBT display = stack.getOrCreateTagElement("display");
        display.remove(vvLore);
        ListNBT tag = new ListNBT();
        lore.forEach(s -> tag.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(s)))));
        display.put("Lore", tag);
    }
    public static void editLore(ItemStack stack, int index, String text) {
        List<String> lore = getLore(stack);
        if (index >= lore.size()) {
            addLore(stack, text);
            return;
        }
        lore.set(index, text);
        setLore(stack, lore);
    }
    public static void addLore(ItemStack stack, String text) {
        CompoundNBT display = stack.getOrCreateTagElement("display");
        display.remove(vvLore);
        ListNBT tag = display.getList("Lore", 8);
        String json = ITextComponent.Serializer.toJson(new StringTextComponent(text));
        tag.add(StringNBT.valueOf(json));
        display.put("Lore", tag);
    }
    public static void removeLore(ItemStack stack, int index) {
        List<String> lore = getLore(stack);
        lore.remove(index);
        setLore(stack, lore);
    }
    public static void clearLore(ItemStack stack) {
        setLore(stack, new ArrayList<>());
    }
    public static void renameItem(ItemStack stack, String name) {
        stack.setHoverName(new StringTextComponent(name));
    }
    public static void addEnchantment(ItemStack stack, Enchantment ench, int level) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(ench, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }
    public static void removeEnchantment(ItemStack stack, Enchantment ench) {
        if (ench == null) {
            EnchantmentHelper.setEnchantments(new HashMap<>(), stack);
            return;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.containsKey(ench)) {
            enchantments.remove(ench);
            EnchantmentHelper.setEnchantments(enchantments, stack);
        }
    }
    public static void setUnbreakable(ItemStack stack, boolean state) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        tag.putBoolean("Unbreakable", state);
        stack.setTag(tag);
    }
    public static boolean isUnbreakable(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) return false;
        return tag.contains("Unbreakable") && tag.getBoolean("Unbreakable");
    }
}
