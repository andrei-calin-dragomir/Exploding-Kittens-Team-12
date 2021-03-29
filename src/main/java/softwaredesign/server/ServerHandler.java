package softwaredesign.server;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import softwaredesign.core.Deck;
import softwaredesign.core.Player;
import softwaredesign.core.State;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class ServerHandler extends SimpleChannelInboundHandler<String>{
    public static HashMap<ChannelHandlerContext, Player> playerMap = new HashMap<>();
    private static HashMap<String,Room> roomList = new HashMap<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {        // Standard netty function that gets called when a new connection is made
        System.out.println("Client joined - " + ctx);
        playerMap.put(ctx, new Player(ctx));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {  // Standard netty function that gets called when a new message has been received
        System.out.println("Server received - " + msg);
        channelRespond(ctx,msg);
    }

    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception { // This handles the received message by checking what it is and handling the action.
        String[] message = msg.split("\\s+");
        switch(message[0].toUpperCase(Locale.ROOT)){
            case "DISCONNECTING":               // When a player disconnect in any way this message is sent from that player
                disconnectPlayer(ctx, null);
                break;
            case "AVAILABLEROOMS":              // Returns the rooms that are available, this is used by the client to refresh the room screen
                if(message.length < 3){
                    String str = String.join(",", roomList.keySet());
                    if(roomList.isEmpty()) ctx.writeAndFlush("ROOM NOROOM");
                    else ctx.writeAndFlush("ROOM AVAILABLE " + str);
                }
                break;
            case "USERNAME":                    // Register the username of the player, checks if its already in use as well
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
            case "JOIN":                        // Lets a player join a room, will return appropriate errors (room full, room not found etc)
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
            case "CREATE":                      // Lets a player create a room, it takes the parameters: <Roomname>,<PlayerTotal>,<ComputerTotal>,(Optional)<CustomDeck>
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
            default:    // If none of these commands match then the command is passed onto the room and that will handle the message further
                getRoom(ctx).channelRespond(ctx, msg);
                break;
        }
    }

    // Used to construct the custom deck that was sent over a message. This is not needed if the deck is "default"
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

    // Returns true if the player name already exists
    private Boolean checkNameInUse(String name){
        for(Player player : playerMap.values()) if(player.getName().equals(name)) return true;
        return false;
    }

    // Removes the player from a room, and will also remove the room if no human players are left.
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

    // This closes the actual connection for the player, which means they left the server completely (and not only a room)
    private void disconnectPlayer(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Closing connection for client - " + getClientName(ctx));
        if(cause != null) System.out.println("He disconnected because of: " + cause);
        cleanRoomOfEntity(ctx);
        ctx.close();
        playerMap.remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        disconnectPlayer(ctx,cause);
    }
}

