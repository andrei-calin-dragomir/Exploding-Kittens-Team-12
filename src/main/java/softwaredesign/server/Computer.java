package softwaredesign.server;

import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;
import softwaredesign.core.Hand;
import softwaredesign.core.Player;

import java.util.Random;

import static java.util.Collections.frequency;

public class Computer extends Player {  // TODO: Add time between actions if possible
    private Random rand = new Random();

    public Computer(Room roomAI, int computerID) {
        super(null);
        this.setCurrentRoom(roomAI);
        this.setPlayerName("Computer_" + computerID);
    }

    public boolean isComputer(){ return true; }

    public void startAction(ServerHeldGame game) throws InterruptedException {
        while(rand.nextInt(3) == 0)    // 25% chance to play a card at every action
            if(compPlay(game)) return;  // Will return if the turn doesn't have to be ended (skip/attack)
        Thread.sleep(rand.nextInt(2000) + 2000);    // Used to emulate "thinking time"
        compDraw(game);
        Thread.sleep(rand.nextInt(2000) + 500);
        game.gameManager.endTurn();
    }

    // Gets a random card from the computer hand
    private Integer getRandCard(){
        for(Card card : getHand())
            if(!card.equals(new DefuseCard())){
                System.out.println("Card chosen: " + card.toString());
                return getHand().indexOf(card);
            }
        return null;
    }

    // Finds player with most (favor) or least cards (attack)
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
        if(frequency(cHand.getHand(), new DefuseCard()) == cHand.getHandSize() || randCardIndex == null) return false; // Return if hand is empty or contains only DefuseCard which you cant play
        Card randCard = getHand().getCard(randCardIndex);
        if(randCard.getName().equals("FavorCard")) return false;
        switch(randCard.getName()){
            case("AttackCard"):
                playAttack(game, randCardIndex);
                return true;
            case("FavorCard"):
                playFavor(game, randCardIndex);
                return false;
            case("SkipCard"):
                game.handlePlayAction(randCardIndex, "");
                return true;
            default:
                game.handlePlayAction(randCardIndex, "");
                return false;
        }
    }

    // Draws a card for the computer
    private void compDraw(ServerHeldGame game) throws InterruptedException {
        Card cardDrawn = game.drawCard();
        getHand().addToHand(cardDrawn);
        getCurrentRoom().sendMsgToRoom(null, "PLAYER " + getName() + " DREW");
        handleKitten(game, cardDrawn);
    }

    // Handles what the choices of the computer are when it gets to place a kitten
    private void handleKitten(ServerHeldGame game, Card cardDrawn) throws InterruptedException {
        if(cardDrawn.equals(new ExplodingKittenCard()))
            if(!game.handleExplodingKitten()){
                Thread.sleep(rand.nextInt(2000) + 2000);
                game.handlePlayAction(getHand().indexOf(new DefuseCard()), "");
                Thread.sleep(rand.nextInt(1000) + 1000);
                game.placeExploding(rand.nextInt(Math.min(4, game.getDeckSize())));   // Places the exploding kitting somewhere near the top
            }
    }
}

