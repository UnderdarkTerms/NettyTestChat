package ca.sheridan.research;

import ca.sheridan.research.protocol.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

@ChannelHandler.Sharable
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Discard the received data silently.
        Packet packet = (Packet) msg;
        if (packet.getUsername().isEmpty()
                || packet.getMessage().isEmpty()) {
            System.err.println("Dropped incorrect packet: {username: " + packet.getMessage() + "; " + packet.getMessage() + "}");
            return;
        }
        clients.writeAndFlush(packet);
        System.out.println(packet.getUsername() + "> " + packet.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        System.err.println("Disconnected: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
        super.channelActive(ctx);
        clients.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
        clients.remove(ctx.channel());
        super.channelInactive(ctx);
    }
}