package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

public class DefuseCard extends Card {
    public void action(ServerHeldGame heldGame) {
        heldGame.setExplodingBool(false);
        Player currentPlayer = heldGame.gameManager.getCurrentPlayer();
        heldGame.gameManager.removeCurrentPlayerCard(new ExplodingKittenCard());
        heldGame.getRoom().sendMsgToPlayer(currentPlayer,"PLACEKITTEN");
    }
}
