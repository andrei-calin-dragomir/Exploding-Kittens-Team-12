package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

import java.util.Random;

public class FavorCard extends Card {
    public void action(ServerHeldGame heldGame, String targetName) {
        Player target = heldGame.gameManager.getPlayerByName(targetName);
        heldGame.getRoom().sendMsgToPlayer(target, "TARGETED " + heldGame.getCurrentPlayer().getName());
        if(target.isComputer()) {
            Random rand = new Random();
            heldGame.giveCard(rand.nextInt(target.getHand().getHandSize()), heldGame.getCurrentPlayerName(), targetName);
        }
    }
}