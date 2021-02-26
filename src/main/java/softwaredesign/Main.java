package softwaredesign;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Deck newDeck = new Deck();
        Hand myHand = new Hand(newDeck);
        Scanner myObj = new Scanner(System.in);
        Boolean quit = false;
        while(!quit){
            System.out.println("Enter action: ");
            if(myObj.hasNextLine()) {
                String action = myObj.nextLine().toLowerCase().trim();
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
                    if(myHand.getHand().isEmpty()){
                        System.out.println("Your hand is empty");
                        continue;
                    }
                    System.out.println("Your hand consists off:\n");
                    myHand.getHand().forEach(x -> System.out.printf("%s - ", x.getName()));
                    System.out.print("\b\b\n");
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

}
