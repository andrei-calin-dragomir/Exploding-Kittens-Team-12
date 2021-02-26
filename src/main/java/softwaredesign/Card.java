package softwaredesign;

import java.util.ArrayList;

public abstract class Card {
    String className = this.getClass().getSimpleName();
    int deckAmount = 0;

    @SuppressWarnings("unused")
    public int getAmount(){
        return deckAmount;
    }
    abstract void action(Deck currDeck, Hand playerHand);

    @Override
    public boolean equals(Object object){ return object != null && object.getClass() == this.getClass(); }
    public String getName(){ return (className); }

}

class exploding_kitten extends Card {
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }

}

class defuse extends Card {
    public void action(Deck currDeck, Hand playerHand){
        if(playerHand.getHand().contains(new exploding_kitten())){
            System.out.println("You defused the exploding kitten");
            playerHand.removeCard(new exploding_kitten());
        }
        else System.out.println("You have no exploding kitten in hand");
    }
}

class attack extends Card {
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class nope extends Card {
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class shuffle extends Card {
    public void action(Deck currDeck, Hand playerHand){
        currDeck.reshuffle();
    }
}

class favor extends Card {
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class skip extends Card {
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class see_future extends Card {
    public void action(Deck currDeck, Hand playerHand){
        ArrayList<Card> deck = currDeck.getFullDeck();
        String futureCards = deck.get(0).getName() + " - " + deck.get(1).getName() + " - " + deck.get(2).getName();
        System.out.println("The top 3 cards are: " + futureCards);
    }
}

class cat_card extends Card {
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class catermelon extends cat_card {}
class taco_cat extends cat_card {}
class potato_cat extends cat_card {}
class rainbow_cat extends cat_card {}
class beard_cat extends cat_card {}

