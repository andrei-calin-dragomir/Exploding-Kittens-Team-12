package softwaredesign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerHeldGameManager{
    public List<Player> alivePlayers = new ArrayList<>();
    public DoublyLinkedList turns = new DoublyLinkedList();
    public Deck mainDeck;
    public DiscardDeck discardDeck;

    public void addPlayers() throws IOException, InterruptedException{
        mainDeck = new Deck();
        discardDeck = new DiscardDeck();
        for(int i = 0; i < ServerHandler.roomPlayerList.size(); i++){
            alivePlayers.add(new Player());
            getAlivePlayers().get(i).setName(ServerHandler.roomPlayerList.get(i));
            getAlivePlayers().get(i).initHand(mainDeck);
            ServerHandler.sendMessageToSingleRoomClient(getAlivePlayers().get(i).getName(),
                    "UPDATEHAND " + createHandAsString(getAlivePlayers().get(i)));
            getTurns().addNode(getAlivePlayers().get(i));
        }
    }

    public String createHandAsString(Player player) {
        System.out.println(player.getHand().getHandSize());
        String constructorHand = new String("");
        for(int i = 0;i < player.getHand().getHandSize() - 1;i++){
            constructorHand += (player.getHand().getCard(i).className + " ");
        }
        constructorHand += (player.getHand().getCard(player.getHand().getHandSize()-1).className);
        System.out.println(constructorHand);
        return constructorHand;
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
