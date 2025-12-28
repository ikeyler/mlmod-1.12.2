package ikeyler.mlmod.util;

import ikeyler.mlmod.Reference;
import ikeyler.mlmod.cfg.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final String MOD_PREFIX = "§8» §f";
    public static final String NOTIFICATION_SOUND = "entity.experience_orb.pickup";
    public static boolean NIGHT_DEV_MODE = false;
    public static float GAME_GAMMA_SETTING = mc.gameSettings.gammaSetting;
    public static LocalDateTime LATEST_WORLD_JOIN = LocalDateTime.now();
    public static final String VAR_SEPARATOR = "::";

    public static void openConfigGui() {
        mc.displayGuiScreen(new GuiConfig(mc.currentScreen, Reference.MOD_ID, new TextComponentTranslation("mlmod.config").getFormattedText()));
    }
    public static boolean isOnMineland() {
        ServerData data = mc.getCurrentServerData();
        return data != null && Arrays.stream(Configuration.MISC.MINELAND_IPS.split(","))
                .anyMatch(ip -> data.serverIP.contains(ip.trim()));
    }
    public static List<String> getScoreboardLines() {
        return new ArrayList<>(mc.world.getScoreboard().getObjectiveNames());
    }
    public static void enableNightDevMode() {
        if (Configuration.CREATIVE.DEV_NIGHT_MODE.get() && mc.player.isCreative()) {
            NIGHT_DEV_MODE = true;
            mc.gameSettings.gammaSetting = 1000.0F;
        }
    }
    public static void disableNightDevMode() {
        if (NIGHT_DEV_MODE && Configuration.CREATIVE.DEV_NIGHT_MODE.get()) {
            mc.gameSettings.gammaSetting = GAME_GAMMA_SETTING;
        }
        NIGHT_DEV_MODE = false;
    }

    public static void nightModeCommand() {
        if (!mc.player.isCreative()) {
            mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.creative_mode_needed"));
            return;
        }
        if (!Configuration.CREATIVE.DEV_NIGHT_MODE.get()) {
            mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.nightmode.enable_in_config"));
            return;
        }
        enableNightDevMode();
        mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.nightmode.enabled"));
    }
    public static void sendSuccess() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.success"));
    }
    public static void sendBarSuccess() {
        mc.ingameGUI.setOverlayMessage(new TextComponentTranslation("mlmod.success"), false);
    }
    public static void sendIncorrectArguments() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.incorrect_arguments"));
    }
    public static void sendCommandError() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.command_error"));
    }
    public static void sendCreativeModeNeeded() {
        mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.creative_mode_needed"));
    }
}
