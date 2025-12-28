package ikeyler.mlmod.util;

import ikeyler.mlmod.itemeditor.ItemEditor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtil {
    public static ItemStack getPlayerHead(String playerName) {
        ItemStack head = new ItemStack(Items.SKULL, 1, 3);
        head.setTagCompound(new NBTTagCompound());
        head.getTagCompound().setTag("SkullOwner", new NBTTagString(playerName));
        head.getOrCreateSubCompound("display").setString("Name", playerName);
        return head;
    }
    public static ItemStack getDynamicVar(boolean saved) {
        ItemStack item = Items.MAGMA_CREAM.getDefaultInstance();
        if (saved) {
            NBTTagCompound display = item.getOrCreateSubCompound("display");
            display.setString("LocName", "save");
            List<String> lore = new ArrayList<>(Arrays.asList(
                    new TextComponentTranslation("mlmod.var_saved").getFormattedText(), " "));
            ItemEditor.setLore(item, lore);
            return item;
        }
        ItemEditor.addLore(item, "");
        return item;
    }
}
