package softwaredesign.gui;

import java.util.Random;

public class CardHelper {

    static String getCardImageUrl(String card){
        Random rand = new Random();
        String res = "file:resources/images/normal/";
        if(card.equals("CatCardShy")
                || card.equals("CatCardZombie")
                || card.equals("CatCardMomma")
                || card.equals("ReverseCard")){
            res = res.concat(card + ".png");
        } else {
            res = res.concat(
                    card
                    + (rand.nextInt(3) + 1)
                    + ".png"
            );
        }
//        System.out.println("res = " + res);
        return res;
    }
}
