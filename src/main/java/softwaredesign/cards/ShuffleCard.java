package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class ShuffleCard extends Card {
    public void action(ServerHeldGame heldGame, String target) { heldGame.gameManager.mainDeck.reshuffle(); }
}
