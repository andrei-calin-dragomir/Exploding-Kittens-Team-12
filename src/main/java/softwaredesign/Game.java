package softwaredesign;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Random;


public class Game {
    private GameManager gameManager;
    private Deck mainDeck;
    private DiscardDeck discardDeck;
    private Scanner scanner = new Scanner(System.in);
    private Random rand = new Random();

    public void start(int numberOfPlayers) throws IOException, InterruptedException {

        mainDeck = new Deck();
        discardDeck = new DiscardDeck();
        gameManager = new GameManager();
        gameManager.addPlayers(numberOfPlayers);

        for(int i = 0; i < numberOfPlayers; i++) {
            System.out.println("Choose a name for player " + (i+1));
            gameManager.getAlivePlayers().get(i).setName(scanner.nextLine());
            gameManager.getAlivePlayers().get(i).initHand(mainDeck);
            gameManager.getTurns().addNode(gameManager.getAlivePlayers().get(i));
        }


        System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn" );
        while(gameManager.getAlivePlayers().size() != 1){
            System.out.println("Enter action (draw | deck | players | ddeck | hand | play): ");
            if(scanner.hasNextLine()) {
                String action = scanner.nextLine().toLowerCase().trim();
                if (action.split("\\s+")[0].equals("draw")) {
                    Card cardDrawn = mainDeck.draw();
                    gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
                    boolean playerExploded = false;
                    boolean drewExplodingKitten;
                    if(cardDrawn.equals(new exploding_kitten())){
                        drewExplodingKitten = true;
                        System.out.println("You drew an exploding kitten! You have to defuse it!");
                        while(gameManager.getCurrentPlayerHand().contains(new exploding_kitten())){
                            if(!gameManager.getCurrentPlayerHand().contains(new defuse())){
                                TimeUnit.MILLISECONDS.sleep(1);
                                System.out.println("You have no defuse cards!");
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println(gameManager.getCurrentPlayer().getName() + " exploded!");
                                gameManager.killPlayer(gameManager.getCurrentPlayer());
                                TimeUnit.MILLISECONDS.sleep(1);
                                playerExploded = true;
                                break;
                            }
                            action = scanner.nextLine().toLowerCase().trim();
                            if(action.equals("hand")){gameManager.getCurrentPlayerHand().printHand();}
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
                    else {
                        drewExplodingKitten = false;
                        System.out.println("You drew a: " + cardDrawn.getName() + " card.");
                    }
                    if(discardDeck.top() != null && discardDeck.top().getName().equals("defuse")
                       && !playerExploded && drewExplodingKitten
                      ){
                        System.out.println("Choose a location to place the card\n" +
                                "(1 - on top; 2 - second; 3 - bottom; 4 - random");
                        String index = scanner.nextLine().toLowerCase().trim();
                        switch(Integer.parseInt(index)){
                            case 4:
                                mainDeck.insertCard(new exploding_kitten(),rand.nextInt(mainDeck.getDeckSize()));
                                break;
                            case 3:
                                mainDeck.insertCard(new exploding_kitten(),mainDeck.getDeckSize());
                                break;
                            default:
                                mainDeck.insertCard(new exploding_kitten(),Integer.parseInt(index)-1);
                                break;
                        }
                    }
                    gameManager.endTurn();
                    System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn" );
                }

                else if (action.equals("deck")) {
                    System.out.println("The deck has " + (mainDeck.getDeckSize()) + " cards left.");
                }
                else if(action.equals("hand")){gameManager.getCurrentPlayerHand().printHand();}
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

}
