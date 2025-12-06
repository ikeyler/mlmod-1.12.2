package ikeyler.mlmod.cfg;

import ikeyler.mlmod.messages.Messages;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ikeyler.mlmod.Main.messageManager;

public class ClothConfig {
    public static Screen buildConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslationTextComponent("mlmod.config"));
        ConfigCategory general = builder.getOrCreateCategory(new TranslationTextComponent("mlmod.config.category.general"));
        ConfigCategory general_messages = builder.getOrCreateCategory(new TranslationTextComponent("mlmod.config.category.messages"));
        ConfigCategory patterns = builder.getOrCreateCategory(new TranslationTextComponent("mlmod.config.category.patterns"));
        ConfigCategory creative = builder.getOrCreateCategory(new TranslationTextComponent("mlmod.config.category.creative"));
        ConfigCategory chat_formatting = builder.getOrCreateCategory(new TranslationTextComponent("mlmod.config.category.chat_formatting"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // general
        TextListEntry general_description = entryBuilder.startTextDescription(new TranslationTextComponent("mlmod.config.category.general.tooltip")).build();
        List<String> ignoredPlayers = new ArrayList<>(Config.IGNORED_PLAYERS.get());
        StringListListEntry ignored_players = entryBuilder.startStrList(new TranslationTextComponent("mlmod.config.option.ignored_players"),
                ignoredPlayers)
                .setTooltip(new TranslationTextComponent("mlmod.config.option.ignored_players.tooltip"))
                .setDefaultValue(Collections.emptyList())
                .setSaveConsumer(Config.IGNORED_PLAYERS::set)
                .build();
        BooleanListEntry chat_player_interact = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.chat_player_interact"), Config.CHAT_PLAYER_INTERACT.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.chat_player_interact.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(Config.CHAT_PLAYER_INTERACT::set)
                .build();
        BooleanListEntry ads = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.ads"), Config.ADS.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.ads.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.ADS::set)
                .build();
        BooleanListEntry pm_notification = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.pm_notification"), Config.PM_NOTIFICATION.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.pm_notification.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(Config.PM_NOTIFICATION::set)
                .build();
        BooleanListEntry message_collector = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.message_collector"), Config.MESSAGE_COLLECTOR.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.message_collector.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(Config.MESSAGE_COLLECTOR::set)
                .build();
        BooleanListEntry hide_translate = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.hide_translate"), Config.HIDE_TRANSLATE.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.hide_translate.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(Config.HIDE_TRANSLATE::set)
                .build();
        EnumListEntry<Config.CHAT_MODE> excl_mark_to_chat = entryBuilder.startEnumSelector(new TranslationTextComponent("mlmod.config.option.excl_mark_to_chat"), Config.CHAT_MODE.class, Config.EXCL_MARK_TO_CHAT.get())
                .setDefaultValue(Config.CHAT_MODE.OFF)
                .setSaveConsumer(Config.EXCL_MARK_TO_CHAT::set)
                .build();

        // main messages
        TextListEntry general_messages_description = entryBuilder.startTextDescription(new TranslationTextComponent("mlmod.config.category.messages.tooltip")).build();
        BooleanListEntry reward_storage = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.reward_storage"), Config.REWARD_STORAGE.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.REWARD_STORAGE::set)
                .build();
        BooleanListEntry welcome_to_mineland = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.welcome_to_mineland"), Config.WELCOME_TO_MINELAND.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.WELCOME_TO_MINELAND::set)
                .build();
        BooleanListEntry unanswered_asks = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.unanswered_asks"), Config.UNANSWERED_ASKS.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.unanswered_asks.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.UNANSWERED_ASKS::set)
                .build();
        BooleanListEntry unread_mail = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.unread_mail"), Config.UNREAD_MAIL.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.unread_mail.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.UNREAD_MAIL::set)
                .build();
        BooleanListEntry new_video = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.new_video"), Config.NEW_VIDEO.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.new_video.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.NEW_VIDEO::set)
                .build();
        BooleanListEntry punishment_broadcast = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.punishment_broadcast"), Config.PUNISHMENT_BROADCAST.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.punishment_broadcast.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.PUNISHMENT_BROADCAST::set)
                .build();
        BooleanListEntry donation = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.donation"), Config.DONATION.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.donation.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.DONATION::set)
                .build();
        BooleanListEntry player_voted = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.player_voted"), Config.PLAYER_VOTED.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.PLAYER_VOTED::set)
                .build();
        BooleanListEntry stream = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.stream"), Config.STREAM.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.STREAM::set)
                .build();
        BooleanListEntry new_ask = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.new_ask"), Config.NEW_ASK.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.NEW_ASK::set)
                .build();

        // creative
        TextListEntry creative_description = entryBuilder.startTextDescription(new TranslationTextComponent("mlmod.config.category.creative.tooltip")).build();
        BooleanListEntry world_invite = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.world_invite"), Config.WORLD_INVITE.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.WORLD_INVITE::set)
                .build();
        BooleanListEntry dev_mode_join = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.dev_mode_join"), Config.DEV_MODE_JOIN.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.DEV_MODE_JOIN::set)
                .build();
        BooleanListEntry show_world_id = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.show_world_id"), Config.SHOW_WORLD_ID.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.show_world_id.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.SHOW_WORLD_ID::set)
                .build();
        BooleanListEntry play_sound = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.play_sound"), Config.PLAY_SOUND.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.play_sound.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(Config.PLAY_SOUND::set)
                .build();
        BooleanListEntry sound_command = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.sound_command"), Config.SOUND_COMMAND.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.sound_command.tooltip"))
                .setDefaultValue(true)
                .setSaveConsumer(Config.SOUND_COMMAND::set)
                .build();
        BooleanListEntry dev_night_mode = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.dev_night_mode"), Config.DEV_NIGHT_MODE.get())
                .setDefaultValue(false)
                .setSaveConsumer(Config.DEV_NIGHT_MODE::set)
                .build();

        // patterns
        TextListEntry patterns_description = entryBuilder.startTextDescription(new TranslationTextComponent("mlmod.config.category.patterns.tooltip")).build();
        StringListEntry reward_storage_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.reward_storage"), Config.REWARD_STORAGE_PATTERN.get())
                .setDefaultValue(Messages.REWARD_STORAGE.getFixedTemplate())
                .setSaveConsumer(value -> {Config.REWARD_STORAGE_PATTERN.set(value); Messages.REWARD_STORAGE.setNewTemplate(value);})
                .build();
        StringListEntry welcome_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.welcome_to_mineland"), Config.WELCOME_PATTERN.get())
                .setDefaultValue(Messages.WELCOME_TO_MINELAND.getFixedTemplate())
                .setSaveConsumer(value -> {Config.WELCOME_PATTERN.set(value); Messages.WELCOME_TO_MINELAND.setNewTemplate(value);})
                .build();
        StringListEntry dev_mode_join_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.dev_mode_join"), Config.DEV_MODE_JOIN_PATTERN.get())
                .setDefaultValue(Messages.DEV_MODE_JOIN.getFixedTemplate())
                .setSaveConsumer(value -> {Config.DEV_MODE_JOIN_PATTERN.set(value); Messages.DEV_MODE_JOIN.setNewTemplate(value);})
                .build();
        StringListEntry unanswered_asks_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.unanswered_asks"), Config.UNANSWERED_ASKS_PATTERN.get())
                .setDefaultValue(Messages.UNANSWERED_ASKS.getFixedTemplate())
                .setSaveConsumer(value -> {Config.UNANSWERED_ASKS_PATTERN.set(value); Messages.UNANSWERED_ASKS.setNewTemplate(value);})
                .build();
        StringListEntry unread_mail_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.unread_mail"), Config.UNREAD_MAIL_PATTERN.get())
                .setDefaultValue(Messages.UNREAD_MAIL.getFixedTemplate())
                .setSaveConsumer(value -> {Config.UNREAD_MAIL_PATTERN.set(value); Messages.UNREAD_MAIL.setNewTemplate(value);})
                .build();
        StringListEntry world_invite_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.world_invite"), Config.WORLD_INVITE_PATTERN.get())
                .setDefaultValue(Messages.WORLD_INVITE.getFixedTemplate())
                .setSaveConsumer(value -> {Config.WORLD_INVITE_PATTERN.set(value); Messages.WORLD_INVITE.setNewTemplate(value);})
                .build();
        StringListEntry new_video_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.new_video"), Config.NEW_VIDEO_PATTERN.get())
                .setDefaultValue(Messages.NEW_VIDEO.getFixedTemplate())
                .setSaveConsumer(value -> {Config.NEW_VIDEO_PATTERN.set(value); Messages.NEW_VIDEO.setNewTemplate(value);})
                .build();
        StringListEntry punishment_broadcast_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.punishment_broadcast"), Config.PUNISHMENT_BROADCAST_PATTERN.get())
                .setDefaultValue(Messages.PUNISHMENT_BROADCAST.getFixedTemplate())
                .setSaveConsumer(value -> {Config.PUNISHMENT_BROADCAST_PATTERN.set(value); Messages.PUNISHMENT_BROADCAST.setNewTemplate(value);})
                .build();
        StringListEntry donation_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.donation"), Config.DONATION_PATTERN.get())
                .setDefaultValue(Messages.DONATION.getFixedTemplate())
                .setSaveConsumer(value -> {Config.DONATION_PATTERN.set(value); Messages.DONATION.setNewTemplate(value);})
                .build();
        StringListEntry player_voted_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.player_voted"), Config.PLAYER_VOTED_PATTERN.get())
                .setDefaultValue(Messages.PLAYER_VOTED.getFixedTemplate())
                .setSaveConsumer(value -> {Config.PLAYER_VOTED_PATTERN.set(value); Messages.PLAYER_VOTED.setNewTemplate(value);})
                .build();
        StringListEntry stream_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.stream"), Config.STREAM_PATTERN.get())
                .setDefaultValue(Messages.STREAM.getFixedTemplate())
                .setSaveConsumer(value -> {Config.STREAM_PATTERN.set(value); Messages.STREAM.setNewTemplate(value);})
                .build();
        StringListEntry new_ask_pattern = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.new_ask"), Config.NEW_ASK_PATTERN.get())
                .setDefaultValue(Messages.NEW_ASK.getFixedTemplate())
                .setSaveConsumer(value -> {Config.NEW_ASK_PATTERN.set(value); Messages.NEW_ASK.setNewTemplate(value);})
                .build();

        // formatting
        BooleanListEntry formatting = entryBuilder.startBooleanToggle(new TranslationTextComponent("mlmod.config.option.chat_formatting"), Config.CHAT_FORMATTING.get())
                .setDefaultValue(false)
                .setSaveConsumer(Config.CHAT_FORMATTING::set)
                .build();
        StringListEntry formatting_cc = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.chat_formatting_cc"), Config.FORMATTING_CC.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.chat_formatting_cc.tooltip"))
                .setDefaultValue("&3CC &8|")
                .setSaveConsumer(Config.FORMATTING_CC::set)
                .build();
        StringListEntry formatting_dc = entryBuilder.startStrField(new TranslationTextComponent("mlmod.config.option.chat_formatting_dc"), Config.FORMATTING_DC.get())
                .setTooltip(new TranslationTextComponent("mlmod.config.option.chat_formatting_dc.tooltip"))
                .setDefaultValue("&2DC &8|")
                .setSaveConsumer(Config.FORMATTING_DC::set)
                .build();

        general.addEntry(general_description).addEntry(ignored_players).addEntry(chat_player_interact).addEntry(ads)
                .addEntry(pm_notification).addEntry(message_collector).addEntry(hide_translate).addEntry(excl_mark_to_chat);

        general_messages.addEntry(general_messages_description).addEntry(reward_storage).addEntry(welcome_to_mineland).addEntry(unanswered_asks).addEntry(unread_mail)
                .addEntry(new_video).addEntry(punishment_broadcast).addEntry(donation).addEntry(player_voted)
                .addEntry(stream).addEntry(new_ask);

        creative.addEntry(creative_description).addEntry(world_invite).addEntry(show_world_id).addEntry(dev_mode_join)
                .addEntry(play_sound).addEntry(sound_command).addEntry(dev_night_mode);

        patterns.addEntry(patterns_description).addEntry(welcome_pattern).addEntry(dev_mode_join_pattern)
                .addEntry(reward_storage_pattern).addEntry(unanswered_asks_pattern)
                .addEntry(unread_mail_pattern).addEntry(world_invite_pattern)
                .addEntry(new_video_pattern).addEntry(punishment_broadcast_pattern).addEntry(donation_pattern)
                .addEntry(player_voted_pattern).addEntry(stream_pattern).addEntry(new_ask_pattern);

        chat_formatting.addEntry(formatting).addEntry(formatting_cc).addEntry(formatting_dc);
        builder.setSavingRunnable(() -> {
            Config.spec.save();
            Messages.updateMessages();
            messageManager.updateIgnoredPlayers();
        });
        return builder.build();
    }
}