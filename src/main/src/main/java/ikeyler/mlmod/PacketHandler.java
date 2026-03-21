package ikeyler.mlmod;

import ikeyler.mlmod.cfg.Configuration;
import ikeyler.mlmod.util.ModUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final String packetName = "packet_interceptor";

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        NetworkManager manager = event.getManager();
        if (manager == null || !Configuration.CREATIVE.DEV_NIGHT_MODE.get()) return;
        ChannelPipeline pipeline = manager.channel().pipeline();
        if (pipeline.get(packetName) == null) {
            pipeline.addBefore("packet_handler", packetName, new ChannelInboundHandlerAdapter() {
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (msg instanceof SPacketTimeUpdate && ModUtils.NIGHT_DEV_MODE) {
                        if (mc.world != null) {
                            mc.world.provider.setWorldTime(Configuration.CREATIVE.DEV_NIGHT_MODE_TIME);
                            return;
                        }
                    }
                    super.channelRead(ctx, msg);
                }
            });
        }
    }
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        NetworkManager manager = event.getManager();
        if (manager != null) {
            ChannelPipeline pipeline = manager.channel().pipeline();
            if (pipeline.get(packetName) != null) {
                try {
                    pipeline.remove(packetName);
                } catch (Exception e) { Main.logger.error("could not remove packet handler:", e); }
            }
        }
    }
}
