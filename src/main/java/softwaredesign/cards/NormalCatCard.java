package softwaredesign.cards;

import softwaredesign.ServerHeldGameManager;

public class NormalCatCard extends Card {
    public void action(ServerHeldGameManager gameManager) {
        System.out.println(className);
    }
}
