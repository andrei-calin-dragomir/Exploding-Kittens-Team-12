package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class ExplodingKittenCard extends Card {
    public void action(ServerHeldGame heldGame, String target) {
        // You cannot "play" this card, it is handled before you get the chance to play it, hence why there is nothing here.
    }
}
