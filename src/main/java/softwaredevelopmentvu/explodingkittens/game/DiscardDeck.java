package softwaredevelopmentvu.explodingkittens.game;

import softwaredevelopmentvu.explodingkittens.game.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class DiscardDeck {
    private List<Card> d_deck = new ArrayList<>();

    public List<Card> getDiscardDeck(){
        return this.d_deck;
    }

    public void discardCard(Card card){
        d_deck.add(card);
    }

    public Card top() {
        if(d_deck.size() == 0)
            return null;
        else
            return d_deck.get(0);
    }
}
