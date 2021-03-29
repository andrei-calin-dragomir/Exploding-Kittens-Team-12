package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class SkipCard extends Card {
    public void action(ServerHeldGame heldGame, String target) throws InterruptedException {
        if(heldGame.getCurrentPlayer().isComputer()) heldGame.gameManager.endTurn();
        else heldGame.nextTurn();
    }
}
