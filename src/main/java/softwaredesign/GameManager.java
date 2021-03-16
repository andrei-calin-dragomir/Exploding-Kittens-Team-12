package softwaredesign;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameManager {
    public List<Player> alivePlayers = new ArrayList<>();
    public DoublyLinkedList turns = new DoublyLinkedList();
    public Scanner scanner = new Scanner(System.in);

    public Deck addPlayers(Deck mainDeck) {
        Deck remainingDeck = mainDeck;
        for(int i = 0; i < ServerHandler.roomPlayerList.size(); i++){
            alivePlayers.add(new Player());
            getAlivePlayers().get(i).setName(ServerHandler.roomPlayerList.get(i));
            getAlivePlayers().get(i).initHand(remainingDeck);
            ServerHandler.sendMessageToSingleRoomClient(getAlivePlayers().get(i).getName(),
                    "UPDATEHAND " + createInitialHandAsString(getAlivePlayers().get(i)));
            getTurns().addNode(getAlivePlayers().get(i));
        }
        return remainingDeck;
    }

    private String createInitialHandAsString(Player player) {
        StringBuilder constructorHand = new StringBuilder();
        for(int i = 0;i < player.getHand().getHand().size() - 1;i++){
            constructorHand.append(player.getHand().getHand().get(i)).append(" ");
        }
        constructorHand.append(player.getHand().getHand().get(player.getHand().getHand().size() - 1));
        return constructorHand.toString();
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
