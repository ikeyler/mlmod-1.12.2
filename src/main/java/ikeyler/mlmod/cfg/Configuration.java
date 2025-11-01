package ikeyler.mlmod.cfg;

import ikeyler.mlmod.Reference;
import ikeyler.mlmod.messages.Messages;
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

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            Messages.updateMessages();
        }
    }
}