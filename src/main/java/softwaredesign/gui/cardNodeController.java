package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class cardNodeController {

    @FXML
    public ImageView favor1;

    public void removeCard(){
        favor1.getParent().getChildrenUnmodifiable().remove(0);
    }

}
