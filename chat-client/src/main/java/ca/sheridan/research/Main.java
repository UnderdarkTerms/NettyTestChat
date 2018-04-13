package ca.sheridan.research;

import ca.sheridan.research.protocol.Packet;
import ca.sheridan.research.protocol.PacketDecoder;
import ca.sheridan.research.protocol.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static ChannelHandlerContext serverContext;


    public static void main(String[] args) throws InterruptedException, IOException {
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addFirst(new PacketDecoder());
                    ch.pipeline().addFirst(new PacketEncoder());

                    ch.pipeline().addLast(new ChannelInboundHandler() {
                        @Override
                        public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }

                        @Override
                        public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }

                        @Override
                        public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                            serverContext = channelHandlerContext;
                            System.out.println("SYSTEM> Connected to the server");
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }

                        @Override
                        public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                            if (!(o instanceof Packet)) {
                                System.out.println("Unknown packet: " + o);
                                return;
                            }
                            System.out.printf("%s> %s\r\n", ((Packet) o).getUsername(), ((Packet) o).getMessage());
                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }

                        @Override
                        public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

                        }

                        @Override
                        public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {

                        }

                        @Override
                        public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }

                        @Override
                        public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {

                        }
                    });


                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                System.out.println("Socket Disconnected");
                Runtime.getRuntime().halt(0);
            });


            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String username = "UnderdarkTerms";
            while (true) {
                String msg = reader.readLine();


                serverContext.writeAndFlush(new Packet(username, username))
                        .addListener((ChannelFutureListener) channelFuture -> System.out.println("C> " + msg + (!channelFuture.isSuccess() ? (" (" + channelFuture + ")") : "")));
            }
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}
