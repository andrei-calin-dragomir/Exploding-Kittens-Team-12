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
        room.sendMessageToRoomClients(null,"TURN " + gameManager.getCurrentPlayer().getName());
        while(getCurrentPlayerName().split("_")[0].equals("Computer")){
            handleComputerAction();
            if(checkWin()) return;
            gameManager.endTurn();
            room.sendMessageToRoomClients(null,"TURN " + gameManager.getCurrentPlayer().getName());
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
            room.sendMessageToRoomClients(room.getRoomPlayerList().get(gameManager.getCurrentPlayer().getName()).getCtx(),
                    "PLAYER " + gameManager.getCurrentPlayer().getName() + " drew");
            String topCardName = "NOCARD";
            Card topCard = gameManager.discardDeck.getTopCard();
            if(topCard != null) topCardName = topCard.getName();
            room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + topCardName);
            if (cardDrawn.equals(new ExplodingKittenCard())) handleExplodingKitten();
            else nextTurn();
        }
    }

    public void handlePlayAction(int index) throws InterruptedException {
        if(gameManager.getCurrentPlayer().getHand().getCard(index).equals(new DefuseCard())){
            gameManager.discardDeck.discardCard(new ExplodingKittenCard());
            drawnExplodingKitten = false;
        }
        gameManager.discardDeck.discardCard(gameManager.getCurrentPlayerHand().playCard(index, this));
        room.sendMessageToRoomClients(room.getRoomPlayerList().get(gameManager.getCurrentPlayer().getName()).getCtx(),
                "PLAYER " + gameManager.getCurrentPlayer().getName() + " played " +
                        gameManager.discardDeck.getTopCard().getName());
        room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " +
                gameManager.discardDeck.getTopCard().getName());
    }

    public void handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (!currentPlayer.getHand().contains(new DefuseCard())) {
            room.sendMessageToSingleRoomClient(getCurrentPlayerName(),"DIED");
            room.sendMessageToRoomClients(room.getClientCTX(getCurrentPlayerName()),"PLAYER " + currentPlayer.getName() + " EXPLODED");
            gameManager.killPlayer(currentPlayer);
            if(!checkWin()) nextTurn();
        }else{
            room.sendMessageToRoomClients(room.getRoomPlayerList().get(gameManager.getCurrentPlayer().getName()).getCtx(),
                    "PLAYER " + gameManager.getCurrentPlayer().getName() + " drewexp");
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
        room.sendMessageToRoomClients(null, "PLAYER " + gameManager.getCurrentPlayer().getName() + " drew");
        if (cardDrawn.equals(new ExplodingKittenCard())) {
            room.sendMessageToRoomClients(null, "PLAYER " + gameManager.getCurrentPlayer().getName() + " drewexp");
            if (gameManager.getCurrentPlayerHand().contains(new DefuseCard())) {
                int defuseCardIndex = gameManager.getCurrentPlayerHand().getHand().indexOf(new DefuseCard());
                handlePlayAction(defuseCardIndex);
                int nextIndex = rand.nextInt(gameManager.mainDeck.getDeckSize());
                gameManager.mainDeck.insertCard(new ExplodingKittenCard(), nextIndex);
                room.sendMessageToRoomClients(null, "PLAYER " + gameManager.getCurrentPlayer().getName() + " defused");
            } else {
                room.sendMessageToRoomClients(null, "PLAYER " + gameManager.getCurrentPlayer().getName() + " exploded");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
            }
        }
    }

    public void start() throws IOException, InterruptedException {
        gameManager = new ServerHeldGameManager();
        gameManager.addPlayers(room);
        room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + "NOCARD");
        setNextTurn(); //Initial player
    }

}