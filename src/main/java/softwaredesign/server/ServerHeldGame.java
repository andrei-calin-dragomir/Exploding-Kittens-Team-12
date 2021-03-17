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

    public void handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        if (!gameManager.getCurrentPlayerHand().contains(new DefuseCard())) {
            room.sendMessageToSingleRoomClient(getCurrentPlayer(),"DIED");
            room.removePlayer(getCurrentPlayer());        // Fix spectator maybe?
            room.sendMessageToRoomClients(null,gameManager.getCurrentPlayer().getName() + " exploded!");
            gameManager.killPlayer(gameManager.getCurrentPlayer());
        }else{
            room.sendMessageToSingleRoomClient(getCurrentPlayer(),"EXPLODING");
        }
    }

    public void handleDrawAction() throws InterruptedException {
        if(!drawnExplodingKitten) {
            Card cardDrawn = gameManager.mainDeck.draw();
            gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
            room.sendMessageToSingleRoomClient(getCurrentPlayer(),"UPDATEHAND " + cardDrawn.getName());
            String topCardName = "NOCARD";
            Card topCard = gameManager.discardDeck.getTopCard();
            if(topCard != null) topCardName = topCard.getName();
            room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize()
                    + " " + topCardName);
            if (cardDrawn.equals(new ExplodingKittenCard())){
                handleExplodingKitten();
                return;
            }

            gameManager.endTurn();
            if(getCurrentPlayer().split("_")[0].equals("Computer")) handleComputerAction();
            room.sendMessageToRoomClients(null,"TURN " + gameManager.getCurrentPlayer().getName());
        }
        // Handle exploding kitten
    }

    public void handlePlayAction(int index) throws InterruptedException {
        if(gameManager.getCurrentPlayer().getHand().getCard(index).equals(new DefuseCard())) drawnExplodingKitten = false;
        gameManager.discardDeck.discardCard(gameManager.getCurrentPlayerHand().playCard(index, this));
        String topCardName = "NOCARD";
        Card topCard = gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();
        room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize()
                + " " + topCardName);
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
    public String getCurrentPlayer(){ return gameManager.getCurrentPlayer().getName(); }
    public Room getRoom() { return room; }

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
                gameManager.mainDeck.insertCard(new ExplodingKittenCard(),rand.nextInt(gameManager.mainDeck.getDeckSize()));
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
        gameManager.endTurn();
    }
    public void start() throws IOException, InterruptedException {
        gameManager = new ServerHeldGameManager();
        gameManager.addPlayers(room);
        room.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + "NOCARD");
        room.sendMessageToRoomClients(null, "TURN " + gameManager.getCurrentPlayer().getName()); //Initial player
    }

}