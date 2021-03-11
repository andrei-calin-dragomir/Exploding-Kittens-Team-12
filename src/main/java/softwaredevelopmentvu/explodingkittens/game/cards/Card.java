package softwaredevelopmentvu.explodingkittens.game.cards;

import softwaredevelopmentvu.explodingkittens.game.Deck;
import softwaredevelopmentvu.explodingkittens.game.Hand;

import java.util.ArrayList;

public abstract class Card {
    protected String className = this.getClass().getSimpleName();
    private int deckAmount = 0;

    @SuppressWarnings("unused")
    public int getAmount(){
        return deckAmount;
    }
    public abstract void action(Deck currDeck, Hand playerHand);

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }

}

