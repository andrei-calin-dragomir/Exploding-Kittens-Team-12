package softwaredesign;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Player> alivePlayers = new ArrayList<>();
    private DoublyLinkedList turns = new DoublyLinkedList();

    public void addPlayers(int numberOfPlayers){
        for(int i = 0; i < numberOfPlayers; i++){
            alivePlayers.add(new Player());
        }
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
