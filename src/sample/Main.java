package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Main.class.getResource("view/sample.fxml"));
        //FlowPane root = new FlowPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("view/sample.css").toExternalForm());
        primaryStage.setTitle("MAKANSG: The best food delivery app ever!");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(("file:src/LOGO.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
