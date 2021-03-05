package softwaredesign;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> currentHand = new ArrayList<>();

    public boolean contains(Card target){
        return currentHand.contains(target);
    }
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

    public Card getCard(int index){
        if(getHandSize() - 1 < index){
            return null;
        }
        return currentHand.get(index);
    }

    public <T> void removeCard(T index){
        try{
            int indexInt = (int) index;
            currentHand.remove(indexInt);
        }
        catch(Exception e){
            currentHand.remove(index);
        }
    }

    public Card playCard(int index, Deck newDeck){
        Card playCard = getCard(index - 1);
        if(playCard == null){
            System.out.println("You only have " + getHandSize() + " cards in your hand.");
            return null;
        }
        else {
            removeCard(index - 1);
            playCard.action(newDeck, this);
            return playCard;
        }
    }
}
