package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

public class FavorCard extends Card {
    public void action(ServerHeldGame heldGame, String targetName) {
        Player target = heldGame.gameManager.getPlayerByName(targetName);
        heldGame.getRoom().sendMsgToPlayer(target, "TARGETED " + heldGame.getCurrentPlayer().getName());
    }
}