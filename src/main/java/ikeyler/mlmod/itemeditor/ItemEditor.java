package ikeyler.mlmod.itemeditor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEditor {
    public static List<String> getLore(ItemStack stack) {
        List<String> lore = new ArrayList<>();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
            if (display.hasKey("Lore", 9)) {
                NBTTagList tag = display.getTagList("Lore", 8);
                for (int i = 0; i < tag.tagCount(); i++) {
                    lore.add(tag.getStringTagAt(i));
                }
            }
        }
        return lore;
    }
    public static void setLore(ItemStack stack, List<String> lore) {
        NBTTagCompound display = stack.getOrCreateSubCompound("display");
        NBTTagList tag = new NBTTagList();
        lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
        display.setTag("Lore", tag);
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
        NBTTagCompound display = stack.getOrCreateSubCompound("display");
        NBTTagList tag = display.getTagList("Lore", 8);
        tag.appendTag(new NBTTagString(text));
        display.setTag("Lore", tag);
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
        stack.setStackDisplayName(name);
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
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setBoolean("Unbreakable", state);
        stack.setTagCompound(tag);
    }
    public static boolean isUnbreakable(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return false;
        return tag.hasKey("Unbreakable") && tag.getBoolean("Unbreakable");
    }
}
