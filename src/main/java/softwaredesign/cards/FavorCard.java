package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class FavorCard extends Card {
    public void action(ServerHeldGame heldGame) {
        System.out.println(className);
    }
}
