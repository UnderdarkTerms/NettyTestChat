package ca.sheridan.research;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

    public static ChannelHandlerContext clientctx;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException { // (2)
        // Discard the received data silently.
        ByteBuf buff = (ByteBuf) msg; // (3)
        byte[] stringBytes = new byte[buff.readInt()];
        buff.readBytes(stringBytes);
        String s = new String(stringBytes, "UTF-8");
        System.out.println("C> " + s);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
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