package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Hand;
import softwaredesign.core.Player;

import java.io.IOException;
import java.util.*;


public class ServerHeldGame {
    public ServerHeldGameManager gameManager;
    private Room room;
    private Random rand = new Random();
    public boolean drawnExplodingKitten;

    public ServerHeldGame(Room assignedRoom){
        room = assignedRoom;
    }
    public String getCurrentPlayerName(){ return gameManager.getCurrentPlayer().getName(); }
    public Room getRoom() { return room; }

    public void nextTurn() throws InterruptedException {
        gameManager.endTurn();
        setNextTurn();
    }

    public void setNextTurn() throws InterruptedException {
        room.sendMessageToRoomClients(null,"TURN " + getCurrentPlayerName());
        while(getCurrentPlayerName().split("_")[0].equals("Computer")){
            handleComputerAction();
            if(checkWin()) return;
            gameManager.endTurn();
            room.sendMessageToRoomClients(null,"TURN " + getCurrentPlayerName());
        }
    }

    public void handleAction(String action) throws InterruptedException {
        String[] parsedAction = action.split(" ");
        System.out.println("Drawing card: " + action);
        switch (parsedAction[0]){
            case "draw":
                handleDrawAction();
                break;
            case "play":
                handlePlayAction(Integer.parseInt(parsedAction[1]));
                break;
        }
    }

    public void handleDrawAction() throws InterruptedException {
        if(!drawnExplodingKitten) {
            Card cardDrawn = gameManager.mainDeck.draw();
            gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
            room.sendMessageToSingleRoomClient(getCurrentPlayerName(),"UPDATEHAND " + cardDrawn.getName());
            room.sendMessageToRoomClients(getCurrentPlayerName(),
                    "PLAYER " + getCurrentPlayerName() + " drew");
            room.sendGameStateUpdates("UPDATEPLAYERHANDS");
            if (cardDrawn.equals(new ExplodingKittenCard())) handleExplodingKitten();
            else nextTurn();
        }
    }

    public void handlePlayAction(int index) throws InterruptedException {
        if(gameManager.getCurrentPlayerHand().getCard(index).equals(new DefuseCard())) drawnExplodingKitten = false;
        gameManager.getCurrentPlayerHand().playCard(index, this);
        room.sendMessageToRoomClients(getCurrentPlayerName(),
                "PLAYER " + getCurrentPlayerName() + " played " +
                        gameManager.discardDeck.getTopCard().getName());
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    public void handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (!currentPlayer.getHand().contains(new DefuseCard())) {
            room.sendMessageToSingleRoomClient(getCurrentPlayerName(),"DIED");
            room.sendMessageToRoomClients(getCurrentPlayerName(),"PLAYER " + getCurrentPlayerName() + " EXPLODED");
            gameManager.killPlayer(currentPlayer);
            if(!checkWin()) nextTurn();
        }else{
            room.sendMessageToRoomClients(getCurrentPlayerName(),
                    "PLAYER " + getCurrentPlayerName() + " drewexp");
            room.sendMessageToSingleRoomClient(getCurrentPlayerName(),"EXPLODING");
        }
    }

    private Boolean checkWin(){
        if(gameManager.alivePlayers.size() == 1){
            room.sendMessageToRoomClients(null, "WINNER " + gameManager.alivePlayers.get(0).getName());
            gameManager.killPlayer(gameManager.alivePlayers.get(0));
            return true;
        }
        return false;
    }

    public void handleComputerAction() throws InterruptedException {
        int pickAction = rand.nextInt(2);
        if(pickAction == 0){
            handleDrawComputerAction();
        }else{
            handleComputerPlayAction();
            handleDrawComputerAction();
        }
    }

    private void handleComputerPlayAction() throws InterruptedException{
        boolean shouldPlay = true;
        Hand currentHand = gameManager.getCurrentPlayer().getHand();
        while(shouldPlay && Collections.frequency(currentHand.getHand(), new DefuseCard()) != currentHand.getHandSize()){
            int pickCardToPlay = rand.nextInt(currentHand.getHandSize());
            boolean foundGoodCard = false;
            while(!foundGoodCard){
                if(currentHand.getHand().get(pickCardToPlay).equals(new DefuseCard())){
                    pickCardToPlay = rand.nextInt(currentHand.getHandSize());
                }else foundGoodCard = true;
            }
            handlePlayAction(pickCardToPlay);
            shouldPlay = rand.nextBoolean();
        }
    }
    private void handleDrawComputerAction() throws InterruptedException {
        Card cardDrawn = gameManager.mainDeck.draw();
        gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
        room.sendMessageToRoomClients(null, "PLAYER " + getCurrentPlayerName() + " drew");
        if (cardDrawn.equals(new ExplodingKittenCard())) {
            room.sendMessageToRoomClients(null, "PLAYER " + getCurrentPlayerName() + " drewexp");
            if (gameManager.getCurrentPlayerHand().contains(new DefuseCard())) {
                int defuseCardIndex = gameManager.getCurrentPlayerHand().getHand().indexOf(new DefuseCard());
                handlePlayAction(defuseCardIndex);
                int nextIndex = rand.nextInt(gameManager.mainDeck.getDeckSize());
                gameManager.mainDeck.insertCard(new ExplodingKittenCard(), nextIndex);
                room.sendMessageToRoomClients(null, "PLAYER " + getCurrentPlayerName() + " defused");
            } else {
                room.sendMessageToRoomClients(null, "PLAYER " + getCurrentPlayerName() + " exploded");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
            }
        }
    }

    public void start() throws IOException, InterruptedException {
        gameManager = new ServerHeldGameManager();
        gameManager.addPlayers(room);
        room.sendGameStateUpdates("CREATEPLAYERHANDS");
        setNextTurn(); //Initial player
    }

}