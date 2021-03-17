package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class DefuseCard extends Card {
    public void action(ServerHeldGame heldGame) throws InterruptedException {
        heldGame.gameManager.getCurrentPlayer().getHand().removeCard(new ExplodingKittenCard());
        heldGame.getRoom().sendMessageToSingleRoomClient(heldGame.gameManager.getCurrentPlayer().getName(),"PLACEKITTEN");
    }
}
