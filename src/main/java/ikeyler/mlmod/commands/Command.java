package ikeyler.mlmod.commands;

import java.util.List;

public abstract class Command {
    protected final String name;
    protected boolean util;
    public abstract void execute(List<String> args);
    public Command(String name, boolean util) {
        this.name = name;
        this.util = util;
    }
    public boolean isUtil() {
        return this.util;
    }
    public String getName() {
        return this.name;
    }
}