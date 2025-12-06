package ikeyler.mlmod.util;

import com.mojang.authlib.GameProfile;
import ikeyler.mlmod.itemeditor.ItemEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    static Minecraft mc = Minecraft.getInstance();
    public static void updateInventory(int slot, ItemStack item) {
        if (mc.hasSingleplayerServer()) {
            mc.getSingleplayerServer().getPlayerList().getPlayerByName(mc.player.getName().getString())
                    .inventoryMenu.setItem(slot, item);
        }
        else {
            try {mc.getConnection().send(new CCreativeInventoryActionPacket(slot, item));} catch (Exception ignore) {}
        }
    }
    public static ItemStack getPlayerHead(String playerName) {
        // todo need to get player uuid from https://api.mojang.com/users/profiles/minecraft/
        // and write it to the itemstack
        ItemStack head = new ItemStack(Items.PLAYER_HEAD, 1);
        CompoundNBT tag = head.getOrCreateTag();
        head.setHoverName(new StringTextComponent(playerName));
        GameProfile profile = new GameProfile(null, playerName);
        mc.getMinecraftSessionService().fillProfileProperties(profile, true);
        tag.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), profile));
        return head;
    }
    public static ItemStack getDynamicVar(boolean saved) {
        ItemStack item = new ItemStack(Items.MAGMA_CREAM);
        if (saved) {
            CompoundNBT display = item.getOrCreateTagElement("display");
            display.putString("LocName", "save");
            List<String> lore = new ArrayList<>();
            lore.add(new TranslationTextComponent("mlmod.var_saved").getString());
            lore.add(" ");
            ItemEditor.setLore(item, lore);
            return item;
        }
        ItemEditor.addLore(item, "");
        return item;
    }
}
