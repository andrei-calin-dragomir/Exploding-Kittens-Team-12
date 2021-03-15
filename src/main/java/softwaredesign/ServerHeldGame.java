package softwaredesign;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class ServerHeldGame {
    private GameManager gameManager;
    private Deck mainDeck;
    private DiscardDeck discardDeck;
    private Scanner scanner = new Scanner(System.in);
    private Random rand = new Random();

    public void start(int sizeOfGame, int numberOfComputers) throws IOException, InterruptedException {

        mainDeck = new Deck();
        discardDeck = new DiscardDeck();
        gameManager = new GameManager();

        System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn" );
        while(gameManager.getAlivePlayers().size() != 1){
            System.out.println("Enter action: ");
            if(scanner.hasNextLine()) {
                String action = scanner.nextLine().toLowerCase().trim();
                if (action.split("\\s+")[0].equals("draw")) {
                    Card cardDrawn = mainDeck.draw();
                    gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
                    if(cardDrawn.equals(new exploding_kitten())){
                        System.out.println("You drew an exploding kitten! You have to defuse it!");
                        while(gameManager.getCurrentPlayerHand().contains(new exploding_kitten())){
                            if(!gameManager.getCurrentPlayerHand().contains(new defuse())){ //TODO the section below needs some IO
                                TimeUnit.MILLISECONDS.sleep(1);
                                System.out.println("You have no defuse cards!");
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println(gameManager.getCurrentPlayer().getName() + " exploded!");
                                gameManager.killPlayer(gameManager.getCurrentPlayer());
                                TimeUnit.MILLISECONDS.sleep(1);
                                break;
                            }
                            action = scanner.nextLine().toLowerCase().trim();
                            if(action.equals("hand")) printHand(gameManager.getCurrentPlayerHand());  //i think this could be turned into a function
                            else if(action.split("\\s+")[0].equals("play")) {                   //in order to avoid repeating code from below
                                if(action.split("\\s+").length != 2) return;
                                int cardIndex = Integer.parseInt(action.split("\\s+")[1]);
                                discardDeck.discardCard(gameManager.getCurrentPlayerHand().playCard(cardIndex, mainDeck));
                            }
                            else System.out.println("Unknown action, please try again.");
                        }
                    }
                    else {
                        System.out.println("You drew a: " + cardDrawn.getName() + " card.");
                    }
                    gameManager.endTurn();
                    System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn" );
                }

                else if (action.equals("deck")) {
                    System.out.println("The deck has " + (mainDeck.getDeckSize()) + " cards left.");
                }
                else if(action.equals("players")){System.out.println("numplayers = " + gameManager.getAlivePlayers().size());} //TODO some IO here too
                else if(action.equals("ddeck")){System.out.println("dd" + discardDeck.top().getName());}
                else if(action.equals("hand")){printHand(gameManager.getCurrentPlayerHand());}
                else if(action.split("\\s+")[0].equals("play")) {
                    if(action.split("\\s+").length != 2) return;
                    int cardIndex = Integer.parseInt(action.split("\\s+")[1]);
                    discardDeck.discardCard(
                            gameManager.getCurrentPlayerHand().playCard(cardIndex, mainDeck)
                    );
                }
                else System.out.println("Unknown action, please try again.");
            }
        }
        System.out.println(gameManager.getAlivePlayers().get(0).getName() + " won!");
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
