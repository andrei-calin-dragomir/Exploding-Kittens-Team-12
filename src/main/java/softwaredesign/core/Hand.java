package softwaredesign.core;

import org.jetbrains.annotations.NotNull;
import softwaredesign.cards.Card;
import softwaredesign.server.ServerHeldGame;

import java.util.ArrayList;
import java.util.Iterator;

public class Hand implements Iterable<Card>{
    ArrayList<Card> currentHand = new ArrayList<>();

    public boolean contains(Card target){
        return currentHand.contains(target);
    }
    public boolean isEmpty(){ return currentHand.isEmpty(); }
    public Integer indexOf(Card target) { return currentHand.indexOf(target); }
    public Iterator<Card> iterator() { return currentHand.iterator(); }


    public Hand(Deck newDeck){
        currentHand.add(newDeck.getDefuse());
        currentHand.addAll(newDeck.getStartCards());
    }

    public void addToHand(Card cardToAdd){
        currentHand.add(cardToAdd);
    }

    public ArrayList<Card> getHand(){
        return currentHand;
    }

    public int getHandSize(){
        return currentHand.size();
    }

    public Card getCard(int index) { return currentHand.get(index); }

    public <T> void removeCard(T index){
        try{
            int indexInt = (int) index;
            currentHand.remove(indexInt);
        }
        catch(Exception e){
            currentHand.remove(index);
        }
    }

    public Card playCard(int index, String target, ServerHeldGame heldGame) throws InterruptedException {
        Card playCard = getCard(index);
        removeCard(index);
        heldGame.gameManager.discardDeck.discardCard(playCard);
        System.out.println("Target: " + target + " Playing card: " + playCard.getName());
        playCard.action(heldGame, target);
        return playCard;
    }

    // Needs changing or removal
    public void printHand(){
        if(isEmpty()) System.out.println("Your hand is empty");
        else{
            System.out.println("Your hand consists off:");
            this.getHand().forEach(x -> System.out.printf("%s - ", x.getName()));
            System.out.print("\b\b\n");
        }
    }

}
