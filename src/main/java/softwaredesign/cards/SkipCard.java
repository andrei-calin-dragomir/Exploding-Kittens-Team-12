package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class SkipCard extends Card {
    public void action(ServerHeldGame heldGame) throws InterruptedException {
        if(heldGame.getCurrentPlayer().isComputer()) heldGame.gameManager.endTurn();
        else heldGame.nextTurn();      // Is currently bugged, it skips the turn but then the player auto plays draw. This is because handleComputerAction manipulates the current player, which is the player after a skip card sometimes
    }

}
