package ikeyler.mlmod.messages;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.cfg.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ikeyler.mlmod.util.TextUtil.MOD_PREFIX;

public class MessageCollector {
    private final File dataFile = new File("mlmodData.txt");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<String> data = new ArrayList<>();

    public MessageCollector() {
        try {
            if (dataFile.createNewFile()) Main.logger.info("created data file: {}", dataFile.getName());
        }
        catch (IOException e) {
            Main.logger.error("could not create data file:", e);
        }
    }
    public void addEntry(MessageType type, String player, String data) {
        if (!Configuration.GENERAL.MESSAGE_COLLECTOR.get()) return;
        // timestamp type | player (optional): data
        String timestamp = LocalDateTime.now().format(formatter);
        StringBuilder entry = new StringBuilder();
        entry.append(timestamp).append(" ");
        entry.append(type.getName()).append(" | ");
        if (player != null) entry.append(player).append(": ");
        entry.append(data);
        writeLine(entry.toString());
    }

    private void writeLine(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            Main.logger.error("error while writing file:", e);
        }
    }
    public List<String> readAll() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (Exception e) {
            Main.logger.error("error while reading file:", e);
        }
        return lines;
    }
    public void findAsync(String query, MessageType type, int limit, String source) {
        CompletableFuture.supplyAsync(() -> {
            List<String> lines = readAll();
            if (type != null) lines = lines.stream().filter(l -> l.split(" ")[2].equalsIgnoreCase(type.getName())).collect(Collectors.toList());
            if (query != null) lines = lines.stream().filter(l -> l.split("\\|", 2)[1].toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
            if (limit > 0 && lines.size() >= limit) lines = lines.subList(lines.size()-limit, lines.size());
            return lines;
        }).thenAcceptAsync(res -> {data = res; searchCompleted(source);});
    }
    private void searchCompleted(String source) {
        if (source.equalsIgnoreCase("mc")) {
            Minecraft mc = Minecraft.getMinecraft();
            if (!data.isEmpty()) {
                TextComponentString component = new TextComponentString("");
                component.appendText((MOD_PREFIX)).appendSibling(new TextComponentTranslation("mlmod.messages.collector.search_found", data.size()));
                component.appendText("\n");
                data.stream().map(s -> {String[] parts = s.split("\\|", 2); return "§7- §7" + parts[0] + "§f" + parts[1] + "\n";}).forEach(component::appendText);
                mc.player.sendMessage(component);
                return;
            }
            mc.player.sendMessage(new TextComponentTranslation("mlmod.messages.collector.search_not_found"));
        }
    }
}
