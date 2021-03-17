package softwaredesign;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;

import java.util.*;

public class Room {
    public static HashMap<ChannelHandlerContext,Client> clientDetails;
    public static ArrayList<Client> roomPlayerList = new ArrayList<>();
    private static int[] gameRules; // 0: Max players, 1: NumberOfComputers
    private static ServerHeldGame onlineGame = new ServerHeldGame();
    private static Client currentHost;
    private static String roomName;

    public Room(Client host, String name, int maxPlayers, int computerAmount, HashMap<ChannelHandlerContext,Client> clientStuff){
        gameRules = new int[]{maxPlayers, computerAmount};
        currentHost = host;
        roomName = name;
        addPlayer(host);
        host.setCurrentRoom(this);
        clientDetails = clientStuff;
        for(int i = 0; i < computerAmount; ++i) roomPlayerList.add(new Computer(this, i));
    }

    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] message = msg.split("\\s+");
        switch(message[0].toUpperCase(Locale.ROOT)){
            case "START":
                if(getHostName().equals(getClientName(ctx))){
                    if(!hasFreeSpots()) {
                        sendMessageToRoomClients(null,"START");
                        onlineGame.start();
                    }
                    else ctx.writeAndFlush("NOSTART " + (getMaxPlayers() - roomPlayerList.size()));
                }
                else ctx.writeAndFlush("CANTSTART " + getHostName());
                break;
            case "PLACE":
                System.out.println(Arrays.toString(message));
                onlineGame.gameManager.mainDeck.insertCard(new ExplodingKittenCard(),Integer.parseInt(message[1]));
                ServerHandler.sendMessageToRoomClients(null, "UPDATEDECKS " + onlineGame.gameManager.mainDeck.getDeckSize()
                        + " " + onlineGame.gameManager.discardDeck.getTopCard().getName());
                break;
            case "PLAY":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayer())){
                    int index = Integer.parseInt(message[1]);
                    if(index < 0 || onlineGame.gameManager.getCurrentPlayer().getHand().getHandSize() - 1 < index){
                        ctx.writeAndFlush("That card is invalid");
                        break;
                    }
                    if(onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && !onlineGame.drawnExplodingKitten){
                        ctx.writeAndFlush("You can only play a defuse card when you draw an Exploding Kitten!");
                        break;
                    }
                    if(!onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && onlineGame.drawnExplodingKitten){
                        ctx.writeAndFlush("You have to play a defuse card when you draw an Exploding Kitten!");
                        break;
                    }
                    ctx.writeAndFlush("PLAYCONFIRMED");
                    onlineGame.handleAction("play " + message[1]);
                }
                break;
            case "DRAW":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayer())){
                    onlineGame.handleAction("draw");
                }
                else{
                    System.out.println("Not current turn");
                }
                break;
            case "CHAT":
                String buildMsg = "";
                for(int i = 1; i < message.length; ++i) buildMsg = buildMsg + message[i] + " ";
                sendMessageToRoomClients(null, "CHAT " + getClientName(ctx) + ": " + buildMsg);
                break;
            default:
                ctx.writeAndFlush("Unknown command, try again");
                break;
        }
    }

    private int getMaxPlayers(){ return gameRules[0]; }
    private boolean hasFreeSpots(){ return roomPlayerList.size() < getMaxPlayers(); }
    private String getHostName(){ return currentHost.getClientName(); }
    public static String getRoomName() { return roomName; }

    public String playerListAsString(){
        String tempString = "";
        for(Client client : roomPlayerList) tempString = tempString + client.getClientName() + "@@";
        return tempString;
    }

//    private void createPlayerList(String [] arg){
//        int playerSpots = Integer.parseInt(arg[0]) - Integer.parseInt(arg[1]);
//        for(int i = 0; i < Integer.parseInt(arg[0]); i++){
//            if(playerSpots != 0) {
//                roomPlayerList.add(i,"free");
//                playerSpots--;
//            }
//            else roomPlayerList.add(i,"Computer_" + (i-Integer.parseInt(arg[1]) + 1));
//        }
//    }
    private String getClientName(ChannelHandlerContext ctx){ return clientDetails.get(ctx).getClientName(); }

    private static ChannelHandlerContext getClientCTX(String clientName){
        for(HashMap.Entry<ChannelHandlerContext, Client> entry : clientDetails.entrySet())
            if(entry.getValue().getClientName().equals(clientName))
                return entry.getKey();
        return null;
    }

    private Client getClientByName(String name){
        System.out.println(roomPlayerList.toString());
        for(Client cli : roomPlayerList) if(name == cli.getClientName()) return cli;
        return null;
    }

    public boolean addPlayer(Client client){
        if(hasFreeSpots()) {
            Boolean a = roomPlayerList.add(client);
            System.out.println(roomPlayerList.toString());
            return a;
        }
        else return false;
    }

    // Is there a case when a player has to be removed but he is not in roomsPlayersList? This is not checked
    public void removePlayer(String player){
        roomPlayerList.remove(getClientByName(player));
    }

    public static void sendMessageToRoomClients(ChannelHandlerContext ctx, String message){
        for(Client cli : roomPlayerList){
            ChannelHandlerContext outgoingCtx = cli.getCtx();
            if(outgoingCtx == null || outgoingCtx == ctx) continue;
            outgoingCtx.writeAndFlush(message);
        }
    }

    public static void sendMessageToSingleRoomClient(String name, String message){
        if(getClientCTX(name) != null) getClientCTX(name).writeAndFlush(message);
    }
}
