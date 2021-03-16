package softwaredesign.cards;

import softwaredesign.ServerHeldGameManager;

import java.util.ArrayList;

public abstract class Card {
    public String className = this.getClass().getSimpleName();
    int deckAmount = 0;

    @SuppressWarnings("unused")
    public int getAmount(){
        return deckAmount;
    }
    public abstract void action(ServerHeldGameManager gameManager);

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }

}


