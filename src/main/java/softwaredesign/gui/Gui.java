package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import softwaredesign.client.ClientProgram;

import java.io.File;
import java.net.URL;

public class Gui extends Application {

    static String latestMessage = "";
    static Scene mainScene;

    AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void start(){
            System.out.println("Game loop started!!");
            super.start();
        }

        @Override
        public void handle(long now) {
            if(!ClientProgram.serverMessage.isEmpty()){
                latestMessage = ClientProgram.serverMessage.removeFirst();
            }
        }
    };

    @Override
    public void start(Stage mainStage) throws  Exception{

//        ClientProgram.startClient("127.0.0.1");
//        gameLoop.start();
//        gameLoop.stop();

        URL url = new File("src/main/resources/fxml/splashScreen.fxml").toURI().toURL();
        URL urlCSS = new File("src/main/resources/css/fxml.css").toURI().toURL();
        URL urlFont = new File("/resources/fonts/bebas.ttf").toURI().toURL();
        Font.loadFont(getClass().getResourceAsStream(urlFont.toExternalForm()), 25);
        Parent root = FXMLLoader.load(url);
        mainScene = new Scene(root);

        mainStage.setOnCloseRequest(event -> {
            ClientProgram.killConnectionSafely();
        });

        mainScene.getStylesheets().add(urlCSS.toExternalForm());
        mainStage.setTitle("Exploding Kittens");
        mainStage.setScene(mainScene);
        mainStage.show();
    }
    public static void main(String[] args) { launch(args); }
}
