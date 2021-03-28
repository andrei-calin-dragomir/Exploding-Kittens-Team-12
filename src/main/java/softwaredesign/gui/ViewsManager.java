package softwaredesign.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class ViewsManager {
    static private HashMap<SceneName, String> scenes = new HashMap<>();
    public enum SceneName {
        SPLASH_SCREEN, CHOOSE_NAME, ROOM_SELECTION, GAME_VIEW
    }
    static{
        scenes.put(SceneName.SPLASH_SCREEN,"src/main/resources/fxml/splashScreen.fxml");
        scenes.put(SceneName.CHOOSE_NAME,"src/main/resources/fxml/chooseName.fxml");
        scenes.put(SceneName.ROOM_SELECTION,"src/main/resources/fxml/roomSelection.fxml");
        scenes.put(SceneName.GAME_VIEW,"src/main/resources/fxml/gameView.fxml");
    }

    static void loadScene(SceneName name) throws Exception{
        URL newUrl = new File(scenes.get(name)).toURI().toURL();
        Pane newScene = FXMLLoader.load(newUrl);
        Gui.mainScene.setRoot(newScene);
    }
}