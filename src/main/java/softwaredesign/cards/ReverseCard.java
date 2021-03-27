package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

import java.util.Collections;

public class ReverseCard extends Card{
    public void action(ServerHeldGame heldGame, String target) throws InterruptedException {
        Player currPlayer = heldGame.getCurrentPlayer();
        System.out.println(heldGame.gameManager.getAlivePlayers());
        Collections.reverse(heldGame.gameManager.getAlivePlayers());
        System.out.println(heldGame.gameManager.getAlivePlayers());
        heldGame.gameManager.changeNextTurn(currPlayer);
        System.out.println(heldGame.gameManager.getAlivePlayers());

    }
}
