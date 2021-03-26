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
        mainDeck = new Deck();
        discardDeck = new DiscardDeck();
        for(Player player : currentRoom.getRoomPlayerList().values()){
            alivePlayers.add(player);
            player.initHand(mainDeck);
            currentRoom.sendMsgToPlayer(player, "UPDATEHAND " + createHandAsString(player));
        }
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

//    public String getTopThreeCards() {
//        if(mainDeck.getDeckSize() <= 3){
//            ArrayList<String> allPlayers = new ArrayList<>();
//            for(Card card : mainDeck) allPlayers.add(card.getName());
//            return String.join(" ", allPlayers);
//        }else return mainDeck.getFullDeck().get(0).getName() + " " +
//                    mainDeck.getFullDeck().get(1).getName() + " " +
//                    mainDeck.getFullDeck().get(2).getName();
//    }

    // Not tested but should work
    public void changeNextTurn(Player target){
        int rotateAmount = alivePlayers.indexOf(target);
        Collections.rotate(alivePlayers, rotateAmount);
    }

    public void endTurn(){
        Collections.rotate(alivePlayers, 1);
    }

    public void removeCurrentPlayerCard(Card card) {

        getCurrentPlayerHand().removeCard(card);
    }
}
