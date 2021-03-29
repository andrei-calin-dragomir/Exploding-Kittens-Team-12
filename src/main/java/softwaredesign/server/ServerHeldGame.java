package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Hand;
import softwaredesign.core.Player;
import softwaredesign.core.State;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ServerHeldGame {
    public ServerHeldGameManager gameManager;
    private Room room;

    public ServerHeldGame(Room assignedRoom){ this.room = assignedRoom; }
    public String getCurrentPlayerName(){ return this.getCurrentPlayer().getName(); }
    public Player getCurrentPlayer(){ return gameManager.getCurrentPlayer(); }
    public Player getPlayer(int index){ return gameManager.getAlivePlayers().get(index); }
    public Card drawCard(){ return gameManager.mainDeck.draw(); }
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
        if(!isExploding()) {
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

    public void handlePlayAction(int index, String target) throws InterruptedException {
        Hand currHand = gameManager.getCurrentPlayerHand();
        Player currPlayer = getCurrentPlayer();
        room.sendMsgToRoom(currPlayer, "PLAYER " + currPlayer.getName() + " PLAYED " +  currHand.getCard(index).getName());
        currHand.playCard(index, target, this);
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    // Returns true if the player exploded
    public Boolean handleExplodingKitten() throws InterruptedException {
        Player currentPlayer = gameManager.getCurrentPlayer();
        currentPlayer.setPlayerState(State.EXPLODING);
        if (!currentPlayer.getHand().contains(new DefuseCard())) {
            currentPlayer.setPlayerState(State.SPECTATING);
            room.sendMsgToPlayer(currentPlayer, "DIED");
            room.sendMsgToRoom(currentPlayer, "PLAYER " + getCurrentPlayerName() + " EXPLODED");
            gameManager.killPlayer(currentPlayer);
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
        getCurrentPlayer().setPlayerState(State.PLAYING);
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    public void giveCard(int index,String target, String sender){
        Card cardToGive = room.roomPlayerList.get(sender).getHand().getCard(index);
        room.roomPlayerList.get(sender).getHand().removeCard(index);
        room.roomPlayerList.get(target).getHand().addToHand(cardToGive);
        room.sendMsgToPlayer(room.roomPlayerList.get(target), "UPDATEHAND " + cardToGive.getName());
    }

    public Boolean isExploding(){ return getCurrentPlayer().getPlayerState() == State.EXPLODING; }

    public Boolean checkWin(){
        if(gameManager.getPlayersLeft() == 1){
            room.sendMsgToRoom(null, "WINNER " + gameManager.getCurrentPlayer().getName());
            gameManager.killPlayer(gameManager.getCurrentPlayer());
            return true;
        }
        return false;
    }

    public Integer getDeckSize(){
        return gameManager.mainDeck.getDeckSize();
    }

    public void start(String deckName) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        gameManager = new ServerHeldGameManager();
        gameManager.initGame(room, deckName);
        room.sendGameStateUpdates("CREATEPLAYERHANDS");
        handleNextTurn();
    }
}