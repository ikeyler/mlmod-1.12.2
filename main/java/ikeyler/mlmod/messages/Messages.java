package ikeyler.mlmod.messages;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.ModUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Messages {
    private static final String messagesFilename = "mlmodmessages.txt";
    private static List<String> messageData = new ArrayList<>();
    public static final Map<String, List<Message>> messagesMap = new HashMap<>();
    private static List<String> readResourceFile() {
        InputStream is = Messages.class.getClassLoader().getResourceAsStream(messagesFilename);
        if (is == null) return new ArrayList<>();
        try (InputStreamReader isr = new InputStreamReader(is)) {
            BufferedReader br = new BufferedReader(isr);
            return br.lines().collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void createMessagesFile() {
        try {
            File file = new File(messagesFilename);
            file.createNewFile();
            Files.write(file.toPath(), readResourceFile());
            Main.logger.info("created messages file");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static int reloadMessages() {
        if (!Files.exists(Paths.get(messagesFilename)))
            createMessagesFile();
        messageData = ModUtils.readAllLines(new File(messagesFilename));
        messagesMap.clear();
        int loaded = 0;
        for (String key : getMessageKeys()) {
            List<Message> messages = getMessages(key);
            messagesMap.put(key, messages);
            loaded += messages.size();
        }
        Main.logger.info("loaded {} messages", loaded);
        updateMessages();
        Main.messageManager.update();
        return loaded;
    }
    public static Message getMessage(String str) {
        for (List<Message> values : messagesMap.values()) {
            for (Message message : values) {
                if (message.matches(str)) {
                    return message;
                }
            }
        }
        return null;
    }
    public static List<Message> getMessages(String key) {
        List<Message> messages = new ArrayList<>();
        for (String line : messageData) {
            String[] split = line.split(":", 2);
            if (split.length > 1 && split[0].equalsIgnoreCase(key))
                messages.add(new Message(split[1]));
        }
        return messages;
    }
    public static Set<String> getMessageKeys() {
        Set<String> keys = new HashSet<>();
        for (String line : messageData) {
            String[] split = line.split(":", 2);
            if (split.length > 1) keys.add(split[0].toUpperCase());
        }
        return keys;
    }
    public static boolean contains(String key, String str) {
        return contains(key, getMessage(str));
    }
    public static boolean contains(String key, Message message) {
        if (!messagesMap.containsKey(key)) return false;
        return messagesMap.get(key).contains(message);
    }
    public static void updateMessages() {
        Configuration.GeneralMessages messages = Configuration.GENERAL_MESSAGES;
        Map<List<Message>, Boolean> configMap = new HashMap<>();
        configMap.put(messagesMap.get("REWARD_STORAGE"), messages.REWARD_STORAGE.get());
        configMap.put(messagesMap.get("WELCOME_TO_MINELAND"), messages.WELCOME_TO_MINELAND.get());
        configMap.put(messagesMap.get("UNREAD_MAIL"), messages.UNREAD_MAIL.get());
        configMap.put(messagesMap.get("UNANSWERED_ASKS"), messages.UNANSWERED_ASKS.get());
        configMap.put(messagesMap.get("WORLD_INVITE"), Configuration.CREATIVE.WORLD_INVITE.get());
        configMap.put(messagesMap.get("NEW_VIDEO"), messages.NEW_VIDEO.get());
        configMap.put(messagesMap.get("PUNISHMENT_BROADCAST"), messages.PUNISHMENT_BROADCAST.get());
        configMap.put(messagesMap.get("DONATION"), messages.DONATION.get());
        configMap.put(messagesMap.get("PLAYER_VOTED"), messages.PLAYER_VOTED.get());
        configMap.put(messagesMap.get("NEW_ASK"), messages.NEW_ASK.get());
        configMap.put(messagesMap.get("LOGIN_CHECK"), messages.LOGIN_CHECK.get());
        for (List<Message> key : configMap.keySet()) {
            if (key == null) continue;
            key.forEach(message -> message.setActive(configMap.get(key)));
        }
    }
}
