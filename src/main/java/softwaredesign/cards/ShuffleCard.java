package softwaredesign.cards;

import softwaredesign.ServerHeldGameManager;

public class ShuffleCard extends Card {
    public void action(ServerHeldGameManager gameManager) {
        gameManager.mainDeck.reshuffle();
    }
}
