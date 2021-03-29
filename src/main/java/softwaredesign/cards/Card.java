package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public abstract class Card {
    private String className = this.getClass().getSimpleName();
    private int deckAmount = 0;     // Says its unused but its used for (de)constructing the deck using gson

    public abstract void action(ServerHeldGame gameManager, String target) throws InterruptedException;

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }
    public void setDeckAmount(int deckAmount) { this.deckAmount = deckAmount; }
}


