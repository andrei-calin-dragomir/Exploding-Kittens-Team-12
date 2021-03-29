package softwaredesign.server;

import static java.util.Collections.frequency;
import static java.util.Collections.min;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Hand;
import softwaredesign.core.Player;
import java.util.Random;

public class Computer extends Player {  // TODO: Add time between actions if possible
    private Random rand = new Random();

    public Computer(Room roomAI, int computerID) {
        super(null);
        this.setCurrentRoom(roomAI);
        this.setPlayerName("Computer_" + computerID);
    }

    public boolean isComputer(){ return true; }

    public String getComputerID(){ return this.getName().split("_")[1]; }    // Maybe needed idk

    public void startAction(ServerHeldGame game) throws InterruptedException {
        while(rand.nextInt(3) == 0)    // 25% chance to play a card at every action
            if(compPlay(game)) return;
        Thread.sleep(rand.nextInt(2000) + 2000);
        compDraw(game);
        Thread.sleep(rand.nextInt(2000) + 500);
        game.gameManager.endTurn();
    }

    private Integer getRandCard(){
        for(Card card : getHand())
            if(!card.equals(new DefuseCard())){
                System.out.println("Card chosen: " + card.toString());
                return getHand().indexOf(card);
            }
        return null;
    }

    // Finds player with most or least cards // TODO: Test this, probs bugged
    private String getHandSizes(ServerHeldGame game, Boolean biggest){
        Player chosenPlayer = null;
        Integer handSize = 0;
        if(!biggest) handSize = 99999;                           // Arbitrary big number
        for(Player player : game.gameManager.getAlivePlayers()){
            if(player == this) continue;                        // Dont count yourself
            if(biggest) {
                if (player.getHand().getHandSize() > handSize) {
                    handSize = player.getHand().getHandSize();
                    chosenPlayer = player;
                }
            }
            else {
                if (player.getHand().getHandSize() < handSize) {
                    handSize = player.getHand().getHandSize();
                    chosenPlayer = player;
                }
            }
        }
        return chosenPlayer.getName();
    }

    // Attacks player with least cards
    private void playAttack(ServerHeldGame game, Integer cardIndex) throws InterruptedException {
        String playerToAttack = getHandSizes(game, false);
        game.handlePlayAction(cardIndex, playerToAttack);
    }

    // Attacks player with most cards
    private void playFavor(ServerHeldGame game, Integer cardIndex) throws InterruptedException {
        String playerToAttack = getHandSizes(game, true);
        game.handlePlayAction(cardIndex, playerToAttack);
    }


    // Returns true if a turn is skipped using the skip card or the attack card
    private Boolean compPlay(ServerHeldGame game) throws InterruptedException {
        Thread.sleep(rand.nextInt(2000) + 2000);
        Hand cHand = getHand();
        Integer randCardIndex = getRandCard();
        System.out.println(randCardIndex);
        if(frequency(cHand.getHand(), new DefuseCard()) == cHand.getHandSize() || randCardIndex == null) return false; // Return if hand is empty or contains only DefuseCard which you cant play
        Card randCard = getHand().getCard(randCardIndex);
        if(randCard.getName().equals("FavorCard")) return false;
        System.out.println("Comparing: " + randCard.getName());
        switch(randCard.getName()){
            case("AttackCard"):
                playAttack(game, randCardIndex);
                return true;
            case("FavorCard"):
                playFavor(game, randCardIndex);
                return false;
            case("SkipCard"):
                System.out.println("Skipping");
                game.handlePlayAction(randCardIndex, "");
                return true;
            default:
                game.handlePlayAction(randCardIndex, "");
                return false;
        }
    }

    private void compDraw(ServerHeldGame game) throws InterruptedException {
        Card cardDrawn = game.drawCard();
        System.out.println("Card drawn by " + getName() + ": " + cardDrawn.getName());
        getHand().addToHand(cardDrawn);
        getCurrentRoom().sendMsgToRoom(null, "PLAYER " + getName() + " DREW");
        handleKitten(game, cardDrawn);
    }

    private void handleKitten(ServerHeldGame game, Card cardDrawn) throws InterruptedException {
        if(cardDrawn.equals(new ExplodingKittenCard()))
            if(!game.handleExplodingKitten()){
                System.out.println("Placing kitten by " + getName());
                Thread.sleep(rand.nextInt(2000) + 2000);
                game.handlePlayAction(getHand().indexOf(new DefuseCard()), "");
                Thread.sleep(rand.nextInt(1000) + 1000);
                game.placeExploding(rand.nextInt(Math.min(4, game.getDeckSize())));   // Places the exploding kitting somewhere near the top
            }
    }
}

