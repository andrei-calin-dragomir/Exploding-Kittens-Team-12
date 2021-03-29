package softwaredesign.core;

import softwaredesign.cards.Card;

import java.util.ArrayList;
import java.util.List;

// Handles the discard pile, it's not that complicated because it isn't manipulated often.
public class DiscardDeck {
    private List<Card> d_deck = new ArrayList<>();

    public void discardCard(Card card){
        d_deck.add(card);
    }

    public boolean isEmpty(){ return d_deck.isEmpty(); }

    public Card getTopCard(){
        if(d_deck.size() == 0) return null;
        return d_deck.get(d_deck.size() - 1);
    }
}
