package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import softwaredesign.client.ClientProgram;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class RoomScreenController implements Initializable {

    ObservableList<String> chatMessages = observableArrayList();

    @FXML
    private Text startError, roomSize, computerAmount, lobbyPlaceholder, player1, player2, player3, player4;

    @FXML
    private TextField sendMessageField;

    @FXML
    private ListView<String> chatBox;

    @FXML
    void startGame(){
        Sounds.stopSound();
        ClientProgram.handleCommand("start");
    }

    @FXML
    public void playClick(){
        Sounds.stopSound();
        Sounds.playClick();
    }
    @FXML
    public void playMessageSent(){
        Sounds.playChatSound(false);
    }

    @FXML
    void updatePlayerList(){
        Text[] playerTexts = new Text[]{player1, player2, player3, player4};
        Object[] players = ClientProgram.playerNamesAndHandSizes.keySet().toArray();
        for(Integer i = 0; i < 4; i++){
            if(i < players.length) {
                String player = players[i].toString();
                if(player.equals(ClientProgram.username)) player += " (You)";
                playerTexts[i].setText(player);
            }
            else playerTexts[i].setText("");
        }
    }

    AnimationTimer lobbyUpdates = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (!ClientProgram.serverMessage.isEmpty()) {
                String[] msg = ClientProgram.serverMessage.removeFirst().split(" ");
                if(msg[0].equals("JOINED") || msg[0].equals("LEFT")) updatePlayerList();
                else if(msg[0].equals("CHAT")) {
                    Sounds.playChatSound(true);
                    String tempString = "";
                    for(int i = 1; i < msg.length; ++i) tempString = tempString + msg[i] + " ";
                    chatMessages.add(tempString);
                    chatBox.setItems(chatMessages);
                    Sounds.playChatSound(true);
                }
                else if(msg[0].equals("CANTSTART")) startError.setText("Only the host can start");
                else if(msg[0].equals("NOSTART")) startError.setText("Not enough players");
                else if(msg[0].equals("START")){
                    System.out.println("Starting game");
                    super.stop();
                    try { ViewsManager.loadScene(ViewsManager.SceneName.GAME_VIEW); }
                    catch (Exception ignore) {}
                }
            }
        }
    };

    @FXML
    void sendMessage(){
        String textToSend = sendMessageField.getText();
        if(textToSend.isBlank()) return;
        ClientProgram.handleCommand("chat " + textToSend);
        sendMessageField.setText("");
    }

    @FXML
    void leaveGame() throws Exception {
        ClientProgram.playerNamesAndHandSizes = new LinkedHashMap<>();
        ClientProgram.playerNamesAndHandSizes.put(ClientProgram.username, -1);
        ClientProgram.handleCommand("leave");
        lobbyUpdates.stop();
        ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SELECTION);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sounds.playRoomMusicWaiting();
        startError.setText("");
        chatBox.setFocusTraversable(false);
        ClientProgram.playerNamesAndHandSizes.put(ClientProgram.username, -1);
        roomSize.setText("Room Size: " + ClientProgram.gameRules[0]);
        computerAmount.setText("Computers: " + ClientProgram.gameRules[1]);
        lobbyPlaceholder.setText(ClientProgram.roomName);
        updatePlayerList();
        lobbyUpdates.start();
        chatMessages.add("Welcome to room: " + ClientProgram.roomName);
        chatBox.setItems(chatMessages);

        sendMessageField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ){
                Sounds.playChatSound(false);
                sendMessage();
            }
        });
    }
}