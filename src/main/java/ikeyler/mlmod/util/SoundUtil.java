package ikeyler.mlmod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SoundUtil {
    private static final List<String> soundIds = new ArrayList<>();
    private static boolean init = false;
    public static List<String> getSoundIds() {
        if (!init && (init=true)) {
            SoundEvent.REGISTRY.getKeys().forEach(s -> soundIds.add(s.toString().toLowerCase().replaceFirst("minecraft:", "")));
        }
        return soundIds;
    }
    public static List<String> findSoundIds(String query) {
        return getSoundIds().stream().filter(s -> s.contains(query.toLowerCase())).collect(Collectors.toList());
    }
    public static void playSound(String soundId, float volume, float pitch) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.player.playSound(new SoundEvent(new ResourceLocation(soundId)), volume, pitch);
    }
}
