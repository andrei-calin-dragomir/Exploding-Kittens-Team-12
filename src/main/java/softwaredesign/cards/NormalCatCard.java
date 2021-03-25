package softwaredesign.cards;

import softwaredesign.core.Hand;
import softwaredesign.server.ServerHeldGame;

public class NormalCatCard extends Card {
    public void action(ServerHeldGame heldGame) throws InterruptedException {
        Hand hand = heldGame.gameManager.getCurrentPlayerHand();
        if(heldGame.gameManager.discardDeck.top().equals(this.className)){
            hand.addToHand(new FavorCard());    //Since multiple inheritance isnt allowed in Java, this is a solution
            int index = hand.getHand().size() -1;
            hand.playCard(index, heldGame);
            hand.removeCard(index);             //If some other class removes the card from the hand, this can be removed
        }
    }
}
