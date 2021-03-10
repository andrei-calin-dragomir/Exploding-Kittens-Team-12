package softwaredevelopmentvu.explodingkittens.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private HBox enemyHBox;

    @FXML
    private HBox announcementHBox;

    @FXML
    private HBox decksHBox;

    @FXML
    private ImageView mainDeck;

    @FXML
    private ImageView discardDeck;

    @FXML
    private HBox playerHandHBox;

//    public void clear(){
//        playerHandHBox.getChildren().remove(2);
//    }

//    public void draw(){
//
//    }
    public void playCard(){
        ImageView imgv = (ImageView) playerHandHBox.getChildren().get(0);
        playerHandHBox.getChildren().remove(0);
        Image res = imgv.getImage();
        updateDiscardDeck(res);
    }

    public void updateDiscardDeck(Image img){
        discardDeck.setImage(img);
    }

    public void handleMainDeckMouseClick(){
        mainDeck.setImage(new Image("file:src/main/resources/images/normal/DefuseCard2.png"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
