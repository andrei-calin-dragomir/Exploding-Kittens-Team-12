package softwaredesign;

import java.util.ArrayList;

public class Hand {
    ArrayList<Card> currentHand = new ArrayList<>();

    public Hand(Deck newDeck){
        currentHand.add(newDeck.getDefuse());
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

    public Card getCard(int index){
        if(getHandSize() - 1 < index){
            return null;
        }
        return currentHand.get(index);
    }

    public <T> void removeCard(T index){
        currentHand.remove(index);
    }

    public void playCard(int index, Deck newDeck){
        Card playCard = getCard(index - 1);
        if(playCard == null) System.out.println("You only have " + getHandSize() + " cards in your hand.");
        else playCard.action(newDeck, this);
        removeCard(index - 1);
    }
}
