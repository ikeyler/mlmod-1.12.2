package ikeyler.mlmod;

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

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        NetworkManager manager = event.getManager();
        ChannelPipeline pipeline = manager.channel().pipeline();
        pipeline.addBefore("packet_handler", "packet_interceptor", new ChannelInboundHandlerAdapter() {
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof SPacketTimeUpdate && ModUtils.NIGHT_DEV_MODE) {
                    mc.world.setWorldTime(18000L);
                    return;
                }
                super.channelRead(ctx, msg);
            }
        });
    }
}
