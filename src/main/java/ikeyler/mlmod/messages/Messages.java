package ikeyler.mlmod.messages;

import ikeyler.mlmod.cfg.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Messages {

    public static final Message WELCOME_TO_MINELAND = new Message("-----------------------------------------------------\n" +
            "     Вот ты и дома, на Mineland Network\n" +
            "\n" +
            " ❖ Беги вперёд и выбери режим для игры!\n" +
            " ❖ Кликни по компасу, чтобы узнать больше\\.\n" +
            " ❖ Играй с другом, вместе веселее - \\/party\n" +
            " \n" +
            " \\[Telegram\\] - \\[Дискорд\\] - \\[Форум\\]\n" +
            "-----------------------------------------------------");
    public static final Message DEV_MODE_JOIN = new Message(" Ты снова в режиме разработчика\\. С возвращением!\n" +
            "  \n" +
            "  \n" +
            " \\| Что-то забыл\\? Открой плейлист с примерами!\n" +
            "  \n" +
            "         \\[Кликни для просмотра плейлиста\\]\n" +
            "  \n" +
            "  ");
    public static final Message REWARD_STORAGE = new Message("Награды » В Секретном Хранилище что-то есть\\.\\.\\.\n" +
            "\n" +
            "                  \\[Забрать Награду\\]\n");
    public static final Message UNREAD_MAIL = new Message("Почта » У тебя есть (.*?) непрочитанных писем - /mail read");
    public static final Message UNANSWERED_ASKS = new Message("Вопросы » Всего (.*?) неотвеченных вопросов - /asks");
    public static final Message WORLD_INVITE = new Message(" \\| \\[Приглашение в мир\\]\n" +
            " \\| (.*?) приглашает тебя посетить игру:\n" +
            " \\|   (.*?) \n" +
            " \\| \n" +
            " \\|            \\[Присоединиться\\]\n" +
            " \\| \n" +
            "  ");
    public static final Message NEW_VIDEO = new Message(" \n" +
            " \\| Новое видео на канале игрока (.*?)\n" +
            " \\| Кликни, чтобы посмотреть: (.*?)\n" +
            " \\| Название: (.*?)\n" +
            " \\| Просмотры: (.*?)");
    public static final Message PUNISHMENT_BROADCAST = new Message("  \n" +
            " \\|        ОБЪЯВЛЕНИЕ О НАКАЗАНИЯХ     \n" +
            " \\| За последние 30 дней персонал забанил (.*?) игроков\\.\n" +
            " \\| В том числе замутил (.*?) игроков\\.\n" +
            " \\| Узнай о правилах сервера Mineland - \\/rules\n" +
            "  ");
    public static final Message DONATION = new Message("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀\n" +
            "            ПОЗДРАВЛЯЕМ!\n" +
            "Игрок (.*?) купил (.*?)" +
            "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
    public static final Message PLAYER_VOTED = new Message(" \n" +
            " \\| Игрок (.*?) проголосовал за Mineland!\n" +
            " \\| ТОП 1 месяца забирает ключ от лицензии Minecraft\\.\n" +
            "   \n" +
            "                  \\[Голосовать\\]\n" +
            " ");
    public static final Message STREAM = new Message(" \n" +
            " \\| Идет стрим игрока (.*?)\n" +
            " \\| Кликни, чтобы посмотреть:" +
            "(.*?)\\]");
    public static final Message NEW_ASK = new Message("Вопросы » Вопрос от (.*?)\\. Сервер: (.*?)\\.");
    public static final Message CREATIVE_CHAT = new Message("Креатив-чат » (.*?): (.*?)");
    public static final Message DONATE_CHAT = new Message("Донат-чат » (.*?): (.*?)");
    public static final Message PM = new Message("✉ » (\\S+) -> тебе \\| (.*?)");
    public static final Message PM_REPLY = new Message("✉ » ты -> (\\S+) \\| (.*?)");

    // ad messages
    public static final Message AD_DELIMITER = new Message(" ");
    public static final Message AD_DELIMITER2 = new Message(" \\|   ?");

    public static final Message AD_DONATE_UPGRADE = new Message(" \\|    ДОПЛАТА В ПАРУ КЛИКОВ     ");
    public static final Message AD_DONATE_UPGRADE2 = new Message(" \\|  Закупил донат и теперь хочешь ранг повыше\\?");
    public static final Message AD_DONATE_UPGRADE3 = new Message(" \\|  Специально для тебя у нас есть система доплаты!");
    public static final Message AD_DONATE_UPGRADE4 = new Message(" \\| ➜ Не знай границ - mineland\\.net(.*?) ?");

    public static final Message AD_ACCOUNT_SECURITY = new Message(" \\|    ЗАЩИТИ СВОЙ АККАУНТ ОТ ВЗЛОМА!     ");
    public static final Message AD_ACCOUNT_SECURITY2 = new Message(" \\|  Хочешь обеспечить безопасность своему аккаунту\\?");
    public static final Message AD_ACCOUNT_SECURITY3 = new Message(" \\|  Боишься, что взломают и на этом игра\\.\\.\\. прекратится\\?!");
    public static final Message AD_ACCOUNT_SECURITY4 = new Message(" \\| ➜ Тогда читай эту статью - bit\\.ly\\/mineland_2fa_ru");

    public static final Message AD_DISCORD = new Message(" \\|    DISCORD ВСЕЛЕННАЯ MINELAND      ");
    public static final Message AD_DISCORD2 = new Message(" \\| ● Каждый вечер игроки собираются в нашем Discord");
    public static final Message AD_DISCORD3 = new Message(" \\| ● Играют, общаются, слушают музыку, находят друзей!");
    public static final Message AD_DISCORD4 = new Message(" \\| ➜ Общайся и веселись уже сейчас - mineland\\.net\\/discord\\/ru");

    public static final Message AD_BECOME_YOUTUBER = new Message(" \\|  Эээу, хочешь стать Ютубером\\? У нас это сделать на изи!");
    public static final Message AD_BECOME_YOUTUBER2 = new Message(" \\| ● Снимаешь летсплеи\\/любишь постримить с друзьями\\?");
    public static final Message AD_BECOME_YOUTUBER3 = new Message(" \\| ● Ранг YouTube создан специально для тебя!");
    public static final Message AD_BECOME_YOUTUBER4 = new Message(" \\| ● Пари в наших Хабах как птичка и развивайся с нами ;\\)");
    public static final Message AD_BECOME_YOUTUBER5 = new Message(" \\| ➜ Стань популярным уже сейчас - \\/youtubers");

    public static final Message AD_TELEGRAM = new Message("\\| ГДЕ ПРОХОДИТ ВЕЧЕРИНКА В MINELAND\\?");
    public static final Message AD_TELEGRAM2 = new Message("\\| Присоединяйтесь к нашему Telegram:");
    public static final Message AD_TELEGRAM3 = new Message("\\| - частые конкурсы");
    public static final Message AD_TELEGRAM4 = new Message("\\| - потрясающие новости и обновления");
    public static final Message AD_TELEGRAM5 = new Message("\\| Находи друзей и играй в другие мини-игры!");
    public static final Message AD_TELEGRAM6 = new Message("\\| Стань частью сообщества - mineland\\.net\\/tg");

    public static final Message AD_TELEGRAM_2 = new Message(" \\|    TELEGRAM БЕСЕДА НА MINELAND   (.*?)");
    public static final Message AD_TELEGRAM_2_2 = new Message(" \\|  Не знаешь с кем повыживать на СкайБлоке\\? ");
    public static final Message AD_TELEGRAM_2_3 = new Message(" \\|  Или погонять на БедВарсе и СкайВарсе\\?");
    public static final Message AD_TELEGRAM_2_4 = new Message(" \\| ➜ Заходи к нам и ищи крутых друзей - mineland\\.net\\/tg");

    public static final Message AD_GOLD = new Message(" \\|    ЗАЧЕМ НУЖНО ЗОЛОТО НА MINELAND\\?     ");
    public static final Message AD_GOLD2 = new Message(" \\| ● Золото - донат-валюта, оно спасает на мини-играх!");
    public static final Message AD_GOLD3 = new Message(" \\| ● Либо потратить его на эффекты и выделиться ;\\)");
    public static final Message AD_GOLD4 = new Message(" \\| ● ⛂ можно купить или выбить из кейсов\\.");
    public static final Message AD_GOLD5 = new Message(" \\| ➜ Легче купить и не париться - mineland\\.net");

    public static final Message AD_REPORT = new Message(" \\|    ОБНАРУЖИЛ НАРУШИТЕЛЯ\\?\\?\\? (.*?)");
    public static final Message AD_REPORT2 = new Message(" \\| ● Быстрее пиши команду \\/report ник и его накажут!");
    public static final Message AD_REPORT3 = new Message(" \\| ● Жалобу рассмотрят в режиме реального времени!");
    public static final Message AD_REPORT4 = new Message(" \\| ➜ Если у тебя появились вопросы, пиши - \\/ask вопрос ");

    public static final Message AD_EXPERT = new Message(" \\|  Освоился на Mineland и хочешь чего-то большего\\? ;\\)");
    public static final Message AD_EXPERT2 = new Message(" \\| ● С рангом Expert можно перевернуть ГОРЫ и даже НЕБО!");
    public static final Message AD_EXPERT3 = new Message(" \\| ● А всё почему\\? Идеальный баланс, доступная цена");
    public static final Message AD_EXPERT4 = new Message(" \\| ● и\\.\\.\\. множество полезных возможностей!");
    public static final Message AD_EXPERT5 = new Message(" \\| ➜ Переворачивай горы и небо - mineland\\.net");

    public static final Message AD_HERO = new Message(" \\|  На Mineland играют настоящие ГЕРОИ!");
    public static final Message AD_HERO2 = new Message(" \\| ● Ранг \\[HERO\\] по сей день является мечтой многих\\.\\.\\.");
    public static final Message AD_HERO3 = new Message(" \\| ● А всё почему\\? Своими возможностями и бонусами");
    public static final Message AD_HERO4 = new Message(" \\| ● он привлекает каждого, но не каждый может быть им!");
    public static final Message AD_HERO5 = new Message(" \\| ➜ А ты уже можешь, не мечтай - mineland\\.net");

    public static final Message AD_GAMER = new Message(" \\|  Не знаешь какой ранг выбрать на Mineland\\?");
    public static final Message AD_GAMER2 = new Message(" \\| ● Начни с ранга Gamer по очень привлекательной цене\\.");
    public static final Message AD_GAMER3 = new Message(" \\| ● Все его возможности здесь - \\/donate");
    public static final Message AD_GAMER4 = new Message(" \\| ➜ Будь круче всех с нами - mineland\\.net");

    public static final Message AD_LICENSE = new Message(" \\|    БЕСПЛАТНАЯ ЛИЦЕНЗИЯ     ");
    public static final Message AD_LICENSE2 = new Message(" \\| ● Голосуй, получай кейсы с наградами,");
    public static final Message AD_LICENSE3 = new Message(" \\| ● изумруды, ключик от Майнкрафт и\\.\\.\\. ");
    public static final Message AD_LICENSE4 = new Message(" \\| ● Ранг EXPERT HERO Навсегда!");
    public static final Message AD_LICENSE5 = new Message(" \\| ➜ Узнай все подробности сейчас - \\/vote");

    public static final Message AD_COSMETICS = new Message(" \\|    ПРЕОБРАЗИСЬ С КОСМЕТИКОЙ     ");
    public static final Message AD_COSMETICS2 = new Message(" \\| ● Благодаря эффектам твой персонаж преобразится вмиг!");
    public static final Message AD_COSMETICS3 = new Message(" \\| ● Чем более редкий эффект, тем он красивее - \\/cosmetics");
    public static final Message AD_COSMETICS4 = new Message(" \\| ➜ Стань на свете всех милее - mineland\\.net ");

    public static final Message AD_CREATIVE_TG = new Message(" \\| Будь в комьюнити КРЕАТИВ\\+");
    public static final Message AD_CREATIVE_TG2 = new Message(" \\| Там обсуждают закрытую информацию\\.");
    public static final Message AD_CREATIVE_TG3 = new Message(" \\| Заходи в Telegram: mineland\\.net\\/tg");

    public static final List<Message> MESSAGES = new ArrayList<>(
            Arrays.asList(REWARD_STORAGE, WELCOME_TO_MINELAND, DEV_MODE_JOIN, UNREAD_MAIL, UNANSWERED_ASKS, WORLD_INVITE, NEW_VIDEO, PUNISHMENT_BROADCAST,
                    DONATION, PLAYER_VOTED, STREAM, NEW_ASK, CREATIVE_CHAT, DONATE_CHAT, PM, PM_REPLY)
    );

    public static final List<Message> AD_MESSAGES = new ArrayList<>(
            Arrays.asList(AD_DELIMITER, AD_DELIMITER2,
                    AD_DONATE_UPGRADE, AD_DONATE_UPGRADE2, AD_DONATE_UPGRADE3, AD_DONATE_UPGRADE4,
                    AD_ACCOUNT_SECURITY, AD_ACCOUNT_SECURITY2, AD_ACCOUNT_SECURITY3, AD_ACCOUNT_SECURITY4,
                    AD_DISCORD, AD_DISCORD2, AD_DISCORD3, AD_DISCORD4,
                    AD_BECOME_YOUTUBER, AD_BECOME_YOUTUBER2, AD_BECOME_YOUTUBER3, AD_BECOME_YOUTUBER4, AD_BECOME_YOUTUBER5,
                    AD_TELEGRAM, AD_TELEGRAM2, AD_TELEGRAM3, AD_TELEGRAM4, AD_TELEGRAM5, AD_TELEGRAM6,
                    AD_GOLD, AD_GOLD2, AD_GOLD3, AD_GOLD4, AD_GOLD5,
                    AD_REPORT, AD_REPORT2, AD_REPORT3, AD_REPORT4,
                    AD_EXPERT, AD_EXPERT2, AD_EXPERT3, AD_EXPERT4, AD_EXPERT5,
                    AD_HERO, AD_HERO2, AD_HERO3, AD_HERO4, AD_HERO5,
                    AD_GAMER, AD_GAMER2, AD_GAMER3, AD_GAMER4,
                    AD_TELEGRAM_2, AD_TELEGRAM_2_2, AD_TELEGRAM_2_3, AD_TELEGRAM_2_4,
                    AD_LICENSE, AD_LICENSE2, AD_LICENSE3, AD_LICENSE4, AD_LICENSE5,
                    AD_COSMETICS, AD_COSMETICS2, AD_COSMETICS3, AD_COSMETICS4,
                    AD_CREATIVE_TG, AD_CREATIVE_TG2, AD_CREATIVE_TG3
            )
    );

    public static void updateMessages() {
        REWARD_STORAGE.setActive(Configuration.GENERAL_MESSAGES.REWARD_STORAGE.get());
        WELCOME_TO_MINELAND.setActive(Configuration.GENERAL_MESSAGES.WELCOME_TO_MINELAND.get());
        DEV_MODE_JOIN.setActive(Configuration.CREATIVE.DEV_MODE_JOIN.get());
        UNREAD_MAIL.setActive(Configuration.GENERAL_MESSAGES.UNREAD_MAIL.get());
        UNANSWERED_ASKS.setActive(Configuration.GENERAL_MESSAGES.UNANSWERED_ASKS.get());
        WORLD_INVITE.setActive(Configuration.CREATIVE.WORLD_INVITE.get());
        NEW_VIDEO.setActive(Configuration.GENERAL_MESSAGES.NEW_VIDEO.get());
        PUNISHMENT_BROADCAST.setActive(Configuration.GENERAL_MESSAGES.PUNISHMENT_BROADCAST.get());
        DONATION.setActive(Configuration.GENERAL_MESSAGES.DONATION.get());
        PLAYER_VOTED.setActive(Configuration.GENERAL_MESSAGES.PLAYER_VOTED.get());
        STREAM.setActive(Configuration.GENERAL_MESSAGES.STREAM.get());
        NEW_ASK.setActive(Configuration.GENERAL_MESSAGES.NEW_ASK.get());
    }
}
