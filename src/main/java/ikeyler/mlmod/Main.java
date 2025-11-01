package ikeyler.mlmod;

import ikeyler.mlmod.messages.Manager;
import ikeyler.mlmod.messages.MessageCollector;
import ikeyler.mlmod.messages.Messages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main
{
    public static final Logger logger = LogManager.getLogger();
    public static final Manager messageManager = new Manager();
    public static final MessageCollector messageCollector = new MessageCollector();
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventListener());
        messageManager.addMessages(Messages.MESSAGES);
        messageManager.addMessages(Messages.AD_MESSAGES);
        Keybinds.register();
    }
}
