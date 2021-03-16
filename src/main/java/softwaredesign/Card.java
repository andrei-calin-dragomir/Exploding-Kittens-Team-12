package softwaredesign;

import java.util.ArrayList;

public abstract class Card {
    String className = this.getClass().getSimpleName();
    int deckAmount = 0;

    @SuppressWarnings("unused")
    public int getAmount(){
        return deckAmount;
    }
    abstract void action(ServerHeldGameManager gameManager);

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }

}

class exploding_kitten extends Card {
    public void action(ServerHeldGameManager gameManager){
        System.out.println(className);
    }

}

class defuse extends Card {
    public void action(ServerHeldGameManager gameManager){
        gameManager.getCurrentPlayer().getHand().removeCard(new exploding_kitten());
        ServerHandler.sendMessageToSingleRoomClient(gameManager.getCurrentPlayer().getName(),"PLACEKITTEN");
    }
}

class attack extends Card {
    public void action(ServerHeldGameManager gameManager){
        System.out.println(className);
    }
}

class nope extends Card {
    public void action(ServerHeldGameManager gameManager){
        System.out.println(className);
    }
}

class shuffle extends Card {
    public void action(ServerHeldGameManager gameManager){
        gameManager.mainDeck.reshuffle();
    }
}

class favor extends Card {
    public void action(ServerHeldGameManager gameManager){
        System.out.println(className);
    }
}

class skip extends Card {
    public void action(ServerHeldGameManager gameManager){
        System.out.println(className);
    }
}

class see_future extends Card {
    public void action(ServerHeldGameManager gameManager){
        ArrayList<Card> deck = gameManager.mainDeck.cardDeck;
        String futureCards = deck.get(0).getName() + " " + deck.get(1).getName() + " " + deck.get(2).getName(); }
}

class cat_card extends Card {
    public void action(ServerHeldGameManager gameManager){
        System.out.println(className);
    }
}

class catermelon extends cat_card {}
class taco_cat extends cat_card {}
class potato_cat extends cat_card {}
class rainbow_cat extends cat_card {}
class beard_cat extends cat_card {}

