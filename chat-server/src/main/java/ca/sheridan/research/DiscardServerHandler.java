package ca.sheridan.research;

import ca.sheridan.research.protocol.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    public static ChannelHandlerContext clientctx;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        // Discard the received data silently.
        Packet packet = (Packet) msg; // (3)
        System.out.println(packet.getUsername() + "> " + packet.getMessage());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        System.err.println("Disconneced: " + cause.getMessage());
        ctx.close();
    }

    public ChannelHandlerContext getClientctx() {
        return clientctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        clientctx = ctx;

        System.out.println("channelActive");
        super.channelActive(ctx);
    }
}