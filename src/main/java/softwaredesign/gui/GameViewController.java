package softwaredesign.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import softwaredesign.cards.DefuseCard;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GameViewController implements Initializable {

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

    //non-fxml vars

    static HashMap<String,Integer> players;

    private void playCard(ImageView iv){
        cardsGridPane.getChildren().remove(iv);
        updateDiscardDeck(iv.getImage());
    }

    public void pickCard(){
        addCardToHand(new Image("file:resources/images/normal/NopeCard1.png"));
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
            updateAnnouncement("test");
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
            Image img = new Image(CardHelper.getCardImageUrl(new DefuseCard()));
            addCardToHand(img);
        }
    }

    private void populatePlayers(){
//        for(String playerName : players.keySet()){
//            Text text = new Text("Player " + playerName + "\nhas " + players.get(playerName) + " cards.");
//            text.setFont(Font.font("Verdana", FontWeight.BOLD,20));
//            enemyHBox.getChildren().add(text);
//        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        System.out.println(ClientProgram.playerNamesAndHandSizes);
        populatePlayers();
        populateHand();
    }
}
