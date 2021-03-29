package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.core.State;
import softwaredesign.server.ServerHeldGame;

public class AttackCard extends Card {
    public void action(ServerHeldGame heldGame, String target) throws InterruptedException {
        Player originalPlayer = heldGame.getCurrentPlayer();
        Player targetPlayer = heldGame.gameManager.getPlayerByName(target);

        heldGame.gameManager.changeNextTurn(targetPlayer);
        targetPlayer.setPlayerState(State.ATTACKED);
        if(originalPlayer.getPlayerState().equals(State.ATTACKED)) originalPlayer.setPlayerState(State.PLAYING);

        heldGame.getRoom().sendMsgToPlayer(targetPlayer, "ATTACKED " + heldGame.getCurrentPlayer().getName());
        if(!originalPlayer.isComputer() && targetPlayer.isComputer()) heldGame.handleNextTurn();
        else heldGame.getRoom().sendMsgToRoom(null, "TURN " + heldGame.getCurrentPlayerName());
    }
}
