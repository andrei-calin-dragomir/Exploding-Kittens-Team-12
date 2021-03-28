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

    public void initGame(Room currentRoom) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        mainDeck = new Deck(currentRoom.getMaxPlayers());
        discardDeck = new DiscardDeck();
        for(Player player : currentRoom.getRoomPlayerList().values()){
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

    public String createHandAsString(Player player) {
        ArrayList<String> allCards = new ArrayList<>();
        for(Card card : player.getHand()) allCards.add(card.getName());
        return String.join(" ", allCards);
    }

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

    // Not tested but should work
    public void changeNextTurn(Player target){
        System.out.println(getAlivePlayers());
        int rotateAmount = alivePlayers.indexOf(target) * -1;
        System.out.println("Rotating by: " + rotateAmount);
        Collections.rotate(alivePlayers, rotateAmount);
        System.out.println(getAlivePlayers());
    }

    public void endTurn(){
        if(getCurrentPlayer().getPlayerState() == State.ATTACKED) getCurrentPlayer().setPlayerState(State.PLAYING);
        else Collections.rotate(alivePlayers, -1);
    }

    public void removeCurrentPlayerCard(Card card) {

        getCurrentPlayerHand().removeCard(card);
    }
}
