package softwaredesign.cards;

import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

import java.util.Collections;

public class ReverseCard extends Card{
    public void action(ServerHeldGame heldGame, String target) throws InterruptedException {
        Player currPlayer = heldGame.getCurrentPlayer();
        Collections.reverse(heldGame.gameManager.getAlivePlayers());
        heldGame.gameManager.changeNextTurn(currPlayer);
        if(heldGame.gameManager.getAlivePlayers().size() == 2){
            Card skip = new SkipCard();
            skip.action(heldGame, "");
        }
    }
}
