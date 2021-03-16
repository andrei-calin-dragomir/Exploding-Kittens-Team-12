package softwaredesign;

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

    public boolean isEmpty(){ return d_deck.isEmpty(); }

    public boolean isTopDefuse(){
        if(!isEmpty())
            if(d_deck.get(0).getName().equals("defuse"))
                return true;
        return false;
    }
    public String top() {
        try {
            return d_deck.get(0).getName();
        }
        catch (Exception NullPointerException){
            return "Empty deck";
        }
    }
    public Card getTopCard(){
        return d_deck.get(d_deck.size() - 1);
    }
}
