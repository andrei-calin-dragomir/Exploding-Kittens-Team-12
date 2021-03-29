package softwaredesign.gui;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SeeTheFutureDialogController implements Initializable {

    @FXML
    private HBox cardBox;

    @FXML
    private Button exitButton;

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    void loadCards(){
        //format of the string: SEETHEFUTURE card card card
//        cardBox.getChildren().clear();
        for(String cardType : GameViewController.cardsToShow.split(" ")){
            if(!cardType.equals("SEETHEFUTURE")) {
                Image img = new Image(CardHelper.getCardImageUrl(cardType));
                ImageView iv = new ImageView(img);
                iv.setUserData(cardType);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setFitHeight(260);

                cardBox.getChildren().add(iv);
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCards();
        System.out.println("CARDS TO SHOW = "+ GameViewController.cardsToShow);
    }
}
