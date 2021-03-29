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
    public String getCurrentPlayerName(){ return this.getCurrentPlayer().getName(); }   // Used to shorten the calls (Law Of Demeter)
    public Player getCurrentPlayer(){ return gameManager.getCurrentPlayer(); }          // Used to shorten the calls (Law Of Demeter)
    public Card drawCard(){ return gameManager.mainDeck.draw(); }
    public Room getRoom() { return room; }

    public void nextTurn() throws InterruptedException {
        gameManager.endTurn();
        handleNextTurn();
    }

    // Sends the end turn message and loops over the bots if they are playing
    public void handleNextTurn() throws InterruptedException {
        room.sendMsgToRoom(null,"TURN " + getCurrentPlayerName());
        while(getCurrentPlayer().isComputer()){
            Computer comp = (Computer) getCurrentPlayer();
            comp.startAction(this);
            if(checkWin()) return;
            room.sendMsgToRoom(null,"TURN " + getCurrentPlayerName());
        }
    }

    //Handles what happens when a player draws, and will handle the exploding kitten, because in that case the player isn't supposed to end their turn.
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

    // Handles what happens if a player tries to play a card, it will call playCard which will then call the appropriate function in the Card subclass
    public void handlePlayAction(int index, String target) throws InterruptedException {
        Hand currHand = gameManager.getCurrentPlayerHand();
        Player currPlayer = getCurrentPlayer();
        room.sendMsgToRoom(currPlayer, "PLAYER " + currPlayer.getName() + " PLAYED " +  currHand.getCard(index).getName());
        currHand.playCard(index, target, this);
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    // Handles what happens if a player is exploding, will kill the player if they have no defuse.
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

    // Place the exploding kitten back at a certain index
    public void placeExploding(int index) {
        gameManager.mainDeck.insertCard(new ExplodingKittenCard(), index);
        getCurrentPlayer().setPlayerState(State.PLAYING);
        room.sendGameStateUpdates("UPDATEPLAYERHANDS");
    }

    // Used for favor, it gives a card from the "sender" to the "target"
    public void giveCard(int index,String target, String sender){
        Hand senderHand = room.roomPlayerList.get(sender).getHand();
        if(senderHand.getHandSize() == 0) return;
        Card cardToGive = senderHand.getCard(index);
        room.roomPlayerList.get(sender).getHand().removeCard(index);
        room.roomPlayerList.get(target).getHand().addToHand(cardToGive);
        room.sendMsgToPlayer(room.roomPlayerList.get(target), "UPDATEHAND " + cardToGive.getName());
    }

    // Checks if the current player is exploding
    public Boolean isExploding(){ return getCurrentPlayer().getPlayerState() == State.EXPLODING; }

    // Checks if only one player is left and handles if the player won
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

    // Starts the actual game by initialising the ServerHeldGameManager and starting the first turn
    public void start(String deckName) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        gameManager = new ServerHeldGameManager();
        gameManager.initGame(room, deckName);
        room.sendGameStateUpdates("CREATEPLAYERHANDS");
        handleNextTurn();
    }
}