package softwaredesign.server;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.cards.Card;
import softwaredesign.cards.DefuseCard;
import softwaredesign.core.Player;
import softwaredesign.core.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

public class Room {
    private LinkedHashMap<String, Player> roomPlayerList = new LinkedHashMap<>();
    private final int[] gameRules; // 0: Max players, 1: NumberOfComputers
    private final ServerHeldGame onlineGame = new ServerHeldGame(this);
    private Player currentHost;
    private String deckName;
    private final String roomName;

    public Room(Player host, String name, int maxPlayers, int computerAmount, String customDeckName){
        this.deckName = customDeckName;
        gameRules = new int[]{maxPlayers, computerAmount};
        currentHost = host;
        roomName = name;
        addPlayer(host);
        host.setCurrentRoom(this);

        for(int i = 0; i < computerAmount; ++i) {
            Computer newComputer = new Computer(this, i);
            roomPlayerList.put(newComputer.getName(), newComputer);
        }
    }

    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] message = msg.split("\\s+");
        if(onlineGame.gameManager != null && !isAlive(ctx) && !message[0].toUpperCase(Locale.ROOT).equals("CHAT")){
            if(onlineGame.gameManager.getAlivePlayers().size() != 0) ctx.writeAndFlush("NOTALLOWED DEAD");
            else ctx.writeAndFlush("ENDED");
            return;
        }
        switch(message[0].toUpperCase(Locale.ROOT)){
            case "START":               // Starts the game if the lobby has enough players and the player is the actual host
                if(getHostName().equals(getClientName(ctx))){
                    if(!hasFreeSpots()) {
                        for(Player p : roomPlayerList.values()) p.setPlayerState(State.PLAYING);
                        sendMsgToRoom(null, "START");
                        onlineGame.start(deckName);
                    }
                    else ctx.writeAndFlush("NOSTART " + (getMaxPlayers() - roomPlayerList.size()));
                }
                else ctx.writeAndFlush("CANTSTART " + getHostName());
                break;
            case "PLACE":               // Determines where the kitten is placed if a player was exploding
                if(onlineGame.isExploding()) {
                    onlineGame.placeExploding(Integer.parseInt(message[1]));
                    if(onlineGame.getCurrentPlayer().isComputer()) onlineGame.gameManager.endTurn();
                    else onlineGame.nextTurn();
                }
                break;
            case "GIVE":                // Gives a card from a player to another player, used for the FavorCard
                onlineGame.giveCard(Integer.parseInt(message[1]),message[2],getClientName(ctx));
                sendGameStateUpdates("UPDATEPLAYERHANDS");
                break;
            case "PLAY":                // This handles the playing of the card, the error checks are client side so it doesn't have to be checked here
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())){
                    String target = "";
                    int index = Integer.parseInt(message[1]);
                    if(message.length > 2) target = message[2];
                    ctx.writeAndFlush("PLAYCONFIRMED");
                    onlineGame.handlePlayAction(index, target);
                }
                break;
            case "DRAW":                // Handles the drawing of a card
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())) onlineGame.handleDrawAction();
                else ctx.writeAndFlush("NOTALLOWED NOTYOURTURN");
                break;
            case "CHAT":                // Can only be used while in the room lobby, not while in game. Allows you to chat with other players
                String buildMsg = "";
                for(int i = 1; i < message.length; ++i) buildMsg = buildMsg + message[i] + " ";
                sendMsgToRoom(null, "CHAT " + getClientName(ctx) + ": " + buildMsg);
                break;
            default:
                ctx.writeAndFlush("Unknown command, try again");
                break;
        }
    }

    // Bunch of getters and booleans
    private Boolean isAlive(ChannelHandlerContext ctx){ return onlineGame.gameManager.isAlive(getClientName(ctx)); }
    private Boolean hasFreeSpots(){ return roomPlayerList.size() < getMaxPlayers(); }
    public Boolean hasStarted(){ return onlineGame.gameManager != null; }
    public Integer getMaxPlayers(){ return gameRules[0]; }
    public String getRoomName(){ return this.roomName; }
    public String getDeckName() { return deckName; }
    public Player getPlayer(String playerName) { return roomPlayerList.get(playerName); }
    public String getHostName(){ return currentHost.getName(); }
    public HashMap<String, Player> getRoomPlayerList() { return roomPlayerList; }
    public void assignNewHost(){ currentHost = roomPlayerList.values().iterator().next(); }
    public int[] getGameRules() {
        return gameRules;
    }

    public String playerListAsString(){                     // Returns the player list as a string with a delimiter so the client side can decode it
        ArrayList<String> allPlayers = new ArrayList<>();
        for(Player player : roomPlayerList.values()) allPlayers.add(player.getName());
        return String.join("@@", allPlayers); // @@ is the delimiter which the client side will use
    }

    public void sendGameStateUpdates(String typeOfUpdate){  // Sends game state updates for the deck update
        String topCardName = "NOCARD";
        String playerHandSizes = "";
        Card topCard = onlineGame.gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();

        for(Player player: onlineGame.gameManager.getAlivePlayers())
            playerHandSizes += player.getName() + " " + player.getHand().getHandSize() + " ";

        sendMsgToRoom(null, "UPDATEDECKS " + onlineGame.getDeckSize() + " " + topCardName);
        sendMsgToRoom(null,typeOfUpdate + " " + playerHandSizes);
    }

    // Gets the client name by ctx, used to reduce calls (Law of Demeter)
    public String getClientName(ChannelHandlerContext ctx){
        for(Player player : roomPlayerList.values())
            if(player.getCtx() != null && player.getCtx().equals(ctx))
                return player.getName();
        return null;
    }

    // Add a player to the room
    public boolean addPlayer(Player player){
        if(hasFreeSpots()) {
            roomPlayerList.put(player.getName(), player);
            return true;
        }
        return false;
    }

    public void removePlayer(String player){ roomPlayerList.remove(player); }

    // Send a message to only one player
    public void sendMsgToPlayer(Player p, String message){
        if(p.getCtx() != null) p.getCtx().writeAndFlush(message);
    }

    // Send a message to everyone in the room except "excludedPlayer", if excludedPlayer is null it will send the message to everyone
    public void sendMsgToRoom(Player excludedPlayer, String message){
        for(Player currPlayer : roomPlayerList.values()){
            ChannelHandlerContext outgoingCtx = currPlayer.getCtx();
            if(currPlayer.equals(excludedPlayer) || outgoingCtx == null) continue;
            outgoingCtx.writeAndFlush(message);
        }
    }

    public Boolean isRoomEmpty(){
        System.out.println(roomPlayerList.keySet());
        for(String name: roomPlayerList.keySet())
            if(!roomPlayerList.get(name).isComputer())
                return false;
        return true;
    }
}
