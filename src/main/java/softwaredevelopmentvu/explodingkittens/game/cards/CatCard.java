package softwaredevelopmentvu.explodingkittens.game.cards;

import softwaredevelopmentvu.explodingkittens.game.Deck;
import softwaredevelopmentvu.explodingkittens.game.Hand;

public abstract class CatCard extends Card {
    public void action(Deck currDeck, Hand playerHand) {
        System.out.println("To be implemented");
    }
}
