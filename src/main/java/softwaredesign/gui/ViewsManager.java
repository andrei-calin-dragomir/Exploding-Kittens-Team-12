package softwaredesign.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.javatuples.Tuple;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ViewsManager {
    static private HashMap<String, Pane> screenMap = new HashMap<>();
    static private HashMap<String, String> views = new HashMap<>();
    static{
        views.put("room_selection","src/main/resources/fxml/roomSelection.fxml");
        views.put("splash_screen","src/main/resources/fxml/splashScreen.fxml");
        views.put("game_view","src/main/resources/fxml/gameView.fxml");
        views.put("server_connect","src/main/resources/fxml/serverConnect.fxml");
        views.put("offline_settings","src/main/resources/fxml/offlineSettings.fxml");
        for (Map.Entry<String, String> entry : views.entrySet()) {
            try {
                URL newUrl = new File(entry.getValue()).toURI().toURL();
                Pane pane = FXMLLoader.load(newUrl);
                ViewsManager.addScreen(entry.getKey(),pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static private Scene main;

    public ViewsManager(Scene main) {
        this.main = main;
    }

    static protected void addScreen(String name, Pane pane){
        screenMap.put(name, pane);
    }

    static protected void removeScreen(String name){
        screenMap.remove(name);
    }

    static protected void activate(String name){
        main.setRoot( screenMap.get(name) );
    }

    static public Scene getCurrentScene() {
        return main;
    }
}