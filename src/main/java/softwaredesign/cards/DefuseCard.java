package softwaredesign.cards;

import softwaredesign.ServerHandler;
import softwaredesign.ServerHeldGameManager;

import static softwaredesign.ServerHandler.sendMessageToSingleRoomClient;

public class DefuseCard extends Card {
    public void action(ServerHeldGameManager gameManager) throws InterruptedException {
        gameManager.getCurrentPlayer().getHand().removeCard(new ExplodingKittenCard());
        sendMessageToSingleRoomClient(gameManager.getCurrentPlayer().getName(),"PLACEKITTEN");
    }
}
