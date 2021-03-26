package softwaredesign.server;


import io.netty.channel.*;
import softwaredesign.core.Player;

import java.util.*;


/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
    public static HashMap<ChannelHandlerContext, Player> playerMap = new HashMap<>();
    private static HashMap<String,Room> roomList = new HashMap<>();
    public static ArrayList<String> serverPlayerList = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx);
        playerMap.put(ctx, new Player(ctx));
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
                playerMap.get(ctx).setPlayerName(message[1]);
                String str = String.join(",", roomList.keySet());
                if(message[2].equals("SOLO")) ctx.writeAndFlush("CONNECTEDTOSERVER SOLO");
                else if(roomList.isEmpty()) ctx.writeAndFlush("CONNECTEDTOSERVER NOROOM");
                else ctx.writeAndFlush("CONNECTEDTOSERVER ROOMAVAILABLE " + str);
                break;
            case "JOIN":
                Room roomObj = roomList.get(message[1]);
                if(roomObj == null) ctx.writeAndFlush("ROOMNOTFOUND");
                if(!roomObj.addPlayer(playerMap.get(ctx))) ctx.writeAndFlush("ROOMFULL");
                else {
                    playerMap.get(ctx).setCurrentRoom(roomObj);
                    ctx.writeAndFlush("JOINSUCCESS " + roomObj.playerListAsString());
                    roomObj.sendMsgToRoom(playerMap.get(ctx), "JOINED " + getClientName(ctx));
                }
                break;
            case "LEAVE":
                cleanRoomOfEntity(ctx);
                ctx.writeAndFlush("LEAVEREGISTERED");
                break;
            case "CREATE":
                String[] gameDetails = message[1].split(",");
                roomList.put(gameDetails[0], new Room(playerMap.get(ctx), gameDetails[0], Integer.parseInt(gameDetails[1]), Integer.parseInt(gameDetails[2])));
                System.out.println("ASD" + playerMap.get(ctx).getName());
                try {
                    if (message[2].equals("SOLO"))
                        ctx.writeAndFlush("ROOMCREATED SOLO");
                }catch(Exception notSolo){ ctx.writeAndFlush("ROOMCREATED"); }
                break;
            default:
                getRoom(ctx).channelRespond(ctx, msg);
                break;
        }
    }

    private String getClientName(ChannelHandlerContext ctx){ return playerMap.get(ctx).getName(); }
    private Room getRoom(ChannelHandlerContext ctx){ return playerMap.get(ctx).getCurrentRoom(); }

    private void cleanRoomOfEntity(ChannelHandlerContext ctx) {
        Room playerRoom = getRoom(ctx);
        Player player = playerMap.get(ctx);
        playerRoom.removePlayer(player.getName());
        playerRoom.sendMsgToRoom(player,"LEFT " + player.getName() + " " + playerRoom.playerListAsString());
        if(playerRoom.isRoomEmpty()) roomList.remove(playerRoom.getRoomName());
    }

//    private static ChannelHandlerContext getClientCTX(String clientName){
//        for(HashMap.Entry<ChannelHandlerContext, Client> entry : clientDetails.entrySet())
//            if(entry.getValue().getClientName().equals(clientName))
//                return entry.getKey();
//        return null;
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        System.out.println(cause);
        System.out.println("Closing connection for client - " + getClientName(ctx));
        cleanRoomOfEntity(ctx); //TODO this one must also clear the turns because if a player leaves, his turn still comes in the game // DONE
        ctx.close();
        playerMap.remove(ctx);
    }
}

