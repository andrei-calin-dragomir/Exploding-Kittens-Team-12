package softwaredevelopmentvu.explodingkittens.game.cards;

import softwaredevelopmentvu.explodingkittens.game.Deck;
import softwaredevelopmentvu.explodingkittens.game.Hand;

public class ExplodingKittenCard extends Card {
    public void action(Deck currDeck, Hand playerHand) {
        System.out.println(className);
    }
}
