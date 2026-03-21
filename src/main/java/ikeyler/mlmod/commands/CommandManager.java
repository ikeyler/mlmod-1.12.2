package ikeyler.mlmod.commands;

import ikeyler.mlmod.Main;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    public CommandManager() {
        this.register();
    }
    public void register() {
        for (Command command : Commands.commandList)
            commands.put(command.getName().toLowerCase(), command);
        Main.logger.info("registered {} commands", commands.size());
    }
    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
}
