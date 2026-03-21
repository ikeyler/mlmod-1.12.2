package ikeyler.mlmod;

import ikeyler.mlmod.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.HashMap;
import java.util.Map;

public class Keybinds {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Map<KeyBinding, Runnable> bindActions = new HashMap<>();

    public static final KeyBinding hub = new KeyBinding("/hub", 0, "MLMod");
    public static final KeyBinding play = new KeyBinding("/play", 0, "MLMod");
    public static final KeyBinding build = new KeyBinding("/build", 0, "MLMod");
    public static final KeyBinding dev = new KeyBinding("/dev", 0, "MLMod");
    public static final KeyBinding nightmode = new KeyBinding("/nightmode", 0, "MLMod");
    public static final KeyBinding varsave = new KeyBinding("/varsave", 0, "MLMod");
    static final KeyBinding[] list = new KeyBinding[]{hub, play, build, dev, nightmode, varsave};

    public static void register() {
        for (KeyBinding bind:list) {
            ClientRegistry.registerKeyBinding(bind);
        }
        bindActions.clear();
        bindActions.put(hub, () -> mc.player.sendChatMessage("/hub"));
        bindActions.put(build, () -> mc.player.sendChatMessage("/build"));
        bindActions.put(play, () -> mc.player.sendChatMessage("/play"));
        bindActions.put(dev, () -> mc.player.sendChatMessage("/dev"));
        bindActions.put(nightmode, ModUtils::nightModeCommand);
        bindActions.put(varsave, () -> MinecraftForge.EVENT_BUS.post(new ClientChatEvent("/varsave")));
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        bindActions.entrySet().stream().filter(entry -> entry.getKey().isPressed())
                .findFirst().ifPresent(entry -> entry.getValue().run());
    }
}