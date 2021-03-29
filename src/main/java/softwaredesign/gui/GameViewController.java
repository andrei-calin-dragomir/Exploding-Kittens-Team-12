package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import softwaredesign.client.ClientInfo;
import softwaredesign.client.ClientProgram;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller during game view, responsible of all interactions during game
 */
public class GameViewController implements Initializable {

    /**
     * Variables and methods referenced in the FXML files
     */

    @FXML
    private VBox root, debugArea, insertIndexBox;

    @FXML
    private HBox enemyHBox;

    @FXML
    private Button leaveButton, placeButton, sendCommandButton;

    @FXML
    private TextField commandField, indexField;

    @FXML
    private TextArea receivedMessages, debugBox;

    @FXML
    private Text announcementText, placeError, deckSizeText, personalAnnouncement;

    @FXML
    private ImageView mainDeck, discardDeck;

    @FXML
    public ScrollPane cardScrollPane;

    @FXML
    public GridPane cardsGridPane;

    @FXML
    public void playClick(){
        Sounds.playClick();
    }

    /**
     * Controller specific variables
     */
    private ArrayList<Button> playerInteractionButtons = new ArrayList<>();
    private ArrayList<ImageView> cardImageViews = new ArrayList<>();
    static public String cardsToShow; //ugly but functional
    private Boolean giveCardMode = false;
    private String giveCardTarget;


    /**
     * These are functions called with the FXML
     */

