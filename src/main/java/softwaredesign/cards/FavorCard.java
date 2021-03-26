package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

public class FavorCard extends Card {
    public void action(ServerHeldGame heldGame, String targetIndex) {
        Player target = heldGame.getPlayer(Integer.parseInt(targetIndex));
        heldGame.getRoom().sendMsgToPlayer(target, "TARGETED " + heldGame.getCurrentPlayer().getName());
    }
}