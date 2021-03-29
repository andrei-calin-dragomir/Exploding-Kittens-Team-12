package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.core.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerHeldGameManager{
    private List<Player> alivePlayers = new ArrayList<>();
    public Deck mainDeck;
    public DiscardDeck discardDeck;

    // Creates the Deck, discardDeck and populates the alivePlayers list. Then it will populate the player hands and send it as an update.
    public void initGame(Room currentRoom, String deckName) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        mainDeck = new Deck(currentRoom.getMaxPlayers(), deckName);
        discardDeck = new DiscardDeck();
        for(Player player : currentRoom.getRoomPlayerList().values()){
            player.setPlayerState(State.PLAYING);
            alivePlayers.add(player);
            player.initHand(mainDeck);
            currentRoom.sendMsgToPlayer(player, "UPDATEHAND " + createHandAsString(player));
        }
    }

    public Player getPlayerByName(String playerName){
        for(Player player : alivePlayers)
            if(playerName.equals(player.getName()))
                return player;
        return null;
    }

    // Returns a string that can be sent over the server, which can then be parsed correctly client side.
    public String createHandAsString(Player player) {
        ArrayList<String> allCards = new ArrayList<>();
        for(Card card : player.getHand()) allCards.add(card.getName());
        return String.join(" ", allCards);
    }

    // Gets the player who's turn it is now
    public Player getCurrentPlayer(){
        return alivePlayers.get(0);
    }

    public Hand getCurrentPlayerHand(){
        return getCurrentPlayer().getHand();
    }

    public List<Player> getAlivePlayers(){
        return this.alivePlayers;
    }

    public Integer getPlayersLeft(){
        return this.alivePlayers.size();
    }

    public Boolean isAlive(String playerName){
        for(Player player : alivePlayers)
            if(player.getName().equals(playerName))
                return true;
        return false;
    }

    public void killPlayer(Player target){ alivePlayers.remove(target); }

    // Changes the turn to a specific player (used in attack and favor)
    public void changeNextTurn(Player target){
        int rotateAmount = alivePlayers.indexOf(target) * -1;
        Collections.rotate(alivePlayers, rotateAmount);
    }

    // Only ends the turn in the case that the player is not attacked, otherwise they will have to play twice.
    public void endTurn(){
        if(getCurrentPlayer().getPlayerState() == State.ATTACKED) getCurrentPlayer().setPlayerState(State.PLAYING);
        else Collections.rotate(alivePlayers, -1);
    }

    // Removes a specific card from the current player
    public void removeCurrentPlayerCard(Card card) {
        getCurrentPlayerHand().removeCard(card);
    }
}
