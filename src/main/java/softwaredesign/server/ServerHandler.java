package softwaredesign.server;


import io.netty.channel.*;
import softwaredesign.core.Deck;
import softwaredesign.core.Player;
import softwaredesign.core.State;

import java.io.File;
import java.util.*;

public class ServerHandler extends SimpleChannelInboundHandler<String>{
    public static HashMap<ChannelHandlerContext, Player> playerMap = new HashMap<>();
    private static HashMap<String,Room> roomList = new HashMap<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx);
        playerMap.put(ctx, new Player(ctx));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server received - " + msg);
        channelRespond(ctx,msg);
    }

    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] message = msg.split("\\s+");
        switch(message[0].toUpperCase(Locale.ROOT)){
            case "DISCONNECTING":
                disconnectPlayer(ctx, null);
                break;
            case "AVAILABLEROOMS" :
                if(message.length < 3){
                    String str = String.join(",", roomList.keySet());
                    if(roomList.isEmpty()) ctx.writeAndFlush("ROOM NOROOM");
                    else ctx.writeAndFlush("ROOM AVAILABLE " + str);
                }
                break;
            case "USERNAME":
                if(checkNameInUse(message[1])) ctx.writeAndFlush("USERNAMETAKEN");
                else{
                    ctx.writeAndFlush("USERNAMEACCEPTED " + message[1]);
                    playerMap.get(ctx).setPlayerName(message[1]);
                    if(message.length < 3){
                        String str = String.join(",", roomList.keySet());
                        if(roomList.isEmpty()) ctx.writeAndFlush("ROOM NOROOM");
                        else ctx.writeAndFlush("ROOM AVAILABLE " + str);
                    }
                }
                break;
            case "JOIN":
                Room roomObj = roomList.get(message[1]);
                if(roomObj == null) ctx.writeAndFlush("ROOM NOTFOUND");
                else if(roomObj.hasStarted()) ctx.writeAndFlush("ROOM STARTED");
                else if(!roomObj.addPlayer(playerMap.get(ctx))) ctx.writeAndFlush("ROOM FULL");
                else {
                    Player p = playerMap.get(ctx);
                    p.setCurrentRoom(roomObj);
                    p.setPlayerState(State.INROOM);
                    ctx.writeAndFlush("JOINSUCCESS " + roomObj.playerListAsString() + " " + roomObj.getGameRules()[0] + "," + roomObj.getGameRules()[1]);
                    roomObj.sendMsgToRoom(playerMap.get(ctx), "JOINED " + getClientName(ctx));
                }
                break;
            case "LEAVEROOM":
                cleanRoomOfEntity(ctx);
                break;
            case "CREATE":
                String[] gameDetails = message[1].split(",");
                if (message.length > 2 && message[2].equals("SOLO")) ctx.writeAndFlush("ROOM CREATED SOLO");
                else{
                    if(roomList.keySet().contains(gameDetails[0])) ctx.writeAndFlush("ROOM TAKEN");
                    else{
                        String deckName = "default";
                        if(gameDetails.length > 3) deckName = constructDeckFromMessage(gameDetails[3], gameDetails[0]);
                        roomList.put(gameDetails[0], new Room(playerMap.get(ctx), gameDetails[0], Integer.parseInt(gameDetails[1]), Integer.parseInt(gameDetails[2]), deckName));
                        ctx.writeAndFlush("ROOM CREATED");
                    }
                }
                break;
            default:
                getRoom(ctx).channelRespond(ctx, msg);
                break;
        }
    }

    private String constructDeckFromMessage(String customDeck, String roomName) throws Exception{
        String[] cardAmounts = customDeck.split("==");
        HashMap<String, Integer> cardAmountMap = new HashMap<>();
        for(String card : cardAmounts){
            String[] singleCard = card.split(":");
            cardAmountMap.put(singleCard[0], Integer.parseInt(singleCard[1]));
        }
        String customDeckName = "custom-" + roomName;
        Deck.createCustom(cardAmountMap, customDeckName, "server");
        return customDeckName;
    }

    private String getClientName(ChannelHandlerContext ctx){ return playerMap.get(ctx).getName(); }
    private Room getRoom(ChannelHandlerContext ctx){ return playerMap.get(ctx).getCurrentRoom(); }

    private Boolean checkNameInUse(String name){
        for(Player player : playerMap.values()) if(player.getName().equals(name)) return true;
        return false;
    }
    private void cleanRoomOfEntity(ChannelHandlerContext ctx) {
        Room playerRoom = getRoom(ctx);
        if(playerRoom != null){
            Player player = playerMap.get(ctx);
            playerRoom.removePlayer(player.getName());
            playerRoom.sendMsgToRoom(player,"LEFT " + player.getName() + " " + playerRoom.playerListAsString());
            if(playerRoom.isRoomEmpty()) {
                File customDeck = new File("resources/decks/server/" + playerRoom.getDeckName() + ".json");
                customDeck.delete();
                roomList.remove(playerRoom.getRoomName());
            }
            else playerRoom.assignNewHost();
        }
    }
    private void disconnectPlayer(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Closing connection for client - " + getClientName(ctx));
        if(cause != null) System.out.println("He disconnected because of: " + cause);
        cleanRoomOfEntity(ctx);
        ctx.close();
        playerMap.remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        disconnectPlayer(ctx,cause);
    }
}

