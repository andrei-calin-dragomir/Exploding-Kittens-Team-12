package softwaredevelopmentvu.explodingkittens.gui;


import softwaredevelopmentvu.explodingkittens.game.cards.Card;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CardHelper {

    static String getCardImageUrl(Card card){
        Random rand = new Random();
        String res;
        if(card.getName().equals("CatCardShy")
                || card.getName().equals("CatCardZombie")
                || card.getName().equals("CatCardMomma")){
            res = "file:src/main/resources/images/normal/"
                    + card.getName()
                    + ".png";
            System.out.println("res = " + res);
        } else {
            res = "file:src/main/resources/images/normal/"
                    + card.getName()
                    + (rand.nextInt(3) + 1)
                    + ".png";
            System.out.println("res = " + res);
        }
        return res;
    }
}
