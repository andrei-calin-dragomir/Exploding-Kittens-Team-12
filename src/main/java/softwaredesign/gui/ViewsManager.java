package softwaredesign.gui;

import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

class ViewsManager {
    static private HashMap<SceneName, String> scenes = new HashMap<>();
    public enum SceneName {
        SPLASH_SCREEN, CHOOSE_NAME, ROOM_SELECTION, GAME_VIEW, SERVER_CONNECT, OFFLINE_SETTINGS, CREATE_ROOM, ROOM_SCREEN, DECK_OPTIONS, DECK_VIEW, SEE_THE_FUTURE_DIALOG
    }
    static{
        scenes.put(SceneName.SPLASH_SCREEN,"src/main/resources/fxml/splashScreen.fxml");
        scenes.put(SceneName.CHOOSE_NAME,"src/main/resources/fxml/chooseName.fxml");
        scenes.put(SceneName.ROOM_SELECTION,"src/main/resources/fxml/roomSelection.fxml");
        scenes.put(SceneName.GAME_VIEW,"src/main/resources/fxml/gameView.fxml");
        scenes.put(SceneName.SERVER_CONNECT,"src/main/resources/fxml/serverConnect.fxml");
        scenes.put(SceneName.OFFLINE_SETTINGS,"src/main/resources/fxml/offlineSettings.fxml");
        scenes.put(SceneName.CREATE_ROOM,"src/main/resources/fxml/createRoom.fxml");
        scenes.put(SceneName.ROOM_SCREEN,"src/main/resources/fxml/roomScreen.fxml");
        scenes.put(SceneName.DECK_OPTIONS,"src/main/resources/fxml/deckOptions.fxml");
        scenes.put(SceneName.DECK_VIEW,"src/main/resources/fxml/deckView.fxml");
        scenes.put(SceneName.SEE_THE_FUTURE_DIALOG,"src/main/resources/fxml/seeTheFutureDialog.fxml");
    }

    static void loadScene(SceneName name) throws Exception{
        URL newUrl = new File(scenes.get(name)).toURI().toURL();
        Pane newScene = FXMLLoader.load(newUrl);
        Gui.mainScene.setRoot(newScene);
        Gui.mainScene.getRoot().requestFocus();
    }

    static void loadInNewWindow(SceneName name){
        try {
            URL newUrl = new File(scenes.get(name)).toURI().toURL();
            Parent root1 = FXMLLoader.load(newUrl);
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}