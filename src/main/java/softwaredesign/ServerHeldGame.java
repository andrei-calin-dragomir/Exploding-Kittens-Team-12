package softwaredesign;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Random;


public class ServerHeldGame {
    public ServerHeldGameManager gameManager;
    private Scanner scanner = new Scanner(System.in);
    private Random rand = new Random();
    public boolean drawnExplodingKitten;

    public void handleExplodingKitten() throws InterruptedException {
        drawnExplodingKitten = true;
        if (!gameManager.getCurrentPlayerHand().contains(new defuse())) {
            ServerHandler.sendMessageToSingleRoomClient(getCurrentPlayer(),"DIED");
            ServerHandler.roomPlayerList.remove(getCurrentPlayer());        // Fix spectator maybe?
            ServerHandler.sendMessageToRoomClients(null,gameManager.getCurrentPlayer().getName() + " exploded!");
            gameManager.killPlayer(gameManager.getCurrentPlayer());
        }else{
            ServerHandler.sendMessageToSingleRoomClient(getCurrentPlayer(),"EXPLODING");
        }
    }

    public void handleDrawAction() throws InterruptedException {
        if(!drawnExplodingKitten) {
            Card cardDrawn = gameManager.mainDeck.draw();
            gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
            System.out.println("Sending UPDATEHAND");
            ServerHandler.sendMessageToSingleRoomClient(getCurrentPlayer(),"UPDATEHAND " + cardDrawn.getName());

            if (cardDrawn.equals(new exploding_kitten())) handleExplodingKitten();

            gameManager.endTurn();
            if(getCurrentPlayer().split("_")[0].equals("Computer")) handleComputerAction();
            ServerHandler.sendMessageToRoomClients(null,"TURN " + gameManager.getCurrentPlayer().getName());
        }
        // Handle exploding kitten
    }

    public void handlePlayAction(int index) throws InterruptedException {
        if(gameManager.getCurrentPlayer().getHand().getCard(index).equals(new defuse())) drawnExplodingKitten = false;
        gameManager.discardDeck.discardCard(gameManager.getCurrentPlayerHand().playCard(index, gameManager));
        String topCardName = "NOCARD";
        Card topCard = gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();
        ServerHandler.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize()
                + " " + topCardName);
    }

    public void handleAction(String action) throws InterruptedException {
        String[] parsedAction = action.split(" ");
        System.out.println("Drawing card: " + action);
        switch (parsedAction[0]){
            case "draw":
                handleDrawAction();
                break;
            case "play":
                handlePlayAction(Integer.parseInt(parsedAction[1]));
                break;
        }
    }
    public String getCurrentPlayer(){ return gameManager.getCurrentPlayer().getName(); }

    public void handleComputerAction() throws InterruptedException {
        System.out.println("Hnalding comptuer");
        Card cardDrawn = gameManager.mainDeck.draw();
        gameManager.getCurrentPlayerHand().addToHand(cardDrawn);
//        ServerHandler.sendMessageToRoomClients(null,"COMPUTER " + gameManager.getCurrentPlayer().getName() + " drew");
        if(cardDrawn.equals(new exploding_kitten())){
//            ServerHandler.sendMessageToRoomClients(null,"COMPUTER "
//                                                       + gameManager.getCurrentPlayer().getName() + " drewexp");
            if(gameManager.getCurrentPlayerHand().contains(new defuse())){
                System.out.println(gameManager.getCurrentPlayer().getHand().getHand().toString());
                int defuseCardIndex = gameManager.getCurrentPlayerHand().currentHand.indexOf(new defuse());
                handlePlayAction(defuseCardIndex);
                gameManager.mainDeck.insertCard(new exploding_kitten(),rand.nextInt(gameManager.mainDeck.getDeckSize()));
//                ServerHandler.sendMessageToRoomClients(null,"COMPUTER "
//                                                           + gameManager.getCurrentPlayer().getName() + "defused");
            }
            else{
                ServerHandler.sendMessageToRoomClients(null,"COMPUTER " + gameManager.getCurrentPlayer().getName()
                                                           + "exploded");
                gameManager.killPlayer(gameManager.getCurrentPlayer());
            }
        }
//        TimeUnit.SECONDS.sleep(1); //In order for the text to not go too fast
        gameManager.endTurn();
        String topCardName = "NOCARD";
        Card topCard = gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();
        ServerHandler.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + topCardName);
    }
    public void start(int sizeOfGame, int numberOfComputers) throws IOException, InterruptedException {

        gameManager = new ServerHeldGameManager();
        gameManager.addPlayers();
        ServerHandler.sendMessageToRoomClients(null, "UPDATEDECKS " + gameManager.mainDeck.getDeckSize() + " " + "NOCARD");
        ServerHandler.sendMessageToRoomClients(null, "TURN " + gameManager.getCurrentPlayer().getName()); //Initial player
    }

}