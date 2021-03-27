package softwaredesign.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import softwaredesign.client.ClientProgram;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class SplashScreenController {

    @FXML
    private void exitGame(){
        System.exit(0);
    }

    @FXML
    private void handleAction(ActionEvent event){
        Node node = (Node) event.getSource() ;
        String data = (String) node.getUserData();
        try {
            ClientProgram.guiInputHandler(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
