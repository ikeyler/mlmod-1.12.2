package ikeyler.mlmod.cfg;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.Reference;
import ikeyler.mlmod.messages.Messages;
import ikeyler.mlmod.util.ModUtils;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid= Reference.MOD_ID)
@Config.LangKey("mlmod.config")
@Mod.EventBusSubscriber(modid=Reference.MOD_ID)
public class Configuration {

    @Config.LangKey("mlmod.config.category.general")
    @Config.Comment("mlmod.config.category.general.tooltip")
    public static General GENERAL = new General();
    @Config.LangKey("mlmod.config.category.messages")
    @Config.Comment("mlmod.config.category.messages.tooltip")
    public static GeneralMessages GENERAL_MESSAGES = new GeneralMessages();
    @Config.LangKey("mlmod.config.category.creative")
    @Config.Comment("mlmod.config.category.creative.tooltip")
    public static Creative CREATIVE = new Creative();
    @Config.LangKey("mlmod.config.category.chat_formatting")
    @Config.Comment("mlmod.config.category.chat_formatting.tooltip")
    public static ChatFormatting CHAT_FORMATTING = new ChatFormatting();

    public static class General {
        @Config.LangKey("mlmod.config.option.ignored_players")
        @Config.Comment("mlmod.config.option.ignored_players.tooltip")
        public String[] IGNORED_PLAYERS = {};
        @Config.LangKey("mlmod.config.option.ads")
        @Config.Comment("mlmod.config.option.ads.tooltip")
        public Bool ADS = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.chat_player_interact")
        @Config.Comment("mlmod.config.option.chat_player_interact.tooltip")
        public Bool CHAT_PLAYER_INTERACT = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.pm_notification")
        @Config.Comment("mlmod.config.option.pm_notification.tooltip")
        public Bool PM_NOTIFICATION = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.message_collector")
        @Config.Comment("mlmod.config.option.message_collector.tooltip")
        public Bool MESSAGE_COLLECTOR = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.hide_translate")
        @Config.Comment("mlmod.config.option.hide_translate.tooltip")
        public Bool HIDE_TRANSLATE = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.excl_mark_to_chat")
        @Config.Comment("mlmod.config.option.excl_mark_to_chat.tooltip")
        public CHAT_MODE EXCL_MARK_TO_CHAT = CHAT_MODE.OFF;
    }

    public static class GeneralMessages {
        @Config.LangKey("mlmod.config.option.welcome_to_mineland")
        public Bool WELCOME_TO_MINELAND = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.reward_storage")
        public Bool REWARD_STORAGE = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.unanswered_asks")
        @Config.Comment("mlmod.config.option.unanswered_asks.tooltip")
        public Bool UNANSWERED_ASKS = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.unread_mail")
        @Config.Comment("mlmod.config.option.unread_mail.tooltip")
        public Bool UNREAD_MAIL = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.new_video")
        @Config.Comment("mlmod.config.option.new_video.tooltip")
        public Bool NEW_VIDEO = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.punishment_broadcast")
        @Config.Comment("mlmod.config.option.punishment_broadcast.tooltip")
        public Bool PUNISHMENT_BROADCAST = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.donation")
        @Config.Comment("mlmod.config.option.donation.tooltip")
        public Bool DONATION = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.player_voted")
        public Bool PLAYER_VOTED = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.stream")
        public Bool STREAM = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.new_ask")
        public Bool NEW_ASK = Bool.TRUE;
    }

    public static class Creative {
        @Config.LangKey("mlmod.config.option.world_invite")
        public Bool WORLD_INVITE = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.dev_mode_join")
        public Bool DEV_MODE_JOIN = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.show_world_id")
        @Config.Comment("mlmod.config.option.show_world_id.tooltip")
        public Bool SHOW_WORLD_ID = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.play_sound")
        @Config.Comment("mlmod.config.option.play_sound.tooltip")
        public Bool PLAY_SOUND = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.sound_command")
        @Config.Comment("mlmod.config.option.sound_command.tooltip")
        public Bool SOUND_COMMAND = Bool.TRUE;
        @Config.LangKey("mlmod.config.option.dev_night_mode")
        public Bool DEV_NIGHT_MODE = Bool.FALSE;
    }

    public static class ChatFormatting {
        @Config.LangKey("mlmod.config.option.chat_formatting")
        public Bool CHAT_FORMATTING = Bool.FALSE;
        @Config.LangKey("mlmod.config.option.chat_formatting_cc")
        @Config.Comment("mlmod.config.option.chat_formatting_cc.tooltip")
        public String CREATIVE_CHAT = "&3CC &8|";
        @Config.LangKey("mlmod.config.option.chat_formatting_dc")
        @Config.Comment("mlmod.config.option.chat_formatting_dc.tooltip")
        public String DONATE_CHAT = "&2DC &8|";
    }

    public enum Bool {
        TRUE(new TextComponentTranslation("mlmod.on").getUnformattedText(), true),
        FALSE(new TextComponentTranslation("mlmod.off").getUnformattedText(), false);
        private final String name;
        private final boolean value;
        Bool(String name, boolean value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return this.name;
        }
        public boolean get() {
            return this.value;
        }
        public static Bool fromBoolean(boolean value) {
            return value ? TRUE : FALSE;
        }
    }

    public enum CHAT_MODE {
        DC("mlmod.config.option.excl_mark_to_chat.dc"),
        CC("mlmod.config.option.excl_mark_to_chat.cc"),
        OFF("mlmod.config.option.excl_mark_to_chat.off");
        private final String translation;
        CHAT_MODE(String translation) {
            this.translation = translation;
        }
        @Override
        public String toString() {
            return new TextComponentTranslation(this.translation).getUnformattedText();
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            Messages.updateMessages();
            Main.messageManager.updateIgnoredPlayers();
            if (!CREATIVE.DEV_NIGHT_MODE.get()) ModUtils.disableNightDevMode();
            Main.logger.info("config updated");
        }
    }
}