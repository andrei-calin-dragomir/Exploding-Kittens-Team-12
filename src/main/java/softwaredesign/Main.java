package softwaredesign;


import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Deck newDeck = new Deck();
        Hand myHand = new Hand();
        Scanner myObj = new Scanner(System.in);
        Boolean quit = false;
        while(!quit){
            System.out.println("Enter action: ");
            if(myObj.hasNextLine()) {
                String action = myObj.nextLine().toLowerCase().trim();
                if (action.equals("q") || action.equals("quit")) {
                    quit = true;
                } else if (action.equals("draw") || action.equals("d")) {
                    Card cardDrawn = newDeck.draw();
                    myHand.addToHand(cardDrawn);
                    System.out.println("You drew a: " + cardDrawn.action(newDeck) + " card.");
                } else if (action.equals("deck")) {
                    System.out.println("The deck has " + (newDeck.getDeckSize()) + " cards left.");
                }
                else if(action.equals("hand")){
                    if(myHand.getHand().isEmpty()){
                        System.out.println("Your hand is empty");
                        continue;
                    }
                    System.out.println("Your hand consists off:\n");
                    myHand.getHand().forEach(x -> System.out.printf("%s - ", x.action(newDeck)));
                    System.out.print("\b\b\n");
                }
            }
        }

    }

}
