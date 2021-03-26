package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public abstract class Card {
    public String className = this.getClass().getSimpleName();
    int deckAmount = 0;

    @SuppressWarnings("unused")
    public int getAmount(){
        return deckAmount;
    }
    public abstract void action(ServerHeldGame gameManager, String target) throws InterruptedException;

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }

}


