package softwaredesign.client;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Deck;
import softwaredesign.core.DiscardDeck;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Random;


public class Game {
    private GameManager gameManager;
    private Deck mainDeck;
    private DiscardDeck discardDeck;
    private Scanner scanner = new Scanner(System.in);
    private Random rand = new Random();
    private boolean drawnExplodingKitten;


    private int validIntInput(){
        int index;
        try{ index = scanner.nextInt(); }
        catch(Exception notAnInt){
            while (!scanner.hasNextInt()){
                System.out.println("Invalid input, try again");
                scanner.next();
            }
            index = scanner.nextInt();
        }
        scanner.nextLine();
        return index;
    }

    public void handleExplodingKitten(){
        System.out.println("You drew an exploding kitten! You have to defuse it!");
        drawnExplodingKitten = true;
        boolean justExploded = false;
        while(gameManager.getCurrentPlayerHand().contains(new ExplodingKittenCard())) {
            if (!gameManager.getCurrentPlayerHand().contains(new DefuseCard())) {
                System.out.println("You have no defuse cards!");
                System.out.println(gameManager.getCurrentPlayer().getName() + " exploded!");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
                justExploded = true;
                break;
            } else {
                handleAction(scanner.nextLine().toLowerCase().trim());
                justExploded = false;
            }
        }
        if(discardDeck.isTopDefuse() && !justExploded){
            System.out.println("Choose a location to place the card\n" +
                    "(1 - on top; 2 - second; 3 - bottom; 4 - random");
            int index = validIntInput();
            while(index > 4 || index < 1){
                System.out.println("Invalid index, try again");
                index = validIntInput();
            }

            switch(index){
                case 4:
                    mainDeck.insertCard(new ExplodingKittenCard(),rand.nextInt(mainDeck.getDeckSize()));
                    break;
                case 3:
                    mainDeck.insertCard(new ExplodingKittenCard(),mainDeck.getDeckSize());
                    break;
                default:
                    mainDeck.insertCard(new ExplodingKittenCard(),index - 1);
                    break;
            }
        }
        drawnExplodingKitten = false;
    }

    public void handleDrawAction(){
        if(!drawnExplodingKitten) {
            Card cardDrawn = mainDeck.draw();
            gameManager.getCurrentPlayerHand().addToHand(cardDrawn);

            if (cardDrawn.equals(new ExplodingKittenCard()))
                handleExplodingKitten();
            else
                System.out.println("You drew a: " + cardDrawn.getName() + " card.");

            gameManager.endTurn();
            System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn");
        }
        else {
            System.out.println("You cannot draw whilst having an exploding kitten in your hand!");
        }
    }

    public void handlePlayAction(int index){
        if(drawnExplodingKitten &&
                !(gameManager.getCurrentPlayerHand().getCard(index - 1).equals(new DefuseCard())))
            System.out.println("You can only play a defuse card if you have an exploding kitten!");

//        else
//            discardDeck.discardCard(
//                    gameManager.getCurrentPlayerHand().playCard(index, mainDeck)
//            );
    }

    public void handleAction(String action){
        switch (action.split("\\s+")[0]){
            case "draw":
                handleDrawAction();
                break;
            case "hand":
                gameManager.getCurrentPlayerHand().printHand();
                break;
            case "players":
                System.out.println("There are " + gameManager.getAlivePlayers().size() + " players alive");
                break;
            case "deck":
                System.out.println("The deck has " + (mainDeck.getDeckSize()) + " cards left.");
                break;
            case "ddeck":
                System.out.println(discardDeck.top());
                break;
            case "play":
                try {
                    int cardIndex = Integer.parseInt(action.split("\\s+")[1]);
                    handlePlayAction(cardIndex);
                }
                catch(Exception invalidInput){ System.out.println("Invalid card index, try again"); }
                break;
            default:
                System.out.println("Unknown action, please try again");
        }
    }

    public void handleComputerAction() throws InterruptedException {
        Card cardDrawn = mainDeck.draw();
        gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
        System.out.println("The Computer drew a card");
        if(cardDrawn.equals(new ExplodingKittenCard())){
            System.out.println("The Computer drew an exploding kitten!");
            if(gameManager.getCurrentPlayerHand().contains(new DefuseCard())){
                int defuseCardIndex = gameManager.getCurrentPlayerHand().getHand().indexOf(new DefuseCard()) + 1;
                handlePlayAction(defuseCardIndex);
                mainDeck.insertCard(new ExplodingKittenCard(),rand.nextInt(mainDeck.getDeckSize()));
                System.out.println("The Computer defused the kitten");
            }
            else{
                System.out.println("The Computer exploded!");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
            }
        }
        TimeUnit.SECONDS.sleep(1); //In order for the text to not go too fast
        gameManager.endTurn();
        System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn");
    }

    public void start(int sizeOfGame, int numberOfComputers) throws IOException, InterruptedException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        mainDeck = new Deck();
        discardDeck = new DiscardDeck();
        gameManager = new GameManager();
        mainDeck = gameManager.addPlayers(sizeOfGame,numberOfComputers,mainDeck);

        System.out.println("It is " + gameManager.getCurrentPlayer().getName() + "'s turn" ); //Initial player
        while(gameManager.getAlivePlayers().size() != 1){
            if(gameManager.getCurrentPlayer().isComputer()){
                handleComputerAction();
            } else {
                System.out.println("Enter action: ");
                if (scanner.hasNextLine())
                    handleAction(scanner.nextLine().toLowerCase().trim());
            }
        }
        System.out.println(gameManager.getAlivePlayers().get(0).getName() + " won!");
    }

}
