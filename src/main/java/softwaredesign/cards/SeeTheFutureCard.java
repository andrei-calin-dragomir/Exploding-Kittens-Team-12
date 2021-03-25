package softwaredesign.cards;

import softwaredesign.core.Deck;
import softwaredesign.server.ServerHeldGame;

import java.util.ArrayList;

public class SeeTheFutureCard extends Card {
    public void action(ServerHeldGame heldGame) {
        heldGame.gameManager.removeCurrentPlayerCard(new SeeTheFutureCard());
        Deck mainDeck = heldGame.gameManager.mainDeck;
        String playerName = heldGame.gameManager.getCurrentPlayer().getName();
        String messageToSend;

        if (mainDeck.getDeckSize() <= 3) {
            ArrayList<String> allPlayers = new ArrayList<>();
            for (Card card : mainDeck.getFullDeck()) allPlayers.add(card.getName());
            messageToSend = String.join(" ", allPlayers);
        } else messageToSend = mainDeck.getFullDeck().get(0).getName() + " " +
                mainDeck.getFullDeck().get(1).getName() + " " +
                mainDeck.getFullDeck().get(2).getName();

        heldGame.getRoom().sendMsgToPlayer(playerName,"SEEFUTURE " + messageToSend);
    }
}
