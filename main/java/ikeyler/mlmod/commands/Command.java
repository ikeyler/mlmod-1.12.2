package ikeyler.mlmod.commands;

import net.minecraft.client.Minecraft;

import java.util.List;

public abstract class Command implements ICommand {
    protected final String name;
    private boolean enabled = true;
    public abstract void execute(List<String> args);
    protected final Minecraft mc = Minecraft.getMinecraft();
    public Command(String name) {
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