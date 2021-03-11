package softwaredevelopmentvu.explodingkittens.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import softwaredevelopmentvu.explodingkittens.game.Game;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private HBox enemyHBox;

    @FXML
    private Text announcementText;

    @FXML
    private HBox decksHBox;

    @FXML
    private ImageView mainDeck;

    @FXML
    private ImageView discardDeck;

    @FXML
    public GridPane cardsGridPane;

    private void playCard(ImageView iv){
        cardsGridPane.getChildren().remove(iv);
        updateDiscardDeck(iv.getImage());
    }

    public void pickCard(){
        addCardToHand(new Image("file:src/main/resources/images/normal/NopeCard1.png"));
    }

    private void addCardToHand(Image img){
        int lastColumn = cardsGridPane.getColumnCount();
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setFitHeight(200);
        iv.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            Node src = (Node) e.getSource();
            playCard((ImageView)src);
            updateAnnouncement("poop");
        });
        //zoom on hover
        iv.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> iv.setFitHeight(250));
        iv.addEventFilter(MouseEvent.MOUSE_EXITED,  e -> iv.setFitHeight(200));

        cardsGridPane.add(iv,lastColumn+1,0);
    }

    private void updateDiscardDeck(Image img){
        Image newImage = new Image(img.getUrl()); //Using the Url makes it non-pixelated
        discardDeck.setImage(newImage);
    }

    private void updateAnnouncement(String text){
        announcementText.setText(text);
    }

    private void populateHand(){
        for (int i = 0; i < 6; i++) {
            Image img;
            if (i % 2 == 0) {
                img = new Image("file:src/main/resources/images/normal/DefuseCard1.png");
            } else {
                img = new Image("file:src/main/resources/images/normal/AttackCard3.png");
            }
            addCardToHand(img);
        }
    }

    private void populatePlayers(){
        for (int i = 0; i < 3; i++) {
            Text text = new Text("Player " + (i+1) + "\nhas " + (i+2) + " cards.");
            text.setFont(Font.font("Verdana", FontWeight.BOLD,20));
            enemyHBox.getChildren().add(text);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populatePlayers();
        populateHand();
    }
}
