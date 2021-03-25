package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Hand;
import softwaredesign.core.Player;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        handleNextTurn();
    }

    public void handleNextTurn() throws InterruptedException {
        room.sendMsgToRoom(null,"TURN " + getCurrentPlayerName());
        while(gameManager.getCurrentPlayer().isComputer()){
            handleComputerAction();
            if(checkWin()) return;
            gameManager.endTurn();
            room.sendMsgToRoom(null,"TURN " + getCurrentPlayerName());
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
            room.sendMsgToPlayer(getCurrentPlayerName(),"UPDATEHAND " + cardDrawn.getName());
            room.sendMsgToRoom(getCurrentPlayerName(),
                    "PLAYER " + getCurrentPlayerName() + " drew");
            room.sendGameStateUpdates("UPDATEPLAYERHANDS");
            if (cardDrawn.equals(new ExplodingKittenCard())) handleExplodingKitten();
            else nextTurn();
        }
    }

    public void handlePlayAction(int index) throws InterruptedException {
        Hand currHand = gameManager.getCurrentPlayerHand();
        String currPlayer = getCurrentPlayerName();
        if(currHand.getCard(index).equals(new DefuseCard())) drawnExplodingKitten = false;
        System.out.println(currHand.getHand().get(index));
        currHand.playCard(index, this);
        room.sendMsgToRoom(currPlayer,
                "PLAYER " + currPlayer + " played " +
                        gameManager.discardDeck.getTopCard().getName());
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    public void handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (!currentPlayer.getHand().contains(new DefuseCard())) {
            room.sendMsgToPlayer(getCurrentPlayerName(),"DIED");
            room.sendMsgToRoom(getCurrentPlayerName(),"PLAYER " + getCurrentPlayerName() + " EXPLODED");
            gameManager.killPlayer(currentPlayer);
            if(!checkWin()) nextTurn();
        }else{
            room.sendMsgToRoom(getCurrentPlayerName(),
                    "PLAYER " + getCurrentPlayerName() + " drewexp");
            room.sendMsgToPlayer(getCurrentPlayerName(),"EXPLODING");
        }
    }

    private Boolean checkWin(){
        if(gameManager.getPlayersLeft() == 1){
            room.sendMsgToRoom(null, "WINNER " + gameManager.getCurrentPlayer().getName());
            gameManager.killPlayer(gameManager.getCurrentPlayer());
            return true;
        }
        return false;
    }

    public void handleComputerAction() throws InterruptedException {
        while(rand.nextInt(3) == 0) handleComputerPlayAction();
        handleDrawComputerAction();
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
        Hand compHand = gameManager.getCurrentPlayerHand();
        Card cardDrawn = gameManager.mainDeck.draw();
        compHand.addToHand(cardDrawn);
        room.sendMsgToRoom(null, "PLAYER " + getCurrentPlayerName() + " drew");
        if (cardDrawn.equals(new ExplodingKittenCard())) {
            room.sendMsgToRoom(null, "PLAYER " + getCurrentPlayerName() + " drewexp");
            if (compHand.contains(new DefuseCard())) {
                int defuseCardIndex = compHand.getHand().indexOf(new DefuseCard());
                handlePlayAction(defuseCardIndex);
                int nextIndex = rand.nextInt(getDeckSize());
                gameManager.mainDeck.insertCard(new ExplodingKittenCard(), nextIndex);
                room.sendMsgToRoom(null, "PLAYER " + getCurrentPlayerName() + " defused");
            } else {
                room.sendMsgToRoom(null, "PLAYER " + getCurrentPlayerName() + " exploded");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
            }
        }
    }

    public Integer getDeckSize(){
        return gameManager.mainDeck.getDeckSize();
    }

    public void start() throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        gameManager = new ServerHeldGameManager();
        gameManager.initGame(room);
        room.sendGameStateUpdates("CREATEPLAYERHANDS");
        handleNextTurn();
    }
}