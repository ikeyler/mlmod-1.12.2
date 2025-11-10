package ikeyler.mlmod.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public class ItemUtil {
    public static ItemStack getPlayerHead(String playerName) {
        ItemStack head = new ItemStack(Items.SKULL, 1, 3);
        head.setTagCompound(new NBTTagCompound());
        head.getTagCompound().setTag("SkullOwner", new NBTTagString(playerName));
        head.getOrCreateSubCompound("display").setString("Name", playerName);
        return head;
    }
}
