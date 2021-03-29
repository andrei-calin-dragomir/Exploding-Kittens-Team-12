package softwaredesign.gui;

import java.util.Random;

/**
 * Provides the right Url for the image of the card requested.
 */
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
            res = res.concat(card+ "1.png");
        }
        return res;
    }
}
