package ikeyler.mlmod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SoundUtil {
    private static final List<String> soundIds = new ArrayList<>();
    private static boolean init = false;
    public static List<String> getSoundIds() {
        if (!init && (init=true)) {
            ForgeRegistries.SOUND_EVENTS.getKeys().forEach(s -> soundIds.add(s.toString().toLowerCase().replaceFirst("minecraft:", "")));
        }
        return soundIds;
    }
    public static List<String> findSoundIds(String query) {
        return getSoundIds().stream().filter(s -> s.contains(query.toLowerCase())).collect(Collectors.toList());
    }
    public static void playSound(String soundId, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        try {
            mc.player.playSound(new SoundEvent(new ResourceLocation(soundId)), volume, pitch);
        } catch (Exception ignore) {}
    }
}
