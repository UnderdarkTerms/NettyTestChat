package ca.sheridan.research;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatWindow extends Application {
    ObservableList<String> items;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox pane = new VBox();
        Scene scene = new Scene(pane, 600, 600);

        ListView<String> history = new ListView<>();

        items = FXCollections.observableArrayList(
                "Single", "Double", "Suite", "Family App");

        history.setItems(items);
        history.setPrefWidth(Double.MAX_VALUE);
        pane.getChildren().add(history);

        HBox box = new HBox();

        TextField input = new TextField();
        Button send = new Button("Send");
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
}
