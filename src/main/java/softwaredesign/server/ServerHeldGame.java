package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Player;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;


public class ServerHeldGame {
    public ServerHeldGameManager gameManager;
    private Scanner scanner = new Scanner(System.in);
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
        String topCardName = "NOCARD";
        Card topCard = gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();
        room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + topCardName);
    }

    public void handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (!currentPlayer.getHand().contains(new DefuseCard())) {
            room.sendMessageToSingleRoomClient(getCurrentPlayerName(),"DIED");
            room.sendMessageToRoomClients(room.getClientCTX(getCurrentPlayerName()),"PLAYER " + currentPlayer.getName() + " EXPLODED");
            gameManager.killPlayer(currentPlayer);
            nextTurn();
        }else{
            room.sendMessageToSingleRoomClient(getCurrentPlayerName(),"EXPLODING");
        }
    }

    public void handleComputerAction() throws InterruptedException {
        Card cardDrawn = gameManager.mainDeck.draw();
        gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
        room.sendMessageToRoomClients(null,"COMPUTER " + gameManager.getCurrentPlayer().getName() + " drew");
        if(cardDrawn.equals(new ExplodingKittenCard())){
            room.sendMessageToRoomClients(null,"COMPUTER " + gameManager.getCurrentPlayer().getName() + " drewexp");
            if(gameManager.getCurrentPlayerHand().contains(new DefuseCard())){
                System.out.println(gameManager.getCurrentPlayer().getHand().getHand().toString());
                int defuseCardIndex = gameManager.getCurrentPlayerHand().getHand().indexOf(new DefuseCard());
                handlePlayAction(defuseCardIndex);
                int nextIndex = rand.nextInt(gameManager.mainDeck.getDeckSize());
                gameManager.mainDeck.insertCard(new ExplodingKittenCard(),nextIndex);
                System.out.println(gameManager.mainDeck.getFullDeck().get(0).getName());
                room.sendMessageToRoomClients(null,"COMPUTER " + gameManager.getCurrentPlayer().getName() + " defused");
            }
            else{
                room.sendMessageToRoomClients(null,"COMPUTER " + gameManager.getCurrentPlayer().getName()
                                                           + " exploded");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
            }
        }
        else{
            String topCardName = "NOCARD";
            Card topCard = gameManager.discardDeck.getTopCard();
            if(topCard != null) topCardName = topCard.getName();
            room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + topCardName);
        }
    }
    public void start() throws IOException, InterruptedException {
        gameManager = new ServerHeldGameManager();
        gameManager.addPlayers(room);
        room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + "NOCARD");
        setNextTurn(); //Initial player
    }

}