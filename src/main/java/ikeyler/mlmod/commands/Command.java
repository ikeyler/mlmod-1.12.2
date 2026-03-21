package ikeyler.mlmod.commands;

import java.util.List;

public interface Command {
    String getName();
    void execute(List<String> args);
    boolean isUtil(); // hide cmd in chat history
}