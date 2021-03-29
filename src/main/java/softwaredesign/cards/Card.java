package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public abstract class Card {
    private String className = this.getClass().getSimpleName();
    private int deckAmount = 0;

    @SuppressWarnings("unused")
    public int getAmount(){
        return deckAmount;
    }
    public abstract void action(ServerHeldGame gameManager, String target) throws InterruptedException;

    public void setDeckAmount(int deckAmount) { this.deckAmount = deckAmount; }

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }

}


