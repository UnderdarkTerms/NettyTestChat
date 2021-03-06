package ca.sheridan.research;

import ca.sheridan.research.protocol.Packet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatWindow {
    private Stage primaryStage = new Stage();

    public void start(String userName, ChannelHandler handler) {
        VBox pane = new VBox();
        Scene scene = new Scene(pane, 600, 600);

        ListView<String> history = new ListView<>();

        history.setItems(handler.getMessages());
        history.setPrefWidth(Double.MAX_VALUE);
        pane.getChildren().add(history);

        HBox box = new HBox();

        TextField input = new TextField();
        Button send = new Button("Send");
        EventHandler<ActionEvent> eventHandler = event -> {
            if (!input.getText().isEmpty()) {
                handler.getCtx().writeAndFlush(new Packet(userName, input.getText()));
                input.setText("");
            }
        };
        send.setOnAction(eventHandler);
        input.setOnAction(eventHandler);
        send.setPrefWidth(100);
        input.prefWidthProperty().bind(scene.widthProperty().subtract(send.prefWidthProperty()));

        box.getChildren().add(input);
        box.getChildren().add(send);


        pane.getChildren().add(box);
        history.prefHeightProperty().bind(scene.heightProperty().subtract(box.heightProperty()));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Window");
        primaryStage.show();
    }

    public void close() {
        System.out.println("Close Request");
        primaryStage.close();
    }
}
