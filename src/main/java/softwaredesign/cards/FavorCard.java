package softwaredesign.cards;

import softwaredesign.ServerHeldGameManager;

public class FavorCard extends Card {
    public void action(ServerHeldGameManager gameManager) {
        System.out.println(className);
    }
}
