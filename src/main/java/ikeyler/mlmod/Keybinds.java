package ikeyler.mlmod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keybinds {
    public static final KeyBinding hub = new KeyBinding("/hub", 0, "MLMod");
    public static final KeyBinding play = new KeyBinding("/play", 0, "MLMod");
    public static final KeyBinding build = new KeyBinding("/build", 0, "MLMod");
    public static final KeyBinding dev = new KeyBinding("/dev", 0, "MLMod");

    static final KeyBinding[] list = new KeyBinding[]{hub, play, build, dev};
    public static void register() {
        for (KeyBinding bind:list) {
            ClientRegistry.registerKeyBinding(bind);
        }
    }
}