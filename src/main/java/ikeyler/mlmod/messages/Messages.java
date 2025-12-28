package ikeyler.mlmod.messages;

import ikeyler.mlmod.cfg.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Messages {

    public static final Message WELCOME_TO_MINELAND = new Message(
            "-----------------------------------------------------\n" +
                    "     Вот ты и дома, на Mineland Network\n" +
                    "\n" +
                    " ❖ Беги вперёд и выбери режим для игры!\n" +
                    " ❖ Кликни по компасу, чтобы узнать больше\\.\n" +
                    " ❖ Играй с другом, вместе веселее - \\/party\n" +
                    " \n" +
                    " \\[Telegram\\] - \\[Дискорд\\] - \\[Форум\\]\n" +
                    "-----------------------------------------------------",

            "-----------------------------------------------------\n" +
                    "     Welcome to Mineland Network\n" +
                    "\n" +
                    " ❖ Have fun with our unique Servers! - \\/servers\n" +
                    " ❖ Feeling alone\\? Invite your Friends! - \\/party\n" +
                    " ❖ Treat yourself with Luxury! - \\/ranks\n" +
                    " ❖ Find us out in more Platforms! - \\/site\n \n" +
                    " \\[Facebook\\] - \\[Discord\\] - \\[Forum\\]\n" +
                    "-----------------------------------------------------"
    );
    public static final Message DEV_MODE_JOIN = new Message(
            " Ты снова в режиме разработчика\\. С возвращением!\\s*" +
                    "\\| Что-то забыл\\? Открой плейлист с примерами!\\s*" +
                    "\\[Кликни для просмотра плейлиста\\]\n" +
                    "\\s*",

            " You're in developer mode again\\. Welcome back!\\s*\\| \\[ENGLISH VIDEOS\\]\\s*\\| Forgot how to code something\\? Open the playlist with examples!\\s*\\[Click to view the playlist\\]\\s*"
    );
    public static final Message REWARD_STORAGE = new Message(
            "Награды » В Секретном Хранилище что-то есть\\.\\.\\.\\s*\\[Забрать Награду\\]\n",
            "Rewards » There's something in the Secret Storage\\.\\.\\.\\s*\\[Open Storage\\]\n"
    );
    public static final Message UNREAD_MAIL = new Message(
            "Почта » У тебя есть (.*?) непрочитанных писем - /mail read",
            "Mail » Received (.*?) new mails - \\/mail read"
    );
    public static final Message UNANSWERED_ASKS = new Message(
            "Вопросы » Всего (.*?) неотвеченных вопросов - /asks",
            "Questions » (.*?) unanswered questions in the system total - \\/asks"
    );
    public static final Message WORLD_INVITE = new Message(" \\| \\[Приглашение в мир\\]\n" +
            " \\| (.*?) приглашает тебя посетить игру:\n" +
            " \\|   (.*?) \n" +
            " \\| \n" +
            " \\|\\s*\\[Присоединиться\\]\n" +
            " \\|\\s*",

            " \\| \\[Invitation to the world\\]\n \\| (.*?) invites you to visit their game:" +
                    "\n \\| (.*?) \n \\| \n \\|\\s*\\[Join the game\\]\n \\|\\s*"
    );
    public static final Message NEW_VIDEO = new Message(" \n" +
            " \\| Новое видео на канале игрока (.*?)\n" +
            " \\| Кликни, чтобы посмотреть: (.*?)\n" +
            " \\| Название: (.*?)\n" +
            " \\| Просмотры: (.*?)",

            " \n \\| (.*?) released a new video!\n\\| Click to view: (.*?)\n \\| Name: (.*?)\n \\| Views: (.*?)"
    );
    public static final Message PUNISHMENT_BROADCAST = new Message("  \n" +
            " \\|\\s*ОБЪЯВЛЕНИЕ О НАКАЗАНИЯХ\\s*\n" +
            " \\| За последние 30 дней персонал забанил (.*?) игроков\\.\n" +
            " \\| В том числе замутил (.*?) игроков\\.\n" +
            " \\| Узнай о правилах сервера Mineland - \\/rules\\s*",

            "\\s*\\|\\s*ANNOUNCEMENT OF PUNISHMENTS\\s*" +
                    "\\| In the last 30 days staff has banned (.*?) players\\.\n " +
                    "\\| In addition, muted (.*?) players\\.\n " +
                    "\\| Find out about the Mineland server rules - \\/rules\\s*"
    );
    public static final Message DONATION = new Message("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀\n" +
            "\\s*ПОЗДРАВЛЯЕМ!\n" +
            "Игрок (.*?) купил (.*?)" +
            "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀",

            "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀\\s*CONGRATULATIONS!\n(.*?) just bought (.*?)" +
            "Congratulate them on the purchase!\n" +
            "(.*?)\n\n" +
            "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀"
    );
    public static final Message PLAYER_VOTED = new Message(" \n" +
            " \\| Игрок (.*?) проголосовал за Mineland!\n" +
            " \\| ТОП 1 месяца забирает ключ от лицензии Minecraft\\." +
            "\\s*\\[Голосовать\\]\\s*",

            " \n \\| (.*?) just voted for Mineland!\n " +
                    "\\| The TOP 1 voter of the month will be awarded with a\n " +
                    "\\| Minecraft license key\\.\\s*\\[Vote now\\]\\s*"
    );
    public static final Message STREAM = new Message(" \n \\| Идет стрим игрока (.*?)\n \\| Кликни, чтобы посмотреть:\\s*(.*)",
            " \n \\| (.*?) is livestreaming now!\n \\| Click to view:\\s*(.*)"
    );
    public static final Message NEW_ASK = new Message(
            "Вопросы » Вопрос от (.*?)\\. Сервер: (.*?)\\.",
            "Questions » (.*?) has sent a question from (.*?)\\. Read it - \\/questions\\.");
    public static final Message CREATIVE_CHAT = new Message("(Креатив-чат|Creative-chat) » (.*?): (.*?)");
    public static final Message DONATE_CHAT = new Message("(Донат-чат|Donate-chat) » (.*?): (.*?)");
    public static final Message PARTY_CHAT = new Message("Party (.*?) » ?(.*?)");
    public static final Message PM = new Message("✉ » (\\S+) -> (тебе|you) \\| (.*?)");
    public static final Message PM_REPLY = new Message("✉ » (ты|you) -> (\\S+) \\| (.*?)");

    public static final Message CC_DISABLED = new Message(
            " \\| \\[ОПОВЕЩЕНИЕ\\]",
            " \\| На Sharded отключен глобальный чат\\. Зачем\\?",
            " \\| Вместо него был создан креатив-чат - \\/cc",
            " \\| Хочешь включить и общаться с игроками\\? - \\/cc on",
            " \\| Мешает этот чат\\? - \\/cc off",
            " \\| \\[NOTIFICATION\\]",
            " \\| Global chat is disabled now in Sharded\\.",
            " \\| What's going to replace it\\? ",
            " \\| Creative chat has been created instead of global chat - \\/cc",
            " \\| Want to turn this chat on and chat with players\\? - \\/cc on",
            " \\| Is this chat bothering you\\? - \\/cc off"
    );

    public static final Message LOGIN_CHECK = new Message(
            "Guard » Checking in progress, please wait\\.\\.\\. \n Do not move, everything happens automatically\\.\\s*",
            "Guard » Enter code from the image into chat\\.\\s*If you don't see the image, you need to completely reinstall Minecraft\\."
    );

    public static final Message WORLD_MODE_CHANGE = new Message(
            "Система » Ты вошёл в режим (игры|разработчика)\\.",
            "System » You're now in (playing|developer) mode\\."
    );

    // ad messages
    public static final Message AD_DELIMITER = new Message(" ");
    public static final Message AD_DELIMITER2 = new Message(" \\|   ?");

    public static final Message AD_DONATE_UPGRADE = new Message(
            " \\|\\s*ДОПЛАТА В ПАРУ КЛИКОВ\\s*",
            " \\|  Закупил донат и теперь хочешь ранг повыше\\?",
            " \\|  Специально для тебя у нас есть система доплаты!",
            " \\| ➜ Не знай границ - mineland\\.net(.*?)\\s*",
            " \\|\\s*PAY IN JUST A FEW CLICKS\\s*",
            " \\|  Have you donated already\\? Would you like to have a higher rank\\?",
            " \\|  Don't worry, we've got a way to cover the difference, ya know\\?",
            " \\| ➜ Limits are for others to contend with - mineland\\.net"
    );
    public static final Message AD_ACCOUNT_SECURITY = new Message(
            " \\|\\s*ЗАЩИТИ СВОЙ АККАУНТ ОТ ВЗЛОМА!\\s*",
            " \\|  Хочешь обеспечить безопасность своему аккаунту\\?",
            " \\|  Боишься, что взломают и на этом игра\\.\\.\\. прекратится\\?!",
            " \\| ➜ Тогда читай эту статью - bit\\.ly\\/mineland_2fa_ru",
            " \\|\\s*SECURITY IS PARAMOUNT\\s*",
            " \\|  Want to secure your account\\?",
            " \\|  Worried you'll get hacked and your game will be\\.\\.\\. over\\?!",
            " \\| ➜ Then link your email to your account - https:\\/\\/bit\\.ly\\/mineland_help"
    );

    public static final Message AD_DISCORD = new Message(
            " \\|\\s*DISCORD ВСЕЛЕННАЯ MINELAND\\s*",
            " \\| ● Каждый вечер игроки собираются в нашем Discord",
            " \\| ● Играют, общаются, слушают музыку, находят друзей!",
            " \\| ➜ Общайся и веселись уже сейчас - mineland\\.net\\/discord\\/ru",

            " \\| WHERE IS THE PARTY AT ON MINELAND\\?",
            " \\| Join our Discord channel:",
            " \\| - frequent contests",
            " \\| - awesome activity and update news",
            " \\| Find friends and play in other mini-games!",
            " \\| Be part of the community - mineland\\.net\\/discord\\/en",

            " \\|\\s*MINELAND VOICE CHAT\\s*",
            " \\| ● Every evening players gather\\.\\.\\. Where\\? At our Discord server!\\s*",
            " \\| ● They game, chat, listen to music, and make friends!",
            " \\| ● There's just one (PROBLEM|ISSUE) - you aren't there :с",
            " \\| ➜ The right moment to join is now! JOIN US! - mineland\\.net\\/discord\\/en",
            " \\| ➜ You're one step away from joining the mineland community:\\s*",
            " \\| - mineland\\.net\\/discord\\/en"
    );

    public static final Message AD_YOUTUBER = new Message(
            " \\|  Эээу, хочешь стать Ютубером\\? У нас это сделать на изи!",
            " \\| ● Снимаешь летсплеи\\/любишь постримить с друзьями\\?",
            " \\| ● Ранг YouTube создан специально для тебя!",
            " \\| ● Пари в наших Хабах как птичка и развивайся с нами ;\\)",
            " \\| ➜ Стань популярным уже сейчас - \\/youtubers",
            " \\|  Heeey, you wanna become a §сYouTuber\\? Easy-peasy!",
            " \\| ● Do you (like|love) recording your gameplay, streaming to friends, or both\\?",
            " \\| ● Then look no further cuz the \\[YouTube\\] rank is perfect for you!",
            " \\| ● Soar through the hubs like a majestic bird and grow alongside us ;\\)",
            " \\| ➜ Become popular right away - \\/youtubers"
    );

    public static final Message AD_TELEGRAM = new Message(
            "\\| ГДЕ ПРОХОДИТ ВЕЧЕРИНКА В MINELAND\\?",
            "\\| Присоединяйтесь к нашему Telegram:",
            "\\| - частые конкурсы",
            "\\| - потрясающие новости и обновления",
            "\\| Находи друзей и играй в другие мини-игры!",
            "\\| Стань частью сообщества - mineland\\.net\\/tg",
            " \\|\\s*TELEGRAM БЕСЕДА НА MINELAND\\s*",
            " \\|  Не знаешь с кем повыживать на СкайБлоке\\? ",
            " \\|  Или погонять на БедВарсе и СкайВарсе\\?",
            " \\| ➜ Заходи к нам и ищи крутых друзей - mineland\\.net\\/tg"
    );

    public static final Message AD_GOLD = new Message(
            " \\|\\s*ЗАЧЕМ НУЖНО ЗОЛОТО НА MINELAND\\?\\s*",
            " \\| ● Золото - донат-валюта, оно спасает на мини-играх!",
            " \\| ● Либо потратить его на эффекты и выделиться ;\\)",
            " \\| ● ⛂ можно купить или выбить из кейсов\\.",
            " \\| ➜ Легче купить и не париться - mineland\\.net",
            " \\|\\s*WHY DO YOU NEED MINELAND GOLD\\?\\s*",
            " \\| ● Gold -  is a stable currency, that helps you in mini-games!",
            " \\| ● Or spend it on effects and stand out from the players ;\\)",
            " \\| ● ⛂ it can be purchased, and found in cases\\.",
            " \\| ➜ It's easier to just buy it though - mineland\\.net"
    );

    public static final Message AD_REPORT = new Message(
            " \\|\\s*ОБНАРУЖИЛ НАРУШИТЕЛЯ\\?\\?\\?\\s*",
            " \\| ● Быстрее пиши команду \\/report ник и его накажут!",
            " \\| ● Жалобу рассмотрят в режиме реального времени!",
            " \\| ➜ Если у тебя появились вопросы, пиши - \\/ask вопрос\\s*",

            " \\|\\s*RULE BREAKER\\? CHEATER\\?\\s*",
            " \\|  You keep having fun while playing and suddenly\\.\\.\\. BOOM\\. A cheater\\.\\.\\.",
            " \\| ● What shall I do\\? Call those who can assist you - The Moderators ;\\)",
            " \\| ● They can help you more quickly the earlier you report the issue\\.",
            " \\| ● Your complaint will be double checked in realtime!",
            " \\|\\s*➜ Most importantly, keep calm and report the player using the command\\s*",
            " \\|\\s*- \\/report USERNAME",

            " \\|\\s*CHEATERS HERE\\? NEVER FEAR!\\s*",
            " \\|  So you're playing, and then\\.\\.\\.! A CHEATER, TWO, THREE\\.\\.\\.",
            " \\| ● What to do\\? Why not call the police, or in this case - the moderators ;\\)",
            " \\| ● The quicker you report, the faster they'll come\\.",
            " \\| ● Your report will be reviewed in real time!",
            " \\| ➜ Don't panic - \\/report NICKNAME\\s*"
    );

    public static final Message AD_EXPERT = new Message(
            " \\|  Освоился на Mineland и хочешь чего-то большего\\? ;\\)",
            " \\| ● С рангом Expert можно перевернуть ГОРЫ и даже НЕБО!",
            " \\| ● А всё почему\\? Идеальный баланс, доступная цена",
            " \\| ● и\\.\\.\\. множество полезных возможностей!",
            " \\| ➜ Переворачивай горы и небо - mineland\\.net",
            " \\|  Learned way around Mineland\\? Wanna look for something cooler \\? ;\\)",
            " \\| ● With the \\[EXPERT\\] rank you can move MOUNTAINS And even the SKIES!",
            " \\| ● What makes it so good\\? The perfect balance between an affordable price",
            " \\| ● and useful features! ;\\)",
            " \\| ➜ Move mountains and skies right now! - mineland\\.net"
    );

    public static final Message AD_HERO = new Message(
            " \\|  На Mineland играют настоящие ГЕРОИ!",
            " \\| ● Ранг \\[HERO\\] по сей день является мечтой многих\\.\\.\\.",
            " \\| ● А всё почему\\? Своими возможностями и бонусами",
            " \\| ● он привлекает каждого, но не каждый может быть им!",
            " \\| ➜ А ты уже можешь, не мечтай - mineland\\.net",
            " \\|  True HEROES play Mineland!",
            " \\| ● The \\[HERO\\] rank is still a dream of many\\.\\.\\.",
            " \\| ● Why's that\\? The opportunities and benefits it gives",
            " \\| ● are desired by many, but not everyone can be a Hero!",
            " \\| ➜ However, you can be the Hero of your dreams!\\s*",
            " \\| ➜ Make your dreams come true - mineland\\.net"
    );

    public static final Message AD_GAMER = new Message(
            " \\|  Не знаешь какой ранг выбрать на Mineland\\?",
            " \\| ● Начни с ранга Gamer по очень привлекательной цене\\.",
            " \\| ● Все его возможности здесь - \\/donate",
            " \\| ➜ Будь круче всех с нами - mineland\\.net",
            " \\|  Are you just beginning your Mineland journey\\?",
            " \\| ● Start with \\[GAMER\\] at a very good price\\.",
            " \\| ● You can find all features here - \\/donate",
            " \\| ➜ Join our family - mineland\\.net"
    );

    public static final Message AD_LICENSE = new Message(
            " \\|\\s*БЕСПЛАТНАЯ ЛИЦЕНЗИЯ\\s*",
            " \\| ● Голосуй, получай кейсы с наградами,",
            " \\| ● изумруды, ключик от Майнкрафт и\\.\\.\\.\\s*",
            " \\| ● Ранг EXPERT HERO Навсегда!",
            " \\| ➜ Узнай все подробности сейчас - \\/vote",
            " \\| ● Vote to get cases with rewards,",
            " \\| ● emeralds, a Minecraft license key and\\.\\.\\. ",
            " \\| ● a permanent rank up to EXPERT or HERO!",
            " \\| ➜ Find out all the details now - \\/vote"
    );

    public static final Message AD_COSMETICS = new Message(
            " \\|\\s*ПРЕОБРАЗИСЬ С КОСМЕТИКОЙ\\s*",
            " \\| ● Благодаря эффектам твой персонаж преобразится вмиг!",
            " \\| ● Чем более редкий эффект, тем он красивее - \\/cosmetics",
            " \\| ➜ Стань на свете всех милее - mineland\\.net\\s*",
            " \\|\\s*TRANSFORM YOURSELF WITH THE NEW COSMETICS\\s*",
            " \\|  Your character will stand out in a flash with the aid of cosmetics!",
            " \\| ● From the simplest to the most intricate and colourful ones\\.\\.\\.",
            " \\| ● The effect is more gorgeous the more valuable it is - \\/cosmetics",
            " \\| ● The best of the best already belong to our donators!",
            " \\| ➜ Be the fairest among them all - mineland\\.net\\s*"
    );

    public static final Message AD_CREATIVE_COMM = new Message(
            " \\| Будь в комьюнити КРЕАТИВ\\+",
            " \\| Там обсуждают закрытую информацию\\.",
            " \\| Заходи в Telegram: mineland\\.net\\/tg",
            " \\| Be in the community of CREATIVE\\+",
            " \\| Closed information is only there\\.",
            " \\| Join: mineland\\.net\\/discord\\/en"
    );

    public static final List<Message> MESSAGES = new ArrayList<>(
            Arrays.asList(REWARD_STORAGE, WELCOME_TO_MINELAND, DEV_MODE_JOIN, UNREAD_MAIL, UNANSWERED_ASKS, WORLD_INVITE, NEW_VIDEO, PUNISHMENT_BROADCAST,
                    DONATION, PLAYER_VOTED, STREAM, NEW_ASK, CREATIVE_CHAT, DONATE_CHAT, PM, PM_REPLY, PARTY_CHAT,
                    CC_DISABLED, LOGIN_CHECK, WORLD_MODE_CHANGE
            )
    );

    public static final List<Message> AD_MESSAGES = new ArrayList<>(
            Arrays.asList(AD_DELIMITER, AD_DELIMITER2,
                    AD_DONATE_UPGRADE,
                    AD_ACCOUNT_SECURITY,
                    AD_DISCORD,
                    AD_YOUTUBER,
                    AD_TELEGRAM,
                    AD_GOLD,
                    AD_REPORT,
                    AD_EXPERT,
                    AD_HERO,
                    AD_GAMER,
                    AD_LICENSE,
                    AD_COSMETICS,
                    AD_CREATIVE_COMM
            )
    );

    public static void updateMessages() {
        Configuration.GeneralMessages messages = Configuration.GENERAL_MESSAGES;
        REWARD_STORAGE.setActive(messages.REWARD_STORAGE.get());
        WELCOME_TO_MINELAND.setActive(messages.WELCOME_TO_MINELAND.get());
        UNREAD_MAIL.setActive(messages.UNREAD_MAIL.get());
        UNANSWERED_ASKS.setActive(messages.UNANSWERED_ASKS.get());
        WORLD_INVITE.setActive(Configuration.CREATIVE.WORLD_INVITE.get());
        NEW_VIDEO.setActive(messages.NEW_VIDEO.get());
        PUNISHMENT_BROADCAST.setActive(messages.PUNISHMENT_BROADCAST.get());
        DONATION.setActive(messages.DONATION.get());
        PLAYER_VOTED.setActive(messages.PLAYER_VOTED.get());
        STREAM.setActive(messages.STREAM.get());
        NEW_ASK.setActive(messages.NEW_ASK.get());
        LOGIN_CHECK.setActive(messages.LOGIN_CHECK.get());
    }
}
