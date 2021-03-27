package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.core.State;
import softwaredesign.server.ServerHeldGame;

public class AttackCard extends Card {
    public void action(ServerHeldGame heldGame, String targetIndex) throws InterruptedException {
        Player originalPlayer = heldGame.getCurrentPlayer();
        Player target = heldGame.getPlayer(Integer.parseInt(targetIndex));
        heldGame.gameManager.changeNextTurn(target);
        target.setPlayerState(State.ATTACKED);
        heldGame.getRoom().sendMsgToPlayer(target, "ATTACKED " + heldGame.getCurrentPlayer().getName());
        if(!originalPlayer.isComputer() && target.isComputer()) heldGame.handleNextTurn();
    }
    //Will be implemented after linked list change
}
