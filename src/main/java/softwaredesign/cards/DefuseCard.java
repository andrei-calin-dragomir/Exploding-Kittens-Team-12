package softwaredesign.cards;

import softwaredesign.ServerHandler;
import softwaredesign.ServerHeldGameManager;

public class DefuseCard extends Card {
    public void action(ServerHeldGameManager gameManager) {
        gameManager.getCurrentPlayer().getHand().removeCard(new ExplodingKittenCard());
        ServerHandler.sendMessageToSingleRoomClient(gameManager.getCurrentPlayer().getName(), "PLACEKITTEN");
    }
}
