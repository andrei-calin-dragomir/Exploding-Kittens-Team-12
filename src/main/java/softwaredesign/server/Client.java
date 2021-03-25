//package softwaredesign.server;
//
//import io.netty.channel.ChannelHandlerContext;
//
//public class Client {
//    private ChannelHandlerContext ctx;
//    private String clientName = "unnamed";
//    private Room currentRoom = null;
//
//    public Client(ChannelHandlerContext clientCTX){ this.ctx = clientCTX; }
//
//    public void setClientName(String passedName){ this.clientName = passedName; }
//    public void setCurrentRoom(Room newRoom){ this.currentRoom = newRoom; }
//    public String getClientName(){ return this.clientName; }
//    public Room getCurrentRoom(){ return this.currentRoom; }
//    public ChannelHandlerContext getCtx() { return this.ctx; }
//}
