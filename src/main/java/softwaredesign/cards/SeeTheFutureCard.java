package softwaredesign.cards;

import softwaredesign.core.Deck;
import softwaredesign.core.Player;
import softwaredesign.server.ServerHeldGame;

import java.util.ArrayList;

public class SeeTheFutureCard extends Card {
    public void action(ServerHeldGame heldGame, String target) {
        Deck mainDeck = heldGame.gameManager.mainDeck;
        Player currPlayer = heldGame.gameManager.getCurrentPlayer();
        String messageToSend = "";

        for(Card card : mainDeck){
            messageToSend += card.getName() + " ";
            if(messageToSend.split(" ").length == 3) break;
        }
        System.out.println(messageToSend);
        heldGame.getRoom().sendMsgToPlayer(currPlayer,"SEEFUTURE " + messageToSend);
    }
}
