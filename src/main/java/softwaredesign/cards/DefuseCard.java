package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class DefuseCard extends Card {
    public void action(ServerHeldGame heldGame) {
        String playerName = heldGame.gameManager.getCurrentPlayer().getName();
        heldGame.gameManager.removeCurrentPlayerCard(new ExplodingKittenCard());
        heldGame.getRoom().sendMessageToSingleRoomClient(playerName,"PLACEKITTEN");
    }
}
