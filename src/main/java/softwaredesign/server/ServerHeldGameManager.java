package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.core.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerHeldGameManager{
    public List<Player> alivePlayers = new ArrayList<>();
    public DoublyLinkedList turns = new DoublyLinkedList();
    public Deck mainDeck;
    public DiscardDeck discardDeck;

    public void addPlayers(Room currentRoom) throws IOException, InterruptedException{
        mainDeck = new Deck();
        discardDeck = new DiscardDeck();
        for(Client client : currentRoom.getRoomPlayerList().values()){
            Player newPlayer = new Player();
            alivePlayers.add(newPlayer);
            newPlayer.setName(client.getClientName());
            newPlayer.initHand(mainDeck);
            currentRoom.sendMessageToSingleRoomClient(newPlayer.getName(), "UPDATEHAND " + createHandAsString(newPlayer));
            getTurns().addNode(newPlayer);
        }
    }

    public String createHandAsString(Player player) {
        ArrayList<String> allCards = new ArrayList<>();
        for(Card card : player.getHand().getHand()) allCards.add(card.getName());
        return String.join(" ", allCards);
    }
    public Player getCurrentPlayer(){
        return turns.head.item;
    }

    public Hand getCurrentPlayerHand(){
        return turns.head.item.getHand();
    }

    public List<Player> getAlivePlayers(){
        return this.alivePlayers;
    }

    public void killPlayer(Player target){
        alivePlayers.remove(target);
        turns.head.previous.next = turns.head.next;
        turns.head.next.previous = turns.head.previous;
        turns.head = turns.head.previous; //Since the draw function ends turn regardless if the person exploded or not
        //this must be set to previous in order to not skip a person
    }

    public DoublyLinkedList getTurns(){
        return turns;
    }


    public void setNextTurn(Player target){
        //TODO
    }

    public void endTurn(){
        turns.head = turns.head.next;
    }

}
