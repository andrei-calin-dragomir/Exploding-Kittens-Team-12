package softwaredesign.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;



public class SplashScreenController {

    @FXML
    private void executeAction(ActionEvent event){
        Node node = (Node) event.getSource() ;
        String data = (String) node.getUserData();
        System.out.println(data);
    }
}
