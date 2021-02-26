package softwaredesign;

import java.util.ArrayList;

public abstract class Card {
    String className = this.getClass().getSimpleName();
    abstract int getAmount();
    abstract void action(Deck currDeck, Hand playerHand);
    public String getName(){ return (className); }
    @Override
    public boolean equals(Object object){
        if(object != null && object.getClass() == this.getClass()) return true;
        return false;
    }
}

class exploding_kitten extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class defuse extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        if(playerHand.getHand().contains(new exploding_kitten())){
            System.out.println("You defused the exploding kitten");
            playerHand.removeCard(new exploding_kitten());
        }
        else System.out.println("You have no exploding kitten in hand");
    }

}

class attack extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }

}

class nope extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class shuffle extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        currDeck.reshuffle();
    }
}

class favor extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class skip extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class see_future extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        ArrayList<Card> deck = currDeck.getFullDeck();
        String futureCards = deck.get(0).getName() + " - " + deck.get(1).getName() + " - " + deck.get(2).getName();
        System.out.println("The top 3 cards are: " + futureCards);
    }
}

class cat_card extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public void action(Deck currDeck, Hand playerHand){
        System.out.println(className);
    }
}

class catermelon extends cat_card {

}

class taco_cat extends cat_card {


}

class potato_cat extends cat_card {

}

class rainbow_cat extends cat_card {

}

class beard_cat extends cat_card {
}

