package ikeyler.mlmod;

import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.SoundUtil;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.LocalDateTime;

public class EventListener {
    private final Minecraft mc = Minecraft.getMinecraft();
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (Configuration.CREATIVE.PLAY_SOUND.get() && event.getEntityPlayer().getName().equals(mc.player.getName())) {
            if (mc.player.isSneaking() && event.getItemStack().isItemEqual(new ItemStack(Item.getItemById(340)))) {
                SoundUtil.playSound(TextUtil.removeColors(event.getItemStack().getDisplayName()).trim(), 1, 1);
            }
        }
    }
    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        ModUtils.disableNightDevMode();
        ModUtils.LATEST_WORLD_JOIN = LocalDateTime.now();
    }
}


