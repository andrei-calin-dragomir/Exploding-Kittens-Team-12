package softwaredevelopmentvu.explodingkittens.game.cards;

import softwaredevelopmentvu.explodingkittens.game.Deck;
import softwaredevelopmentvu.explodingkittens.game.Hand;

public class DefuseCard extends Card {
    public void action(Deck currDeck, Hand playerHand) {
        if (playerHand.getHand().contains(new ExplodingKittenCard())) {
            System.out.println("You defused the exploding kitten");
            playerHand.removeCard(new ExplodingKittenCard());
        } else System.out.println("You have no exploding kitten in hand");
    }
}
