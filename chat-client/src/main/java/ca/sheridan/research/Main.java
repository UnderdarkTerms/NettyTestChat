package ca.sheridan.research;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
                            ByteBuf buf = ((ByteBuf) o);
                            byte[] msg = new byte[buf.readInt()];
                            buf.readBytes(msg);
                            System.out.println("S> " + new String(msg, "UTF-8"));
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

                    ch.pipeline().addFirst(new MessageToByteEncoder<String>() {
                        @Override
                        protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf) throws Exception {
                            byte[] bytes = s.getBytes("UTF-8");
                            byteBuf.writeInt(bytes.length);
                            byteBuf.writeBytes(bytes);
                        }
                    });
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
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
            while (true) {
                String msg = reader.readLine();
                ByteBuf buf = serverContext.alloc().buffer();
                buf.writeInt(msg.getBytes().length);
                buf.writeBytes(msg.getBytes());
                serverContext.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("C> " + msg + (!channelFuture.isSuccess() ? (" (" + channelFuture + ")") : ""));
                    }
                });
            }
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}
