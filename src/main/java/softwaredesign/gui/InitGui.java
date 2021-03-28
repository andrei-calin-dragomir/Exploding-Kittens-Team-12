package softwaredesign.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class InitGui extends Application {

    @Override
    public void start(Stage mainStage) throws  Exception{
        URL url = new File("src/main/resources/fxml/splashScreen.fxml").toURI().toURL();
        URL urlCSS = new File("src/main/resources/fxml/fxml.css").toURI().toURL();
//        ClientProgram.startClient();
        Parent root = FXMLLoader.load(url);
        Scene splashScreenScene = new Scene(root);
        ViewsManager viewsManager = new ViewsManager(splashScreenScene);
        splashScreenScene.getStylesheets().add(urlCSS.toExternalForm());
        mainStage.setTitle("Exploding Kittens");
        mainStage.setScene(splashScreenScene);
        mainStage.show();
    }
    public static void main(String[] args) { launch(args); }
}
