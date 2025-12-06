package ikeyler.mlmod;

import ikeyler.mlmod.util.ModUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PacketHandler {
    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientConnected(ClientPlayerNetworkEvent.LoggedInEvent event) {
        NetworkManager manager = event.getNetworkManager();
        ChannelPipeline pipeline = manager.channel().pipeline();
        pipeline.addBefore("packet_handler", "packet_interceptor", new ChannelInboundHandlerAdapter() {
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof SUpdateTimePacket && ModUtils.NIGHT_DEV_MODE) {
                    mc.level.setDayTime(18000L);
                    return;
                }
                super.channelRead(ctx, msg);
            }
        });
    }
}
