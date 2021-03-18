package softwaredesign.gui;


import softwaredesign.cards.Card;

import java.util.Random;

public class CardHelper {

    static String getCardImageUrl(Card card){
        Random rand = new Random();
        String res = "file:resources/images/normal/";
        if(card.getName().equals("CatCardShy")
                || card.getName().equals("CatCardZombie")
                || card.getName().equals("CatCardMomma")){
            res = res.concat(card.getName() + ".png");
        } else {
            res = res.concat(
                    card.getName()
                    + (rand.nextInt(3) + 1)
                    + ".png"
            );
        }
        System.out.println("res = " + res);
        return res;
    }
}
