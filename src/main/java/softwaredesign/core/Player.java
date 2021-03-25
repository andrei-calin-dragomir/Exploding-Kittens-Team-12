package softwaredesign.core;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.server.Room;

public class Player {
    //Server related
    private ChannelHandlerContext ctx;
    private String name = "unnamed";
    private Room currentRoom = null;

    // Game related
    private Hand hand;

    public Player(ChannelHandlerContext clientCTX){ this.ctx = clientCTX; }

    public void setPlayerName(String passedName){ this.name = passedName; }
    public void setCurrentRoom(Room newRoom){ this.currentRoom = newRoom; }
    public String getPlayerName(){ return this.name; }
    public Room getCurrentRoom(){ return this.currentRoom; }
    public ChannelHandlerContext getCtx() { return this.ctx; }


    public boolean isComputer(){ return false; }
    public String getName(){ return this.name; }
    public Hand getHand(){ return hand; }
    public void setName(String name){ this.name = name; }
    public void initHand(Deck deck){ hand = new Hand(deck); }
}
