package ikeyler.mlmod;

import ikeyler.mlmod.cfg.Config;
import ikeyler.mlmod.util.ModUtils;
import ikeyler.mlmod.util.SoundUtil;
import ikeyler.mlmod.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.LocalDateTime;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EventListener {
    private final Minecraft mc = Minecraft.getInstance();
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (Config.PLAY_SOUND.get() && mc.player.isShiftKeyDown() && event.getItemStack().getItem() == Items.BOOK) {
            SoundUtil.playSound(TextUtil.removeColors(event.getItemStack().getHoverName().getString()).trim(), 1, 1);
        }
    }
    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        ModUtils.disableNightDevMode();
        ModUtils.LATEST_WORLD_JOIN = LocalDateTime.now();
    }
}


