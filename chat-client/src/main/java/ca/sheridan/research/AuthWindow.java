package ca.sheridan.research;

import ca.sheridan.research.protocol.PacketDecoder;
import ca.sheridan.research.protocol.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AuthWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(5));
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(75);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(25);

        pane.getColumnConstraints().addAll(c1, c2);

        TextField ip = new TextField();
        ip.setText("localhost");
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

        connect.setOnAction(event -> connect(primaryStage, ip, port, username));
    }

    private void connect(Stage primaryStage, TextField ipText, TextField portText, TextField usernameText) {
        int port;
        try {
            port = Integer.valueOf(portText.getText());
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid connection details");
            alert.setContentText("Please, enter correct server port.");
            alert.showAndWait();
            return;
        }

        if (usernameText.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid connection details");
            alert.setContentText("Please, enter correct username.");
            alert.showAndWait();
            return;
        }

        if (ipText.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid connection details");
            alert.setContentText("Please, enter correct IP-address.");
            alert.showAndWait();
            return;
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        ChannelHandler handler = new ChannelHandler();
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addFirst(new PacketDecoder());
                ch.pipeline().addFirst(new PacketEncoder());
                ch.pipeline().addLast(handler);
            }
        });


        ChannelFuture futureMain = b.connect(ipText.getText(), port);
        try {
            futureMain.addListener(future -> Platform.runLater(() -> {
                if (future.isSuccess()) {
                    ChatWindow chatWindow = new ChatWindow();
                    chatWindow.start(usernameText.getText(), handler);
                    primaryStage.hide();
                    futureMain.channel().closeFuture().addListener((ChannelFutureListener) future1 -> Platform.runLater(chatWindow::close));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Unable to connect");
                    alert.setHeaderText("Connection failed");
                    alert.setContentText(future.cause().toString());
                    alert.showAndWait();
                }
            }));
            futureMain.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        futureMain.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            workerGroup.shutdownGracefully();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connection to server lost");
                alert.setContentText("Disconnected from the server");
                alert.showAndWait();

                primaryStage.show();
            });
        });
    }
}
