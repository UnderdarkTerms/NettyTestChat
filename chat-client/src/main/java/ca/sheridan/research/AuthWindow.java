package ca.sheridan.research;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AuthWindow extends Application {

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
        ip.setPadding(new Insets(2));
        pane.add(ip, 0, 0);

        TextField port = new TextField();
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
    }
}
