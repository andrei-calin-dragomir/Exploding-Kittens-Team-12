package softwaredesign.core;

public class Player {
    private String name;
    private Hand hand;
    private boolean isComputer = false;

    public void markAsComputer(){ isComputer = true; }
    public boolean isComputer(){ return isComputer; }
    public String getName(){ return this.name; }
    public Hand getHand(){ return hand; }
    public void setName(String name){ this.name = name; }
    public void initHand(Deck deck){ hand = new Hand(deck); }
}
