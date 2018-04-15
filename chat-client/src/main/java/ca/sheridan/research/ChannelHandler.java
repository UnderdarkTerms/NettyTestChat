package ca.sheridan.research;

import ca.sheridan.research.protocol.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChannelHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private ObservableList<String> messages;


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        ctx = channelHandlerContext;
        messages = FXCollections.observableArrayList();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (!(o instanceof Packet)) {
            throw new IllegalArgumentException("Unknow packet type: " + o);
        }

        messages.add(String.format("%s> %s", ((Packet) o).getUsername(), ((Packet) o).getMessage()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Network Exception: " + cause.getMessage());
        if (!ctx.channel().isOpen())
            ctx.channel().close();
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public ObservableList<String> getMessages() {
        return messages;
    }
}
