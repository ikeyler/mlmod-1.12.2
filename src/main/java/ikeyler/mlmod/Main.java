package ikeyler.mlmod;

import ikeyler.mlmod.messages.MessageManager;
import ikeyler.mlmod.messages.MessageCollector;
import ikeyler.mlmod.messages.Messages;
import ikeyler.mlmod.missedmessages.MissedMessagesManager;
import ikeyler.mlmod.variables.VarCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main
{
    public static final Logger logger = LogManager.getLogger();
    public static final MessageManager messageManager = new MessageManager();
    public static final MessageCollector messageCollector = new MessageCollector();
    public static final VarCollector varCollector = new VarCollector();
    public static final MissedMessagesManager missedMessagesManager = new MissedMessagesManager();
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.register(new PacketHandler());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new Keybinds());
        MinecraftForge.EVENT_BUS.register(new TitleListener());
        Keybinds.register();
    }
    @EventHandler
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        Messages.reloadMessages();
    }
}
