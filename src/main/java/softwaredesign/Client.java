package softwaredesign;

import io.netty.channel.ChannelHandlerContext;

public class Client {
    private ChannelHandlerContext ctx;
    private String clientName = "unnamed";
    private Room currentRoom = null;

    public Client(ChannelHandlerContext clientCTX){ ctx = clientCTX; }

    public void setClientName(String passedName){ clientName = passedName; }
    public void setCurrentRoom(Room newRoom){ currentRoom = newRoom; }
    public String getClientName(){ return clientName; }
    public Room getCurrentRoom(){ return currentRoom; }
    public ChannelHandlerContext getCtx() { return ctx; }
}
