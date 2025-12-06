package ikeyler.mlmod;

import ikeyler.mlmod.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Keybinds {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<KeyBinding, Runnable> bindActions = new HashMap<>();

    public static final KeyBinding hub = new KeyBinding("/hub", InputMappings.UNKNOWN.getValue(), "MLMod");
    public static final KeyBinding play = new KeyBinding("/play", InputMappings.UNKNOWN.getValue(), "MLMod");
    public static final KeyBinding build = new KeyBinding("/build", InputMappings.UNKNOWN.getValue(), "MLMod");
    public static final KeyBinding dev = new KeyBinding("/dev", InputMappings.UNKNOWN.getValue(), "MLMod");
    public static final KeyBinding nightmode = new KeyBinding("/nightmode", InputMappings.UNKNOWN.getValue(), "MLMod");
    public static final KeyBinding varsave = new KeyBinding("/varsave", InputMappings.UNKNOWN.getValue(), "MLMod");
    static final KeyBinding[] list = new KeyBinding[]{hub, play, build, dev, nightmode, varsave};

    public static void register() {
        for (KeyBinding bind:list) {
            ClientRegistry.registerKeyBinding(bind);
        }
        bindActions.clear();
        bindActions.put(hub, () -> mc.player.chat("/hub"));
        bindActions.put(build, () -> mc.player.chat("/build"));
        bindActions.put(play, () -> mc.player.chat("/play"));
        bindActions.put(dev, () -> mc.player.chat("/dev"));
        bindActions.put(nightmode, ModUtils::nightModeCommand);
        bindActions.put(varsave, () -> MinecraftForge.EVENT_BUS.post(new ClientChatEvent("/varsave")));
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        bindActions.entrySet().stream().filter(entry -> entry.getKey().isDown())
                .findFirst().ifPresent(entry -> entry.getValue().run());
    }
}
