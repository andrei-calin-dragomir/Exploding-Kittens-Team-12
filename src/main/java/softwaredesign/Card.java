package softwaredesign;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class Card {
    String className = this.getClass().getSimpleName();
    abstract int getAmount();
    abstract String action(Deck currDeck);
}

class exploding_kitten extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class defuse extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class attack extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class nope extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class shuffle extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class favor extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class skip extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class see_future extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        ArrayList<Card> deck = currDeck.getFullDeck();
        String futureCards = deck.get(0).getClass().toString() + " - " + deck.get(1).getClass().toString() + " - " + deck.get(2).getClass().toString();
        return futureCards;
    }
    public String getName(){
        return (className);
    }
}

class alter_future extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
    }
}

class cat_card extends Card {
    int deckAmount = 0;
    public int getAmount(){
        return deckAmount;
    }
    public String action(Deck currDeck){
        return (className);
    }
    public String getName(){
        return (className);
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

