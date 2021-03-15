package softwaredesign;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
//    String: Username Channel: connection
    private static Hashtable<String,ChannelHandlerContext> clientDetails = new Hashtable<>();
    private static Hashtable<ChannelHandlerContext,String> reversedClientDetails = new Hashtable<>();

    private static ArrayList<String> roomPlayerList = new ArrayList<>();
    private static String[] gameDetails = {"0","0"}; // 0: RoomSize, 1: NumberOfComputers
    private static Game onlineGame = new Game();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx);
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
        switch(message[0]){
            case "USERNAME":
                clientDetails.put(message[1],ctx);
                reversedClientDetails.put(ctx,message[1]);
                ctx.write("CONNECTEDTOSERVER ");
                if(Arrays.equals(gameDetails, new String[]{"0", "0"})) ctx.writeAndFlush("NOROOM");
                else ctx.writeAndFlush("ROOMAVAILABLE");
                break;
            case "JOIN":
                if(!searchInPlayerList("free")) ctx.writeAndFlush("ROOMFULL");
                else{
                    ctx.write("JOINSUCCESS ");
                    updatePlayerList(reversedClientDetails.get(ctx));
                    sendMessageToRoomClients(ctx,"JOINED " + reversedClientDetails.get(ctx));
                    if(!roomPlayerList.contains("free")) {
                        ctx.writeAndFlush("START");
                        sendMessageToRoomClients(ctx, "START");
                        onlineGame.start(Integer.parseInt(gameDetails[0]), Integer.parseInt(gameDetails[1]));
                    }
                    for(int i =0; i < roomPlayerList.size() - 1; i++) ctx.write(roomPlayerList.get(i) + ",");
                    ctx.writeAndFlush(roomPlayerList.get(roomPlayerList.size() - 1));
                }
                break;
            case "LEAVE":
                updatePlayerList(reversedClientDetails.get(ctx));
                sendMessageToRoomClients(ctx,"LEFT " + reversedClientDetails.get(ctx) + " " + roomPlayerList);
                break;
            case "CREATE":
                gameDetails = message[1].split(",");
                createPlayerList(gameDetails);
                updatePlayerList(reversedClientDetails.get(ctx));
                ctx.writeAndFlush("ROOMCREATED");
                break;
            case "PLAY":
                break;
        }
    }
    private void createPlayerList(String [] arg){
        int playerSpots = Integer.parseInt(arg[0]) - Integer.parseInt(arg[1]);
        for(int i = 0; i < Integer.parseInt(arg[0]); i++){
            if(playerSpots != 0) {
                roomPlayerList.add(i,"free");
                playerSpots--;
            }
            else roomPlayerList.add(i,"Computer" + (i-Integer.parseInt(arg[1])));
        }
    }
    private void updatePlayerList(String playerName){
        for(int i = 0; i < roomPlayerList.size(); i++){
            if(roomPlayerList.get(i).equals(playerName)){
                roomPlayerList.set(i,"free");
                return;
            }
        }
        for(int i = 0; i < roomPlayerList.size(); i++){
            if(roomPlayerList.get(i).equals("free")){
                roomPlayerList.set(i,playerName);
                return;
            }
        }
    }
    private boolean searchInPlayerList(String playerName){
        System.out.println(roomPlayerList + "");
        for(int i = 0; i < roomPlayerList.size(); i++){
            System.out.println(roomPlayerList.get(i));
            if(roomPlayerList.get(i).equals(playerName)) return true;
        }
        return false;
    }
    private void sendMessageToRoomClients(ChannelHandlerContext ctx,String message){
        for(int i = 0; i < roomPlayerList.size(); i++){
            ChannelHandlerContext outgoingCtx = clientDetails.get(roomPlayerList.get(i));
            if(outgoingCtx == ctx) continue;
            if(outgoingCtx == null) continue;
            outgoingCtx.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause);
        System.out.println("Closing connection for client - " + ctx);
        ctx.close();
        if(roomPlayerList.contains(reversedClientDetails.get(ctx))){
            updatePlayerList(reversedClientDetails.get(ctx));
            sendMessageToRoomClients(ctx,"LEFT " + reversedClientDetails.get(ctx) + " " + roomPlayerList);
        }
        clientDetails.remove(reversedClientDetails.get(ctx));
        reversedClientDetails.remove(ctx);
    }
}
