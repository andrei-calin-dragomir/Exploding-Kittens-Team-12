package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class ShuffleCard extends Card {
    public void action(ServerHeldGame heldGame) { heldGame.gameManager.mainDeck.reshuffle(); }
}