    @FXML
    void leave() throws Exception{
        Sounds.stopSound();
        gameLoop.stop();
        sendCommand("leave");
        if(ClientInfo.getOfflineGame()){
            ClientProgram.killOffline();
            Thread.sleep(2000);
            ViewsManager.loadScene(ViewsManager.SceneName.SPLASH_SCREEN);
        }
        else ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SELECTION);
    }


    @FXML
    void placeKitten(){
        String index = indexField.getText();
        if(!ClientProgram.isInteger(index)) {
            Sounds.playErrorSound();
            placeError.setText("Enter a valid number");
        }
        else if(Integer.parseInt(index) > Integer.parseInt(ClientInfo.getDeckSize())){
            Sounds.playErrorSound();
            placeError.setText("That number is too big");
        }
        else if(Integer.parseInt(index) < 0) {
            Sounds.playErrorSound();
            placeError.setText("That number is too small");
        }
        else {
            sendCommand("place " + (Integer.parseInt(index)));
            setDisableAll(false);
            setDisableInsertIndexBox(true);
        }
    }

    @FXML
    void showDeckSize(){
        deckSizeText.setText("The deck has " + ClientInfo.getDeckSize() + " cards");
        deckSizeText.setTextAlignment(TextAlignment.CENTER);
    }

    @FXML
    void hideDeckSize(){
        deckSizeText.setText("");
    }

    @FXML
    void sendCommand() {
        String cmd = commandField.getText();
        //debug
        commandField.setText("");
        addText("s> " + cmd);
        ClientProgram.handleCommand(cmd);
    }

    /**
     * This function executes the passed parameter. The @FXML version does so taking
     * the cmd parameter from the textField
     */
    void sendCommand(String cmd){
        //debug
        commandField.setText("");
        addText("s> " + cmd);
        ClientProgram.handleCommand(cmd);
    }



    /**
     * The main loop that checks for updates from the server. When it finds an update, it act accordingly.
     * This function gets called at every frame, ~60 times a second.
     */
    private AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(ClientInfo.getServerMessage().isEmpty()){
                return;
            }
            String cmd = ClientInfo.getServerMessage().removeFirst();
            addText("r> " + cmd);
            String tempString = "";
            String[] commands = cmd.split(" ");
            switch(commands[0]){
                case "EXPLODING":
                    Sounds.playExplodingKittenDrawn();
                    announcementText.setText("You drew an Exploding Kitten, quick, defuse it!");
                    setDisableDefuseCards(false);
                    setDisableOtherCards(true);
                    break;
                case "DIED":
                    Sounds.playExplosionSound();
                    personalAnnouncement.setText("You died, bummer. :( ");
                    setDisableAll(true);
                    if(ClientInfo.getPlayersInfo().size() == 3 && !Sounds.isPlaying("lastPlayersLeft")){
                        Sounds.stopSound();
                        Sounds.playLastPlayersMusic();
                    }
                    break;
                case "PLACEKITTEN":
                    Sounds.playPlayCard();
                    actionPlaceBackKitten();
                    break;
                case "ENDED":
                    announcementText.setText("Game ended.");
                    setDisableAll(true);
                    break;
                case "TURN":
                    Sounds.playNextTurnSound();
                    if(commands[1].equals(ClientInfo.getUsername())){ //if it's your turn
                        announcementText.setText("It's your turn!");
                        personalAnnouncement.setText("Play a card or draw!");
                        setDisableAll(false);
                    }
                    else {
                        announcementText.setText("It's " + commands[1] + "'s turn!");
                        setDisableAll(true);
                    }
                    break;
                case "WINNER":
                    //WINNER + name
                    if(commands[1].equals(ClientInfo.getUsername())){
                        Sounds.stopSound();
                        Sounds.playWin();
                        announcementText.setText("You have won! Congrats");
                    }
                    else {
                        announcementText.setText(commands[1] + " has won!");
                    }
                    setDisableAll(true);
                    break;
                case "LEFT":
                    //LEFT + whoLeft + remaining1@@remaning2
                    Sounds.playPlayerLeft();
                    announcementText.setText(commands[1] + " has left the game");
                    updateAll();
                    break;
                case "UPDATEHAND":
                    updateAll();
                    break;
                case "SEEFUTURE":
                    //SEEFUTURE cardname cardname cardname
                    try {
                        launchSeeTheFutureDialog(cmd);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case "ATTACKED":
                    personalAnnouncement.setText(commands[1] + " has attacked you, you draw twice");
                    break;
                case "TARGETED":
                    //TARGETED + name
                    Sounds.playGiveCard();
                    announcementText.setText(commands[1] + " has asked you a favor, select which card you want to give up");
                    giveCardTarget = commands[1];
                    setGiveCardMode(true);
                    break;
                case "PLAYER":
                    //PLAYER name ACTION ACTION2
                    switch(commands[2]){
                        case "EXPLODED":
                            Sounds.playExplosionSound();
                            personalAnnouncement.setText(commands[1] + " has just exploded!");
                            updateAll();
                            if(ClientInfo.getPlayersInfo().size() == 2 && !Sounds.isPlaying("lastPlayersLeft")){
                                Sounds.stopSound();
                                Sounds.playLastPlayersMusic();
                            }
                            break;
                        case "DREW":
                            Sounds.drawnCard();
                            personalAnnouncement.setText(commands[1] + " has drawn a card.");
                            break;
                        case "DREWEXP":
                            Sounds.playMeow();
                            personalAnnouncement.setText(commands[1] + " has drawn an exploding kitten!");
                            break;
                        case "DEFUSED":
                            Sounds.playPlayCard();
                            personalAnnouncement.setText(commands[1] + " has defused the kitten.");
                            break;
                        case "PLAYED":
                            Sounds.playPlayCard();
                            personalAnnouncement.setText(commands[1] + " played the " + commands[3] + " card.");
                            break;
                    }
                    break;
                case "UPDATEPLAYERHANDS":
                    //global refresh
                    updateAll();
                    break;
                case "UPDATEDECKS":
                    updateAll();
                    break;
                default:
//                    announcementText.setText("Unknown server command: " + cmd);
                    break;
            }

        }
    };

    /*The following functions enable and disable parts of the interface depending on what action
      is being played. The names are self-explanatory.
     */

    void setGiveCardMode(Boolean value){
        giveCardMode = value;
        setDisableAll(false);
        mainDeck.setDisable(value);
        setDisableDefuseCards(!value);
        setDisableOtherCards(!value);
    }

    void setDisableDefuseCards(Boolean value){
        for (ImageView card : cardImageViews) {
            if(card.getUserData().equals("DefuseCard")){
                card.setDisable(value);
            }
        }
    }

    void setDisableOtherCards(Boolean value){
        for (ImageView card : cardImageViews) {
            if(!card.getUserData().equals("DefuseCard")){
                card.setDisable(value);
            }
        }
    }

    void setDisableInsertIndexBox(Boolean value){
        //enable => value = false
        mainDeck.setVisible(value);
        insertIndexBox.setVisible(!value);
        insertIndexBox.setDisable(value);
        indexField.setDisable(value);
        placeButton.setDisable(value);
    }

    void setDisableAll(Boolean value){
//        mainDeck.setDisable(value);
        cardsGridPane.setDisable(value);
    }

    private void setupInteractionButtons(String player, Boolean attack){
        String actionType;
        if (attack) actionType = "AttackCard ";
        else actionType = "FavorCard ";
        sendCommand("play " + actionType + player);
        cardsGridPane.setDisable(false);
        mainDeck.setDisable(false);
        for(Button button : playerInteractionButtons){
            button.setVisible(false);
        }
    }

    /**
     * Enable buttons for favor or attack.
     * @param attack true if attack, false if favor
     */
    private void enableInteraction(Boolean attack){
        String buttonText;
        if(attack) buttonText = "Attack!";
        else buttonText = "Ask favor!";

        cardsGridPane.setDisable(true);
        mainDeck.setDisable(true);

        for(Button button : playerInteractionButtons){
            button.setText(buttonText);
            button.setOnAction(event -> setupInteractionButtons((String) button.getParent().getUserData(),attack));
            button.setVisible(true);
        }
    }


    void actionPlaceBackKitten(){
        placeError.setText("Insert value between \n0 (top of deck) and " + ClientInfo.getDeckSize());
        setDisableAll(true);
        setDisableInsertIndexBox(false);
    }

    /**
     * Repopulate all the elements of the interface
     */
    void updateAll(){
        refreshPlayers();
        populateHand();
        setMostRecentDiscardedCard(ClientInfo.getDiscardTop());
    }

    /**
     * This gets called when clicking on a card. Depending on state of the player and card, it does
     * the appropriate action
     */
    private void playCard(ImageView iv){
        Sounds.stopTicking();
        if(iv.getUserData().equals("AttackCard") && !giveCardMode){
            enableInteraction(true);
            return;
        }
        if(iv.getUserData().equals("FavorCard") && !giveCardMode){
            enableInteraction(false);
            return;
        }
        if(giveCardMode){
            sendCommand("give " + iv.getUserData() + " " + giveCardTarget );
            setGiveCardMode(false);
            setDisableAll(true);
        } else {
            sendCommand("play " + iv.getUserData());
        }
        Sounds.playPlayCard();

        removeCardFromHand(iv);

        setMostRecentDiscardedCard((String) iv.getUserData());
    }

    public void drawCard(){
        Sounds.drawnCard();
        if(!cardsGridPane.isDisable()) {
            sendCommand("draw");
        }
    }

    private void removeCardFromHand(ImageView iv){
        cardsGridPane.getChildren().remove(iv);
        cardImageViews.remove(iv);
    }


    /**
     * Adds the specified card to the ScrollPane containing the player hand
     * @param cardType for example FavorCard
     */
    private void addCardToHand(String cardType){
        Image img = new Image(CardHelper.getCardImageUrl(cardType));
        int lastColumn = cardsGridPane.getColumnCount();
        ImageView iv = new ImageView(img);
        cardImageViews.add(iv);
        iv.setUserData(cardType);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setFitHeight(200);

        iv.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            Node src = (Node) e.getSource();
            playCard((ImageView)src);
//            announcementText.setText("test");
        });
        //zoom on hover
        iv.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> iv.setFitHeight(250));
        iv.addEventFilter(MouseEvent.MOUSE_EXITED,  e -> iv.setFitHeight(200));

        cardsGridPane.add(iv,lastColumn+1,0);
    }

    private void setMostRecentDiscardedCard(String cardName){
        Image newImage = new Image(CardHelper.getCardImageUrl(cardName));
        discardDeck.setImage(newImage);
    }

    private void populateHand(){
        cardsGridPane.getChildren().clear();
        cardImageViews.clear();

        for(String card : ClientInfo.getOwnHand()){
            addCardToHand(card);
        }
        setDisableDefuseCards(true);
        setDisableOtherCards(false);
    }

    private void launchSeeTheFutureDialog(String cmd) {
        cardsToShow = cmd;
        System.out.println("CARD TO SHOW = " + cardsToShow);
        ViewsManager.loadInNewWindow(ViewsManager.SceneName.SEE_THE_FUTURE_DIALOG);
    }

    private void refreshPlayers(){
        enemyHBox.getChildren().clear();
        for (String playerName : ClientInfo.getPlayersInfo().keySet()){
            if(!playerName.equals(ClientInfo.getUsername())) {
                VBox enemy = new VBox();
                enemy.setAlignment(Pos.CENTER);
                enemy.setUserData(playerName);

                Text text = new Text("Player " + playerName + "\nhas " + ClientInfo.getPlayersInfo().get(playerName) + " cards.");
                text.setFont(Font.font("Bebas Neue", FontWeight.BOLD, 20));
                text.setStyle("-fx-font-size: 25");

                Button attackButton = new Button("Attack!");
                attackButton.setPrefWidth(100);
                attackButton.setPrefHeight(30);
                onHoverButton(attackButton);
                playerInteractionButtons.add(attackButton);
                attackButton.setVisible(false);

                enemy.getChildren().add(text);
                enemy.getChildren().add(attackButton);

                enemyHBox.getChildren().add(enemy);
            }
        }
    }

    private void onHoverButton(Node node){
        node.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 15; -fx-font-size: 20; -fx-text-fill: white;");
        node.setOnMouseEntered(mouseEvent -> node.setStyle("-fx-background-color: #797979; -fx-background-radius: 15; -fx-font-size: 20; -fx-text-fill: white;"));
        node.setOnMouseExited(mouseEvent -> node.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 15; -fx-font-size: 20; -fx-text-fill: white;"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sounds.playInGameMusic();
        cardScrollPane.setPannable(true);
        refreshPlayers();
        populateHand();
        gameLoop.start();

        /*
         * disable debug interface
         */
        debugArea.getChildren().clear();



        //  DEBUG STUFF
        commandField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ){
                sendCommand();
            }
        });
    }

    /**
     * Debug function that updates the textField
     */
    void addText(String text){
        debugBox.setText(debugBox.getText() + text + "\n");
        debugBox.positionCaret(debugBox.getLength());
    }
}
