package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import softwaredesign.client.ClientProgram;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameViewController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private Button leaveButton;

    @FXML
    private TextField commandField;

    @FXML
    private TextArea receivedMessages;

    @FXML
    private TextArea debugBox;

    @FXML
    private HBox enemyHBox;

    @FXML
    private Text announcementText, placeError, deckSizeText;

    @FXML
    private ImageView mainDeck;

    @FXML
    private VBox insertIndexBox;

    @FXML
    private TextField indexField;

    @FXML
    private Button placeButton;

    @FXML
    private ImageView discardDeck;

    @FXML
    public ScrollPane cardScrollPane;

    @FXML
    public GridPane cardsGridPane;

    @FXML
    public void playClick(){
        //Sounds.playClick();
    }

    ArrayList<Button> playerInteractionButtons = new ArrayList<>();
    ArrayList<ImageView> cardImageViews = new ArrayList<>();
    static public String cardsToShow; //ugly but functional
    Boolean giveCardMode = false;
    String giveCardTarget;


    AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if(ClientProgram.serverMessage.isEmpty()){
                return;
            }
            String cmd = ClientProgram.serverMessage.removeFirst();
            addText("r> " + cmd);
            String tempString = "";
            String[] commands = cmd.split(" ");
            switch(commands[0]){
                case "LEAVEREGISTERED":
                    //go back to room
                    break;
                case "EXPLODING":
                    //must defuse it immediately
                    //alert player, freeze non-defuse cards?
                    //Sounds.playExplodingKittenDrawn();
                    setAnnouncementText("You drew an Exploding Kitten, quick, defuse it!");
                    setDisableDefuseCards(false);
                    setDisableOtherCards(true);
                    break;
                case "DIED":
                    //u died
                    //freeze all interactions
                    //Sounds.playExplosionSound();
                    setAnnouncementText("You died, bummer. :( ");
                    setDisableAll(true);
                    break;
                case "PLACEKITTEN":
                    //enable selection of index
                    //then send "place + index"
                    //Sounds.playPlayCard();
                    actionPutBackKitten();
                    break;
                case "ENDED":
                    //make everything freeze/go back to room list view?
                    //TODO check how it works with WINNER message
                    setAnnouncementText("Game ended.");
                    setDisableAll(true);
                    break;
                case "PLAYCONFIRMED":
                    //display green tick?
                    break;
                case "NOTALLOWED":
                    //hardcode into gui
                    if(commands[1].equals("DEAD")) {
                        System.out.println("You can't do that because you have already exploded.");
                    }
                    else if(commands[1].equals("BADPLACE")) {
                        //in index selection, only accept between right range
                        System.out.println("Invalid card placement, try placing again.");
                    }
                    else if(commands[1].equals("NOTEXPLODING")) {
                        //make defuse unplayable in normal game phase
                        System.out.println("You can only play a defuse card when you draw an Exploding Kitten!");
                    }
                    else if(commands[1].equals("MUSTDEFUSE")) {
                        //make cards other than defuse freeze
                        System.out.println("You have to play a defuse card when you draw an Exploding Kitten!");
                    }
//                    else if(commands[1].equals("INVALIDPLAY")) System.out.println("Trying to play invalid card.");
//                    else if(commands[1].equals("NOTYOURTURN")) System.out.println("It is not your turn.");
                    break;
                case "TURN":
                    //unfreeze nodes if it's ur turn
                    if(commands[1].equals(ClientProgram.username)){
                        setAnnouncementText("It's your turn!");
                        setDisableAll(false);
                    }
                    else {
                        setAnnouncementText("It's " + commands[1] + "'s turn!");
                        setDisableAll(true);
                    }
                    break;
                case "WINNER":
                    //WINNER + name
                    //update announcement and freeze everything
                    if(commands[1].equals(ClientProgram.username)){
                        //Sounds.playWin();
                        setAnnouncementText("You have won! Congrats");
                    }
                    else {
                        setAnnouncementText(commands[1] + " has won!");
                    }
                    setDisableAll(true);
                    break;
                case "LEFT":
                    //LEFT + whoLeft + remaining1@@remaning2
                    //global update + announcment
                    setAnnouncementText(commands[1] + " has left the game");
                    updateAll();
                    break;
                case "UPDATEHAND":
                    //global update
                    updateAll();
                    break;
                case "SEEFUTURE":
                    //SEEFUTURE cardname cardname cardname
                    //open windows with the cards
                    try {
                        launchSeeTheFutureDialog(cmd);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case "ATTACKED":
                    //update announcment
                    setAnnouncementText(commands[1] + " has attacked you, you draw twice");
                    break;
                case "TARGETED":
                    //targeted + name
                    //enable exclusively handview and select one card reply with
                    //TODO
                    setAnnouncementText(commands[1] + " has asked you a favor, select which card you want to give up");
                    giveCardTarget = commands[1];
                    setGiveCardMode(true);
                    break;
                case "PLAYER":
                    //PLAYER name ACTION ACTION2
                    //announce + global update
//                    updateAll();
                    switch(commands[2]){
                        case "EXPLODED":
                            //Sounds.playExplosionSound();
                            setAnnouncementText(commands[1] + " has just exploded!");
                            break;
                        case "DREW":
                            setAnnouncementText(commands[1] + " has drawn a card.");
                            break;
                        case "DREWEXP":
                            //Sounds.playMeow();
                            setAnnouncementText(commands[1] + " has drawn an exploding kitten!");
                            break;
                        case "DEFUSED":
                            setAnnouncementText(commands[1] + " has defused the kitten.");
                            break;
                        case "PLAYED":
                            setAnnouncementText(commands[1] + " played the " + commands[3] + " card.");
                            break;
                    }
                    break;
                case "UPDATEPLAYERHANDS":
                    //global refresh
                    updateAll();
                    System.out.println("YOUR HAND IS: " + ClientProgram.ownHand.toString());
                    break;
                case "CREATEPLAYERHANDS":
                    //do nothing;
                    System.out.println("YOUR HAND IS: " + ClientProgram.ownHand.toString());
                case "UPDATEDECKS":
                    //global update
                    updateAll();
                    break;
                default:
                    //update announcement?
                    setAnnouncementText("Unknown server command: " + cmd);
                    break;
            }

        }
    };

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
//                System.out.println("userdata = " + card.getUserData());
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

    void updateAll(){
        refreshPlayers();
        populateHand();
        setMostRecentDiscardedCard(ClientProgram.discardDeckTop);
    }

    void interactWithPlayer(String player, Boolean attack){
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
    void enableInteraction(Boolean attack){
        String buttonText;
        if(attack) buttonText = "Attack!";
        else buttonText = "Ask favor!";

        cardsGridPane.setDisable(true);
        mainDeck.setDisable(true);

        for(Button button : playerInteractionButtons){
            button.setText(buttonText);
            button.setOnAction(event -> interactWithPlayer((String) button.getParent().getUserData(),attack));
            button.setVisible(true);
        }
    }


    void actionPutBackKitten(){
        setDisableAll(true);
        setDisableInsertIndexBox(false);
    }

    @FXML
    void placeKitten(){
        System.out.println(ClientProgram.deckSize);
        String index = indexField.getText();
        if(!ClientProgram.isInteger(index)) placeError.setText("Enter a valid number");
        else if(Integer.parseInt(index) > Integer.parseInt(ClientProgram.deckSize)) placeError.setText("That number is too big");
        else if(Integer.parseInt(index) < 0) placeError.setText("That number is too small");
        else {
            sendCommand("place " + (Integer.parseInt(index)));
            setDisableAll(false);
            setDisableInsertIndexBox(true);
        }
    }

    @FXML
    void showDeckSize(){
        deckSizeText.setText("The deck has " + ClientProgram.deckSize + " cards");
        deckSizeText.setTextAlignment(TextAlignment.CENTER);
    }

    @FXML
    void hideDeckSize(){
        deckSizeText.setText("");
    }

    private void playCard(ImageView iv){
        //Sounds.stopSound();
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
        //Sounds.playPlayCard();

        removeCardFromHand(iv);

        setMostRecentDiscardedCard((String) iv.getUserData());
    }

    public void pickCard(){
        //Sounds.drawnCard();
        if(!cardsGridPane.isDisable()) {
            sendCommand("draw");
        }
    }

    private void removeCardFromHand(ImageView iv){
        cardsGridPane.getChildren().remove(iv);
        cardImageViews.remove(iv);
    }

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
//            setAnnouncementText("test");
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

    private void setAnnouncementText(String text){
        announcementText.setText(text);
    }

    private void populateHand(){
        cardsGridPane.getChildren().clear();
        cardImageViews.clear();


        for(String card : ClientProgram.ownHand){
            addCardToHand(card);
        }

        setDisableDefuseCards(true);
        setDisableOtherCards(false);
    }

    private void onHoverButton(Node node){
        node.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 15; -fx-font-size: 20; -fx-text-fill: white;");
        node.setOnMouseEntered(mouseEvent -> node.setStyle("-fx-background-color: #797979; -fx-background-radius: 15; -fx-font-size: 20; -fx-text-fill: white;"));
        node.setOnMouseExited(mouseEvent -> node.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 15; -fx-font-size: 20; -fx-text-fill: white;"));
    }

    private void launchSeeTheFutureDialog(String cmd) {
        cardsToShow = cmd;
        System.out.println("CARD TO SHOW = " + cardsToShow);
        ViewsManager.loadInNewWindow(ViewsManager.SceneName.SEE_THE_FUTURE_DIALOG);
    }

    private void refreshPlayers(){
        enemyHBox.getChildren().clear();
        for (String playerName : ClientProgram.playerNamesAndHandSizes.keySet()){
            if(!playerName.equals(ClientProgram.username)) {
                VBox enemy = new VBox();
                enemy.setAlignment(Pos.CENTER);
                enemy.setUserData(playerName);

                Text text = new Text("Player " + playerName + "\nhas " + ClientProgram.playerNamesAndHandSizes.get(playerName) + " cards.");
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

    @FXML
    void leave() throws Exception{
        sendCommand("leave");
        ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SELECTION);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardScrollPane.setPannable(true);
        refreshPlayers();
        populateHand();
        gameLoop.start();




        //  DEBUG STUFF
        commandField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ){
                try {
                    sendTextAsCommand();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }





    void sendCommand(String cmd) {
        commandField.setText("");
        addText("s> " + cmd);
        ClientProgram.handleCommand(cmd);
    }

    @FXML
    void sendTextAsCommand() throws IOException {
        String cmd = commandField.getText();
        sendCommand(cmd);
    }

    void addText(String text){
        debugBox.setText(debugBox.getText() + text + "\n");
        debugBox.positionCaret(debugBox.getLength());
    }
}
