package softwaredesign.cards;

import softwaredesign.server.ServerHeldGame;

public class NopeCard extends Card {
    public void action(ServerHeldGame heldGame) {
        System.out.println(className);
    } //Hopefully will not be implemented
}
