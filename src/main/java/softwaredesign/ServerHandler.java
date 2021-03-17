package softwaredesign;


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
                    ctx.writeAndFlush("JOINSUCCESS " + roomObj.playerListAsString());
                    roomObj.sendMessageToRoomClients(ctx, "JOINED " + getClientName(ctx));
                }
                break;
            case "LEAVE":
                String clientName = getClientName(ctx);
                roomList.get(clientName).removePlayer(clientName);
                ctx.writeAndFlush("LEAVEREGISTERED");
                break;
            case "CREATE":
                String[] gameDetails = message[1].split(",");
                roomList.put(gameDetails[0], new Room(clientDetails.get(ctx), gameDetails[0], Integer.parseInt(gameDetails[1]), Integer.parseInt(gameDetails[2]), clientDetails));
                ctx.writeAndFlush("ROOMCREATED");
                break;
            default:
                getRoom(ctx).channelRespond(ctx, msg);
                break;
        }
    }

    private String playerListAsString(){
        ArrayList<String> tempList = serverPlayerList;
        tempList.removeAll(Collections.singleton("free"));
        return String.join("@@", tempList);
    }

    private void createPlayerList(String [] arg){
        int playerSpots = Integer.parseInt(arg[0]) - Integer.parseInt(arg[1]);
        for(int i = 0; i < Integer.parseInt(arg[0]); i++){
            if(playerSpots != 0) {
                serverPlayerList.add(i,"free");
                playerSpots--;
            }
            else serverPlayerList.add(i,"Computer_" + (i-Integer.parseInt(arg[1]) + 1));
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

    private boolean searchInPlayerList(String playerName){
        System.out.println(serverPlayerList + "");
        for(int i = 0; i < serverPlayerList.size(); i++){
            System.out.println(serverPlayerList.get(i));
            if(serverPlayerList.get(i).equals(playerName)) return true;
        }
        return false;
    }

    public static void sendMessageToRoomClients(ChannelHandlerContext ctx, String message){
        for(int i = 0; i < serverPlayerList.size(); i++){
            ChannelHandlerContext outgoingCtx = getClientCTX(serverPlayerList.get(i));
            if(outgoingCtx == null || outgoingCtx == ctx) continue;
            outgoingCtx.writeAndFlush(message);
        }
    }

    public static void sendMessageToSingleRoomClient(String name, String message){
        if(getClientCTX(name) != null) getClientCTX(name).writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        System.out.println(cause);
        System.out.println("Closing connection for client - " + getClientName(ctx));
        ctx.close();
        if(serverPlayerList.contains(getClientName(ctx))){
            getRoom(ctx).sendMessageToRoomClients(ctx,"LEFT " + getClientName(ctx) + " " + serverPlayerList);
            getRoom(ctx).removePlayer(getClientName(ctx));
        }
        clientDetails.remove(ctx);
    }
}

