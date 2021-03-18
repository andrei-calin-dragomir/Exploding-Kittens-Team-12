package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

import java.util.ArrayList;

public class SeeTheFutureCard extends Card {
    public void action(ServerHeldGame heldGame) {
        heldGame.gameManager.getCurrentPlayer().getHand().removeCard(new SeeTheFutureCard());
        heldGame.getRoom().sendMessageToSingleRoomClient(heldGame.gameManager.getCurrentPlayer().getName(),
                "SEEFUTURE " + heldGame.gameManager.getTopThreeCards());
    }
}
