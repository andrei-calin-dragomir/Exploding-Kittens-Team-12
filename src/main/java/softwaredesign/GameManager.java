package softwaredesign;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameManager {
    public List<Player> alivePlayers = new ArrayList<>();
    public DoublyLinkedList turns = new DoublyLinkedList();
    public Scanner scanner = new Scanner(System.in);

    public Deck addPlayers(int sizeOfGame,int numberOfComputers,Deck mainDeck) {
        Deck remainingDeck = mainDeck;
        for(int i = 0; i < sizeOfGame; i++){
            alivePlayers.add(new Player());
            if(i == 0) getAlivePlayers().get(i).setName(ClientProgram.username);
            else if(i < (sizeOfGame - numberOfComputers)) {
                System.out.println("Insert player " + i + "'s name here:");
                getAlivePlayers().get(i).setName(scanner.nextLine());
            }else {
                getAlivePlayers().get(i).setName("Computer " + i);
                System.out.println("Added " + getAlivePlayers().get(i).getName());
            }
            getAlivePlayers().get(i).initHand(remainingDeck);
            getTurns().addNode(getAlivePlayers().get(i));
        }
        return remainingDeck;
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
