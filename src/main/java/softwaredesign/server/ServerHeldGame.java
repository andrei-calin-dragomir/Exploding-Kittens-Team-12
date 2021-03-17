package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;

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
    public String getCurrentPlayer(){ return gameManager.getCurrentPlayer().getName(); }
    public Room getRoom() { return room; }

    public void nextTurn() throws InterruptedException {
        gameManager.endTurn();
        setNextTurn();
    }

    public void setNextTurn() throws InterruptedException {
        room.sendMessageToRoomClients(null,"TURN " + gameManager.getCurrentPlayer().getName());
        while(getCurrentPlayer().split("_")[0].equals("Computer")){
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
            room.sendMessageToSingleRoomClient(getCurrentPlayer(),"UPDATEHAND " + cardDrawn.getName());
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
        if (!gameManager.getCurrentPlayerHand().contains(new DefuseCard())) {
            room.sendMessageToSingleRoomClient(getCurrentPlayer(),"DIED");
            room.sendMessageToRoomClients(room.getRoomPlayerList().get(gameManager.getCurrentPlayer().getName()).getCtx(),
                    "PLAYER " + gameManager.getCurrentPlayer().getName() + " exploded");
            gameManager.killPlayer(gameManager.getCurrentPlayer());
        }else{
            room.sendMessageToRoomClients(room.getRoomPlayerList().get(gameManager.getCurrentPlayer().getName()).getCtx(),
                    "PLAYER " + gameManager.getCurrentPlayer().getName() + " drewexp");
            room.sendMessageToSingleRoomClient(getCurrentPlayer(),"EXPLODING");
        }
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
        while(shouldPlay){
            int pickCardToPlay = rand.nextInt(gameManager.getCurrentPlayerHand().getHandSize());
            boolean foundGoodCard = false;
            while(!foundGoodCard){
                if(gameManager.getCurrentPlayerHand().getHand().get(pickCardToPlay).equals(new DefuseCard())){
                    pickCardToPlay = rand.nextInt(gameManager.getCurrentPlayerHand().getHandSize());
                }else foundGoodCard = true;
            }
            handlePlayAction(pickCardToPlay);
            shouldPlay = rand.nextBoolean();
        }
    }
    private void handleDrawComputerAction() throws InterruptedException{
        Card cardDrawn = gameManager.mainDeck.draw();
        gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
        room.sendMessageToRoomClients(null,"PLAYER " + gameManager.getCurrentPlayer().getName() + " drew");
        if(cardDrawn.equals(new ExplodingKittenCard())){
            room.sendMessageToRoomClients(null,"PLAYER " + gameManager.getCurrentPlayer().getName() + " drewexp");
            if(gameManager.getCurrentPlayerHand().contains(new DefuseCard())){
                int defuseCardIndex = gameManager.getCurrentPlayerHand().getHand().indexOf(new DefuseCard());
                handlePlayAction(defuseCardIndex);
                int nextIndex = rand.nextInt(gameManager.mainDeck.getDeckSize());
                gameManager.mainDeck.insertCard(new ExplodingKittenCard(),nextIndex);
                room.sendMessageToRoomClients(null,"PLAYER " + gameManager.getCurrentPlayer().getName() + " defused");
            }else{
                room.sendMessageToRoomClients(null,"PLAYER " + gameManager.getCurrentPlayer().getName()
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