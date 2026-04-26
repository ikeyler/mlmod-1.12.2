package ikeyler.mlmod.commands;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.commands.Commands.*;
import ikeyler.mlmod.commands.UtilCommands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandManager {
    private final Map<String, ICommand> commands = new HashMap<>();
    public CommandManager() {
        this.register();
    }
    public void register() {
        register(new ConfigCommand());
        register(new MessagesCommand());
        register(new IgnoreListCommand());
        register(new SoundCommand());
        register(new IgnoreCommand());
        register(new HeadCommand());
        register(new NightModeCommand());
        register(new ItemCommand());
        register(new PlayerInteractCommand());
        register(new VarsCommand());
        register(new VarSaveCommand());
        register(new RemoveVarCommand());
        register(new GetVarCommand());
        register(new ToggleMsgCollectorCommand());
        register(new ShowMessageAdsCommand());
        register(new CopyTextCommand());
        register(new ReloadMessagesCommand());
        register(new HelpCommand());
        register(new VarCommand());
        register(new TextCommand());
        register(new NumberCommand());
        Main.logger.info("registered {} commands", commands.size());
    }
    public void register(ICommand command) {
        if (command == null || command.getName() == null) return;
        commands.put(command.getName().toLowerCase(), command);
    }
    public Optional<ICommand> getCommand(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }
}
