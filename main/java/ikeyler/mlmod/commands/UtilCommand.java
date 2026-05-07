package ikeyler.mlmod.commands;

import java.util.List;

public abstract class UtilCommand implements ICommand {
    protected final String name;
    private boolean enabled = true;
    public abstract void execute(List<String> args);
    public UtilCommand(String name) {
        this.name = name;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
