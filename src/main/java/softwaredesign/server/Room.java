package softwaredesign.server;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.cards.*;
import softwaredesign.core.Player;

import java.util.*;

public class Room {
    public LinkedHashMap<String, Client> roomPlayerList = new LinkedHashMap<>();
    private final int[] gameRules; // 0: Max players, 1: NumberOfComputers
    private final ServerHeldGame onlineGame = new ServerHeldGame(this);
    private Client currentHost;
    private final String roomName;
    public String getRoomName(){ return this.roomName; }

    public Room(Client host, String name, int maxPlayers, int computerAmount){
        gameRules = new int[]{maxPlayers, computerAmount};
        currentHost = host;
        roomName = name;
        addPlayer(host);
        host.setCurrentRoom(this);
        for(int i = 0; i < computerAmount; ++i) {
            Computer newComputer = new Computer(this, i);
            roomPlayerList.put(newComputer.getClientName(), newComputer);
        }
    }

    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] message = msg.split("\\s+");
        if(onlineGame.gameManager != null && !isAlive(ctx) && !message[0].toUpperCase(Locale.ROOT).equals("CHAT")){
            if(onlineGame.gameManager.alivePlayers.size() != 0) ctx.writeAndFlush("NOTALLOWED DEAD");
            else ctx.writeAndFlush("ENDED");
            return;
        }
        switch(message[0].toUpperCase(Locale.ROOT)){
            case "START":
                System.out.println(roomPlayerList.toString());
                System.out.println(getHostName());
                System.out.println(getClientName(ctx));
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
                if(Integer.parseInt(message[1]) >= 0 && Integer.parseInt(message[1]) < onlineGame.gameManager.mainDeck.getDeckSize()){
                    onlineGame.gameManager.mainDeck.insertCard(new ExplodingKittenCard(),Integer.parseInt(message[1]));
                    sendGameStateUpdates("UPDATEPLAYERHANDS");
                }else ctx.writeAndFlush("NOTALLOWED BADPLACE");
                break;
            case "PLAY":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())){
                    int index = Integer.parseInt(message[1]);
                    if(index < 0 || onlineGame.gameManager.getCurrentPlayerHand().getHandSize() - 1 < index){
                        ctx.writeAndFlush("NOTALLOWED INVALIDPLAY");
                        break;
                    }
                    if(onlineGame.gameManager.getCurrentPlayerHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && !onlineGame.drawnExplodingKitten){
                        ctx.writeAndFlush("NOTALLOWED NOTEXPLODING");
                        break;
                    }
                    if(!onlineGame.gameManager.getCurrentPlayerHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && onlineGame.drawnExplodingKitten){
                        ctx.writeAndFlush("NOTALLOWED MUSTDEFUSE");
                        break;
                    }
//TODO                    if(onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new FavorCard())){
//                        ctx.writeAndFlush("PLAYCONFIRMED");
//                        break;
//                    }
                    ctx.writeAndFlush("PLAYCONFIRMED");
                    onlineGame.handleAction("play " + message[1]);
                }
                break;
//TODO            case "TARGETED":
//                if(getRoomPlayerList().containsKey(message[1])){
//                    onlineGame.handleAction("play " + onlineGame.gameManager.getCurrentPlayerHand().getHand().indexOf(new FavorCard()));
//                    sendMessageToSingleRoomClient(message[1],"GIVECARD " + getClientName(ctx));
//                }else ctx.writeAndFlush("NOTALLOWED WRONGNAME");
            case "DRAW":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())) onlineGame.handleAction("draw");
                else ctx.writeAndFlush("NOTALLOWED NOTYOURTURN");
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

    private Boolean isAlive(ChannelHandlerContext ctx){ return onlineGame.gameManager.isAlive(getClientName(ctx)); }
    public int getMaxPlayers(){ return gameRules[0]; }
    private boolean hasFreeSpots(){ return roomPlayerList.size() < getMaxPlayers(); }
    public HashMap<String, Client> getRoomPlayerList() { return roomPlayerList; }
    public String getHostName(){ return currentHost.getClientName(); }

    public String playerListAsString(String delim){
        ArrayList<String> allPlayers = new ArrayList<>();
        for(Client client : roomPlayerList.values()) allPlayers.add(client.getClientName());
        return String.join(delim, allPlayers); // @@ is the delimiter which the client side will use
    }

    public void sendGameStateUpdates(String typeOfUpdate){
        String topCardName = "NOCARD";
        Card topCard = onlineGame.gameManager.discardDeck.getTopCard();
        if(topCard != null) topCardName = topCard.getName();
        sendMessageToRoomClients(null, "UPDATEDECKS " + onlineGame.gameManager.mainDeck.getDeckSize() + " " + topCardName);
        StringBuilder playerHandSizes = new StringBuilder();
        for(Player player: onlineGame.gameManager.alivePlayers) {
            playerHandSizes.append(player.getName()).append(" ").append(player.getHand().getHandSize()).append(" ");
        }
        sendMessageToRoomClients(null,typeOfUpdate + " " + playerHandSizes.toString());
    }

    public String getClientName(ChannelHandlerContext ctx){
        for(Client client : roomPlayerList.values())
            if(client.getCtx() != null && client.getCtx().equals(ctx))
                return client.getClientName();
        return null;
    }

    public ChannelHandlerContext getClientCTX(String clientName){ return roomPlayerList.get(clientName).getCtx(); }

    public boolean addPlayer(Client client){
        if(hasFreeSpots()) {
            roomPlayerList.put(client.getClientName(), client);
            return true;
        }
        return false;
    }

    public void removePlayer(String player){ roomPlayerList.remove(player); }

    public void sendMessageToRoomClients(String excludedPlayer, String message){
        for(String cli : roomPlayerList.keySet()){
            System.out.println("Yu are here1");
            ChannelHandlerContext outgoingCtx = roomPlayerList.get(cli).getCtx();
            System.out.println("Yu are here2");
            if(cli == excludedPlayer || outgoingCtx == null) continue;
            System.out.println("Yu are here3");
            outgoingCtx.writeAndFlush(message);
            System.out.println("Yu are here4");
        }
    }

    public void sendMessageToSingleRoomClient(String name, String message){
        if(getClientCTX(name) != null) getClientCTX(name).writeAndFlush(message);
    }
}
