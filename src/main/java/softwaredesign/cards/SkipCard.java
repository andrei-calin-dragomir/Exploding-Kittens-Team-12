package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class SkipCard extends Card {
    public void action(ServerHeldGame heldGame) throws InterruptedException { heldGame.nextTurn(); }
}
