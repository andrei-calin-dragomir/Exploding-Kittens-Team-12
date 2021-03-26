package softwaredesign.core;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.server.Room;

public class Player {
    private State playerState = State.OUTOFROOM;
    private ChannelHandlerContext ctx;
    private String name = "unnamed";
    private Room currentRoom = null;
    private Hand hand;

    public Player(ChannelHandlerContext clientCTX){ this.ctx = clientCTX; }

    public State getPlayerState() { return playerState; }
    public void setPlayerName(String passedName){ this.name = passedName; }
    public void setCurrentRoom(Room newRoom){ this.currentRoom = newRoom; }
    public void setName(String name){ this.name = name; }
    public void initHand(Deck deck){ hand = new Hand(deck); }

    public void setPlayerState(State playerState) { this.playerState = playerState; }
    public Room getCurrentRoom(){ return this.currentRoom; }
    public ChannelHandlerContext getCtx() { return this.ctx; }
    public String getName(){ return this.name; }
    public Hand getHand(){ return hand; }
    public boolean isComputer(){ return false; }
}
