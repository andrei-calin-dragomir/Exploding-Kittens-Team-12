package softwaredesign.cards;

import softwaredesign.ServerHeldGameManager;

public class AttackCard extends Card {
    public void action(ServerHeldGameManager gameManager) {
        System.out.println(className);
    }
}
