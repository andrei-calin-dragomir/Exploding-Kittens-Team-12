package softwaredesign.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class InitGui extends Application {

    @Override
    public void start(Stage mainStage) throws  Exception{
        URL url = new File("src/main/resources/fxml/splashScreen.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root);
        mainStage.setTitle("Exploding Kittens");
        mainStage.setScene(scene);
        mainStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
