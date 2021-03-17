package softwaredesign.server;


import io.netty.channel.*;

import java.util.*;


/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
    public static HashMap<ChannelHandlerContext,Client> clientDetails = new HashMap<>();
    private static HashMap<String,Room> roomList = new HashMap<>();
    public static ArrayList<String> serverPlayerList = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx);
        clientDetails.put(ctx, new Client(ctx));
    }

    /*
     * When a message is received from client, send that message to all channels.
     * FOr the sake of simplicity, currently we will send received chat message to
     * all clients instead of one specific client. This code has scope to improve to
     * send message to specific client as per senders choice.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server received - " + msg);
        channelRespond(ctx,msg);
    }
    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] message = msg.split("\\s+");
        switch(message[0].toUpperCase(Locale.ROOT)){
            case "USERNAME":
                clientDetails.get(ctx).setClientName(message[1]);
                System.out.println(clientDetails.get(ctx).getClientName());
                String str = "";
                for(String key : roomList.keySet()) str = str + key + ",";
                System.out.println(str);
                if(roomList.isEmpty()) ctx.writeAndFlush("CONNECTEDTOSERVER NOROOM");
                else ctx.writeAndFlush("CONNECTEDTOSERVER ROOMAVAILABLE " + str);
                break;
            case "JOIN":
                Room roomObj = roomList.get(message[1]);
                if(roomObj == null) ctx.writeAndFlush("ROOMNOTFOUND");
                if(!roomObj.addPlayer(clientDetails.get(ctx))) ctx.writeAndFlush("ROOMFULL");
                else {
                    clientDetails.get(ctx).setCurrentRoom(roomObj);
                    ctx.writeAndFlush("JOINSUCCESS " + roomObj.playerListAsString("@@"));
                    roomObj.sendMessageToRoomClients(ctx, "JOINED " + getClientName(ctx));
                }
                break;
            case "LEAVE":
                Room playerRoom = getRoom(ctx);
                String playerName = getClientName(ctx);
                playerRoom.removePlayer(playerName);
                playerRoom.sendMessageToRoomClients(ctx,"LEFT " + playerName + " " + playerRoom.playerListAsString("@@"));
                ctx.writeAndFlush("LEAVEREGISTERED");
                break;
            case "CREATE":
                String[] gameDetails = message[1].split(",");
                roomList.put(gameDetails[0], new Room(clientDetails.get(ctx), gameDetails[0], Integer.parseInt(gameDetails[1]), Integer.parseInt(gameDetails[2])));
                ctx.writeAndFlush("ROOMCREATED");
                break;
            default:
                getRoom(ctx).channelRespond(ctx, msg);
                break;
        }
    }

    private String getClientName(ChannelHandlerContext ctx){ return clientDetails.get(ctx).getClientName(); }
    private Room getRoom(ChannelHandlerContext ctx){ return clientDetails.get(ctx).getCurrentRoom(); }

    private static ChannelHandlerContext getClientCTX(String clientName){
        for(HashMap.Entry<ChannelHandlerContext, Client> entry : clientDetails.entrySet())
            if(entry.getValue().getClientName().equals(clientName))
                return entry.getKey();
        return null;
    }

    public boolean addPlayer(String playerName){
        if(serverPlayerList.remove("free")) return serverPlayerList.add(playerName);
        else return false;
    }

    // Is there a case when a player has to be removed but he is not in roomsPlayersList? This is not checked
    public void removePlayer(String playerName){
        if(serverPlayerList.remove(playerName)) serverPlayerList.add("free");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        System.out.println(cause);
        System.out.println("Closing connection for client - " + getClientName(ctx));
        Room playerRoom = getRoom(ctx);
        if(playerRoom != null){
            playerRoom.removePlayer(getClientName(ctx));
            playerRoom.sendMessageToRoomClients(ctx,"LEFT " + getClientName(ctx) + " " + playerRoom.playerListAsString("@@"));
        }
        ctx.close();
        clientDetails.remove(ctx);
    }
}

