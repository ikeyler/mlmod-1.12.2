package ikeyler.mlmod.util;

import ikeyler.mlmod.cfg.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.time.LocalDateTime;

public class ModUtils {
    private static final Minecraft mc = Minecraft.getInstance();
    public static final String MOD_PREFIX = "§8» §f";
    public static final String NOTIFICATION_SOUND = "entity.experience_orb.pickup";
    public static boolean NIGHT_DEV_MODE = false;
    public static final double GAME_GAMMA_SETTING = mc.options.gamma;
    public static LocalDateTime LATEST_WORLD_JOIN = LocalDateTime.now();

    public static void enableNightDevMode() {
        if (Config.DEV_NIGHT_MODE.get() && mc.player.isCreative()) {
            NIGHT_DEV_MODE = true;
            mc.options.gamma = 1000.0F;
        }
    }
    public static void disableNightDevMode() {
        if (NIGHT_DEV_MODE && Config.DEV_NIGHT_MODE.get()) {
            mc.options.gamma = GAME_GAMMA_SETTING;
        }
        NIGHT_DEV_MODE = false;
    }

    public static void nightModeCommand() {
        if (!mc.player.isCreative()) {
            mc.player.sendMessage(new TranslationTextComponent("mlmod.messages.creative_mode_needed"), null);
            return;
        }
        if (!Config.DEV_NIGHT_MODE.get()) {
            mc.player.sendMessage(new TranslationTextComponent("mlmod.messages.nightmode.enable_in_config"), null);
            return;
        }
        enableNightDevMode();
        mc.player.sendMessage(new TranslationTextComponent("mlmod.messages.nightmode.enabled"), null);
    }
    public static void sendSuccess() {
        mc.player.sendMessage(new TranslationTextComponent("mlmod.success"), null);
    }
    public static void sendBarSuccess() {
        mc.gui.setOverlayMessage(new TranslationTextComponent("mlmod.success"), false);
    }
    public static void sendIncorrectArguments() {
        mc.player.sendMessage(new TranslationTextComponent("mlmod.incorrect_arguments"), null);
    }
    public static void sendCommandError() {
        mc.player.sendMessage(new TranslationTextComponent("mlmod.command_error"), null);
    }
    public static void sendCreativeModeNeeded() {
        mc.player.sendMessage(new TranslationTextComponent("mlmod.messages.creative_mode_needed"), null);
    }
    public static void sendMessage(ITextComponent component) {
        mc.player.sendMessage(component, null);
    }
}
