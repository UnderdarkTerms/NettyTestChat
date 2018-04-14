package ca.sheridan.research;

import ca.sheridan.research.protocol.Packet;
import ca.sheridan.research.protocol.PacketDecoder;
import ca.sheridan.research.protocol.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Random;

public class AuthWindow extends Application {
    public static ChannelHandlerContext ctx;
    public static ObservableList<String> messages;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5));
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(75);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(25);

        pane.getColumnConstraints().addAll(c1, c2);

        TextField ip = new TextField();
        ip.setText("10.48.33.108");
        ip.setPadding(new Insets(2));
        pane.add(ip, 0, 0);

        TextField port = new TextField();
        port.setText("8080");
        port.setPadding(new Insets(2));
        pane.add(port, 1, 0);


        TextField username = new TextField();
        username.setPadding(new Insets(2));
        pane.add(username, 0, 1);
        GridPane.setColumnSpan(username, 2);

        Button connect = new Button("Connect");
        connect.setPrefWidth(Double.MAX_VALUE);

        pane.add(connect, 0, 2);
        GridPane.setColumnSpan(connect, 2);

        Scene root = new Scene(pane, 300, 100);

        primaryStage.setTitle("Chat Login");
        primaryStage.setScene(root);
        primaryStage.show();

        connect.setOnAction(event -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();

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
                            ctx = channelHandlerContext;
                            messages = FXCollections.observableArrayList();
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
                            messages.add(String.format("%s> %s", ((Packet) o).getUsername(), ((Packet) o).getMessage()));
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
            ChannelFuture futureMain = b.connect(ip.getText(), Integer.valueOf(port.getText()));
            try {
                futureMain.addListener(future -> {
                    if (future.isSuccess()) {
                        Platform.runLater(() -> {
                            ChatWindow window = new ChatWindow();


                            try {
                                window.start(new Stage(), username.getText());
                                primaryStage.hide();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Unable to connect");
                            alert.setHeaderText("Connection failed");
                            alert.setContentText(future.toString());
                            alert.showAndWait();
                        });
                    }
                });
                futureMain.sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            futureMain.channel().closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
        });

    }

}
