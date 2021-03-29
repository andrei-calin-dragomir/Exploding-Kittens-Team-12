package softwaredesign.server;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.cards.*;
import softwaredesign.core.Player;
import softwaredesign.core.State;

import java.util.*;

public class Room {
    public LinkedHashMap<String, Player> roomPlayerList = new LinkedHashMap<>();
    private final int[] gameRules; // 0: Max players, 1: NumberOfComputers
    private final ServerHeldGame onlineGame = new ServerHeldGame(this);
    private Player currentHost;
    private String deckName;
    private final String roomName;
    public String getRoomName(){ return this.roomName; }

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
            case "START":
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
            case "PLACE":
                if(onlineGame.isExploding()) {
                    onlineGame.placeExploding(Integer.parseInt(message[1]));
                    if(onlineGame.getCurrentPlayer().isComputer()) onlineGame.gameManager.endTurn();
                    else onlineGame.nextTurn();
                }
                break;
            case "GIVE":
                onlineGame.giveCard(Integer.parseInt(message[1]),message[2],getClientName(ctx));
                sendGameStateUpdates("UPDATEPLAYERHANDS");
                break;
            case "PLAY":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())){
                    System.out.println(message[1]);
                    int index = Integer.parseInt(message[1]);
                    System.out.println(index);
                    if(index < 0 || onlineGame.gameManager.getCurrentPlayerHand().getHandSize() - 1 < index){
                        ctx.writeAndFlush("NOTALLOWED INVALIDPLAY");
                    }
                    else if(onlineGame.gameManager.getCurrentPlayerHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && !onlineGame.isExploding()){
                        ctx.writeAndFlush("NOTALLOWED NOTEXPLODING");
                    }
                    else if(!onlineGame.gameManager.getCurrentPlayerHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && onlineGame.isExploding()){
                        ctx.writeAndFlush("NOTALLOWED MUSTDEFUSE");
                    }
                    else {
                        String target = "";
                        if(message.length > 2) target = message[2];
                        ctx.writeAndFlush("PLAYCONFIRMED");
                        onlineGame.handlePlayAction(index, target);
                    }
                }
                break;
            case "DRAW":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())) onlineGame.handleDrawAction();
                else ctx.writeAndFlush("NOTALLOWED NOTYOURTURN");
                break;
            case "CHAT":
                String buildMsg = "";
                for(int i = 1; i < message.length; ++i) buildMsg = buildMsg + message[i] + " ";
                sendMsgToRoom(null, "CHAT " + getClientName(ctx) + ": " + buildMsg);
                break;
            default:
                ctx.writeAndFlush("Unknown command, try again");
                break;
        }
    }

    private Boolean isAlive(ChannelHandlerContext ctx){ return onlineGame.gameManager.isAlive(getClientName(ctx)); }
    private Boolean hasFreeSpots(){ return roomPlayerList.size() < getMaxPlayers(); }
    public Integer getMaxPlayers(){ return gameRules[0]; }
    public String getDeckName() { return deckName; }
    public String getHostName(){ return currentHost.getName(); }
    public HashMap<String, Player> getRoomPlayerList() { return roomPlayerList; }
    public void assignNewHost(){ currentHost = roomPlayerList.values().iterator().next(); }
    public Boolean hasStarted(){ return onlineGame.gameManager != null; }

    public int[] getGameRules() {
        return gameRules;
    }

    public String playerListAsString(){
        ArrayList<String> allPlayers = new ArrayList<>();
        for(Player player : roomPlayerList.values()) allPlayers.add(player.getName());
        return String.join("@@", allPlayers); // @@ is the delimiter which the client side will use
    }

    public void sendGameStateUpdates(String typeOfUpdate){
        String topCardName = "NOCARD";
        String playerHandSizes = "";
        Card topCard = onlineGame.gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();

        for(Player player: onlineGame.gameManager.getAlivePlayers())
            playerHandSizes += player.getName() + " " + player.getHand().getHandSize() + " ";

        sendMsgToRoom(null, "UPDATEDECKS " + onlineGame.getDeckSize() + " " + topCardName);
        sendMsgToRoom(null,typeOfUpdate + " " + playerHandSizes);
    }

    public String getClientName(ChannelHandlerContext ctx){
        for(Player player : roomPlayerList.values())
            if(player.getCtx() != null && player.getCtx().equals(ctx))
                return player.getName();
        return null;
    }

    public ChannelHandlerContext getClientCTX(String clientName){ return roomPlayerList.get(clientName).getCtx(); }

    public boolean addPlayer(Player player){
        if(hasFreeSpots()) {
            roomPlayerList.put(player.getName(), player);
            return true;
        }
        return false;
    }

    public void removePlayer(String player){ roomPlayerList.remove(player); }

    // Send a message to everyone in the room except "excludedPlayer", if excludedPlayer is null it will send the message to everyone
    public void sendMsgToRoom(Player excludedPlayer, String message){
        for(Player currPlayer : roomPlayerList.values()){
            ChannelHandlerContext outgoingCtx = currPlayer.getCtx();
            if(currPlayer.equals(excludedPlayer) || outgoingCtx == null) continue;
            outgoingCtx.writeAndFlush(message);
        }
    }

    public void sendMsgToPlayer(Player p, String message){
        if(p.getCtx() != null) p.getCtx().writeAndFlush(message);
    }

    public Boolean isRoomEmpty(){
        System.out.println(roomPlayerList.keySet());
        for(String name: roomPlayerList.keySet())
            if(!roomPlayerList.get(name).isComputer())
                return false;
        return true;
    }
}
