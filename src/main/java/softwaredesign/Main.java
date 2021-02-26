package softwaredesign;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Deck newDeck = new Deck();
        Hand myHand = new Hand(newDeck);
        printHand(myHand);
        Scanner scanObj = new Scanner(System.in);
        boolean quit = false;
        while(!quit){
            System.out.println("Enter action: ");
            if(scanObj.hasNextLine()) {
                String action = scanObj.nextLine().toLowerCase().trim();
                if (action.equals("q") || action.equals("quit")) {
                    quit = true;
                } else if (action.split("\\s+")[0].equals("draw")) {
                    int repeat = 1;
                    if(action.split("\\s+").length == 2) repeat = Integer.parseInt(action.split("\\s+")[1]);
                    for(int i = 0; i < repeat; i++){
                        Card cardDrawn = newDeck.draw();
                        myHand.addToHand(cardDrawn);
                        System.out.println("You drew a: " + cardDrawn.getName() + " card.");
                    }
                } else if (action.equals("deck")) {
                    System.out.println("The deck has " + (newDeck.getDeckSize()) + " cards left.");
                }
                else if(action.equals("hand")){
                    printHand(myHand);
                }
                else if(action.split("\\s+")[0].equals("play")) {
                    if(action.split("\\s+").length != 2) return;
                    int cardIndex = Integer.parseInt(action.split("\\s+")[1]);
                    myHand.playCard(cardIndex, newDeck);
                }
                else System.out.println("Unknown action, please try again.");
            }
        }

    }

    public static void printHand(Hand myHand){
        if(myHand.getHand().isEmpty()) System.out.println("Your hand is empty");
        else{
            System.out.println("Your hand consists off:");
            myHand.getHand().forEach(x -> System.out.printf("%s - ", x.getName()));
            System.out.print("\b\b\n");
        }
    }
}
