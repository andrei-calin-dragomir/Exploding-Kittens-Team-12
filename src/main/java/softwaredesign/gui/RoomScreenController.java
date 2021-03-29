package softwaredesign.gui;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import softwaredesign.client.ClientInfo;
import softwaredesign.client.ClientProgram;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Lobby view. You can chat and start the game (if you're the host)
 */
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
        ClientProgram.handleCommand("start");
    }

    @FXML
    public void playClick(){
        Sounds.playClick();
    }


    @FXML
    void updatePlayerList(){
        Text[] playerTexts = new Text[]{player1, player2, player3, player4};
        Object[] players = ClientInfo.getPlayerNamesAndHandSizes().keySet().toArray();
        for(Integer i = 0; i < 4; i++){
            if(i < players.length) {
                String player = players[i].toString();
                if(player.equals(ClientInfo.getUsername())) player += " (You)";
                playerTexts[i].setText(player);
            }
            else playerTexts[i].setText("");
        }
    }

    AnimationTimer lobbyUpdates = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (!ClientInfo.getServerMessage().isEmpty()) {
                String[] msg = ClientInfo.getServerMessage().removeFirst().split(" ");
                if(msg[0].equals("JOINED")) {
                    Sounds.playPlayerJoined();
                    updatePlayerList();
                }
                else if(msg[0].equals("LEFT")) {
                    updatePlayerList();
                    Sounds.playPlayerLeft();
                }
                else if(msg[0].equals("CHAT")) {
                    if(!msg[1].equals(ClientInfo.getUsername())) {
                        Sounds.playChatSound(true);
                    }
                    String tempString = "";
                    for(int i = 1; i < msg.length; ++i) tempString = tempString + msg[i] + " ";
                    chatMessages.add(tempString);
                    chatBox.setItems(chatMessages);
                }
                else if(msg[0].equals("CANTSTART")) {
                    Sounds.playErrorSound();
                    startError.setText("Only the host can start");
                }
                else if(msg[0].equals("NOSTART")) {
                    Sounds.playErrorSound();
                    startError.setText("Not enough players");
                }
                else if(msg[0].equals("START")){
                    Sounds.stopSound();
                    System.out.println("Starting game");
                    super.stop();
                    try { ViewsManager.loadScene(ViewsManager.SceneName.GAME_VIEW); }
                    catch (Exception ignore) {}
                }
            }
        }
    };

    /**
     * Use chat.
     */
    @FXML
    void sendMessage(){
        String textToSend = sendMessageField.getText();
        if(textToSend.isBlank()) return;
        Sounds.playChatSound(false);
        ClientProgram.handleCommand("chat " + textToSend);
        sendMessageField.setText("");
    }

    @FXML
    void leaveGame() throws Exception {
        Sounds.stopSound();
        ClientInfo.setPlayerNamesAndHandSizes(new LinkedHashMap<>());
        ClientInfo.getPlayerNamesAndHandSizes().put(ClientInfo.getUsername(), -1);  // Resets the local username list
        ClientProgram.handleCommand("leave");
        lobbyUpdates.stop();
        ViewsManager.loadScene(ViewsManager.SceneName.ROOM_SELECTION);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sounds.playRoomMusicWaiting();
        startError.setText("");
        chatBox.setFocusTraversable(false);
        ClientInfo.getPlayerNamesAndHandSizes().put(ClientInfo.getUsername(), -1);
        roomSize.setText("Room Size: " + ClientInfo.getGameRules()[0]);
        computerAmount.setText("Computers: " + ClientInfo.getGameRules()[1]);
        lobbyPlaceholder.setText(ClientInfo.getRoomName());
        updatePlayerList();
        lobbyUpdates.start();
        chatMessages.add("Welcome to room: " + ClientInfo.getRoomName());
        chatBox.setItems(chatMessages);

        sendMessageField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ){
                sendMessage();
            }
        });
    }
}