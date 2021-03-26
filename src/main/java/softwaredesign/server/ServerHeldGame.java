package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Hand;
import softwaredesign.core.Player;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Collections.frequency;


public class ServerHeldGame {
    public ServerHeldGameManager gameManager;
    private Room room;
    private Random rand = new Random();
    public boolean drawnExplodingKitten;

    public ServerHeldGame(Room assignedRoom){ this.room = assignedRoom; }
    public void setExplodingBool(boolean exploding) { this.drawnExplodingKitten = exploding; }
    public String getCurrentPlayerName(){ return this.getCurrentPlayer().getName(); }
    public Player getCurrentPlayer(){ return gameManager.getCurrentPlayer(); }
    public Card drawCard(){
        System.out.println(gameManager.mainDeck.cardDeck);
        return gameManager.mainDeck.draw();
    }
    public Room getRoom() { return room; }

    public void nextTurn() throws InterruptedException {
        gameManager.endTurn();
        handleNextTurn();
    }

    public void handleNextTurn() throws InterruptedException {
        room.sendMsgToRoom(null,"TURN " + getCurrentPlayerName());
        while(getCurrentPlayer().isComputer()){
            Computer comp = (Computer) getCurrentPlayer();
            comp.startAction(this);
            if(checkWin()) return;
            room.sendMsgToRoom(null,"TURN " + getCurrentPlayerName());
        }
    }

    public void handleDrawAction() throws InterruptedException {
        if(!drawnExplodingKitten) {
            Card cardDrawn = drawCard();
            Player currPlayer = getCurrentPlayer();
            currPlayer.getHand().addToHand(cardDrawn);
            room.sendMsgToPlayer(currPlayer,"UPDATEHAND " + cardDrawn.getName());
            room.sendMsgToRoom(currPlayer, "PLAYER " + currPlayer.getName() + " DREW");
            room.sendGameStateUpdates("UPDATEPLAYERHANDS");
            if (cardDrawn.equals(new ExplodingKittenCard()))
                if(!handleExplodingKitten())
                    return;
            if(!checkWin()) nextTurn();
        }
    }

    public void handlePlayAction(int index) throws InterruptedException {
        Hand currHand = gameManager.getCurrentPlayerHand();
        Player currPlayer = getCurrentPlayer();
        Card cardPlayed = currHand.playCard(index, this);
        room.sendMsgToRoom(currPlayer, "PLAYER " + currPlayer.getName() + " PLAYED " +  cardPlayed.getName());
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    // Returns true if the player exploded
    public Boolean handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (!currentPlayer.getHand().contains(new DefuseCard())) {
            System.out.println("Killing player");
            room.sendMsgToPlayer(currentPlayer, "DIED");
            room.sendMsgToRoom(currentPlayer, "PLAYER " + getCurrentPlayerName() + " EXPLODED");
            gameManager.killPlayer(currentPlayer);
            drawnExplodingKitten = false;
            return true;
        }
        else{
            room.sendMsgToRoom(currentPlayer, "PLAYER " + getCurrentPlayerName() + " DREWEXP");
            room.sendMsgToPlayer(currentPlayer, "EXPLODING");
        }
        return false;
    }

    public void placeExploding(int index) throws InterruptedException {
        gameManager.mainDeck.insertCard(new ExplodingKittenCard(), index);
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    public Boolean checkWin(){
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
        while(shouldPlay && frequency(currentHand.getHand(), new DefuseCard()) != currentHand.getHandSize()){
            int pickCardToPlay = rand.nextInt(currentHand.getHandSize());
            boolean foundGoodCard = false;
            while(!foundGoodCard){
                if(currentHand.getCard(pickCardToPlay).equals(new DefuseCard())){
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
        room.sendMsgToRoom(null, "PLAYER " + getCurrentPlayerName() + " DREW");
        if (cardDrawn.equals(new ExplodingKittenCard())) {
            room.sendMsgToRoom(null, "PLAYER " + getCurrentPlayerName() + " drewexp");
            if (compHand.contains(new DefuseCard())) {
                int defuseCardIndex = compHand.getHand().indexOf(new DefuseCard());
                int nextIndex = rand.nextInt(getDeckSize());
                handlePlayAction(defuseCardIndex);
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