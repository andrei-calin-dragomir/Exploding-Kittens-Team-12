package softwaredesign;


import io.netty.channel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
//    String: Username Channel: connection
    public static Hashtable<String,ChannelHandlerContext> clientDetails = new Hashtable<>();
    private static Hashtable<ChannelHandlerContext,String> reversedClientDetails = new Hashtable<>();

    public static ArrayList<String> roomPlayerList = new ArrayList<>();
    private static String[] gameDetails = {"0","0"}; // 0: RoomSize, 1: NumberOfComputers
    private static ServerHeldGame onlineGame = new ServerHeldGame();
    private static boolean canSend = true;

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
            case "START":
                if(!roomPlayerList.contains("free")) {
                    sendMessageToRoomClients(null,"START");
                    onlineGame.start(Integer.parseInt(gameDetails[0]), Integer.parseInt(gameDetails[1]));
                }else{
                    sendMessageToSingleRoomClient(null,"NOSTART");
                }
                break;
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
                    for(int i =0; i < roomPlayerList.size() - 1; i++) ctx.write(roomPlayerList.get(i) + ",");
                    ctx.writeAndFlush(roomPlayerList.get(roomPlayerList.size() - 1));
                    sendMessageToRoomClients(ctx,"JOINED " + reversedClientDetails.get(ctx));
                }
                break;
            case "LEAVE":
                updatePlayerList(reversedClientDetails.get(ctx));
                sendMessageToRoomClients(ctx,"LEFT " + reversedClientDetails.get(ctx) + " " + roomPlayerList);
                break;
            case "PLACE":
                System.out.println(Arrays.toString(message));
                onlineGame.gameManager.mainDeck.insertCard(new exploding_kitten(),Integer.parseInt(message[1]));
                ServerHandler.sendMessageToRoomClients(null, "UPDATEDECKS " + onlineGame.gameManager.mainDeck.getDeckSize()
                        + " " + onlineGame.gameManager.discardDeck.getTopCard().getName());
                break;
            case "CREATE":
                gameDetails = message[1].split(",");
                createPlayerList(gameDetails);
                updatePlayerList(reversedClientDetails.get(ctx));
                ctx.writeAndFlush("ROOMCREATED");
                break;
            case "PLAY":
                if(reversedClientDetails.get(ctx).equals(onlineGame.getCurrentPlayer())){
                    if(onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new defuse()) && !onlineGame.drawnExplodingKitten) break;
                    if(!onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new defuse()) && onlineGame.drawnExplodingKitten) break;
                    ctx.writeAndFlush("PLAYCONFIRMED");
                    onlineGame.handleAction("play " + message[1]);
                }
                break;
            case "DRAW":
                if(reversedClientDetails.get(ctx).equals(onlineGame.getCurrentPlayer())){
                    onlineGame.handleAction("draw");
                }
                else{
                    System.out.println("Not current turn");
                }
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
            else roomPlayerList.add(i,"Computer_" + (i-Integer.parseInt(arg[1]) + 1));
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
    public static void sendMessageToRoomClients(ChannelHandlerContext ctx, String message) throws InterruptedException {
        for(int i = 0; i < roomPlayerList.size(); i++){
            ChannelHandlerContext outgoingCtx = clientDetails.get(roomPlayerList.get(i));
            System.out.println("Sending message: " + message);
            if(outgoingCtx == null || outgoingCtx == ctx) continue;
            while(!canSend) {
                System.out.println("Waiting to send");
                TimeUnit.MILLISECONDS.sleep(100);
            }
            canSend = false;
            ChannelFuture sentMsg = outgoingCtx.writeAndFlush(message + "\r").sync();
            System.out.println("Sent message: " + message);
            System.out.println("Has sent message: " + sentMsg.isDone());
            while(!sentMsg.isDone()) {
                System.out.println("Waiting for response");
                TimeUnit.MILLISECONDS.sleep(100);
            }
            canSend = true;
        }
    }
    public static void sendMessageToSingleRoomClient(String name, String message) throws InterruptedException {
        if(clientDetails.get(name) == null) return;
        System.out.println("Sending message from singleroom");
        clientDetails.get(name).writeAndFlush(message + "\r").sync();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        System.out.println(cause);
        System.out.println("Closing connection for client - " + ctx);
        ctx.close();
        if(roomPlayerList.contains(reversedClientDetails.get(ctx))){
            sendMessageToRoomClients(ctx,"LEFT " + reversedClientDetails.get(ctx) + " " + roomPlayerList);
            updatePlayerList(reversedClientDetails.get(ctx));
        }
        clientDetails.remove(reversedClientDetails.get(ctx));
        reversedClientDetails.remove(ctx);
    }
}

