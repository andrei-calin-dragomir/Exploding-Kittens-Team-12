package softwaredesign.server;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;

import java.util.*;

public class Room {
    public LinkedHashMap<String, Client> roomPlayerList = new LinkedHashMap<>();
    private int[] gameRules; // 0: Max players, 1: NumberOfComputers
    private ServerHeldGame onlineGame = new ServerHeldGame(this);
    private Client currentHost;
    private String roomName;

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
                    sendMessageToRoomClients(null, "UPDATEDECKS " + onlineGame.gameManager.mainDeck.getDeckSize()
                            + " " + onlineGame.gameManager.discardDeck.getTopCard().getName());
                }else ctx.writeAndFlush("NOTALLOWED BADPLACE");
                break;
            case "PLAY":
                if(getClientName(ctx).equals(onlineGame.getCurrentPlayerName())){
                    int index = Integer.parseInt(message[1]);
                    if(index < 0 || onlineGame.gameManager.getCurrentPlayer().getHand().getHandSize() - 1 < index){
                        ctx.writeAndFlush("NOTALLOWED INVALIDPLAY");
                        break;
                    }
                    if(onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && !onlineGame.drawnExplodingKitten){
                        ctx.writeAndFlush("NOTALLOWED NOTEXPLODING");
                        break;
                    }
                    if(!onlineGame.gameManager.getCurrentPlayer().getHand().getCard(Integer.parseInt(message[1])).equals(new DefuseCard()) && onlineGame.drawnExplodingKitten){
                        ctx.writeAndFlush("NOTALLOWED MUSTDEFUSE");
                        break;
                    }
                    ctx.writeAndFlush("PLAYCONFIRMED");
                    onlineGame.handleAction("play " + message[1]);
                }
                break;
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

    public void sendMessageToRoomClients(ChannelHandlerContext ctx, String message){
        for(Client cli : roomPlayerList.values()){
            ChannelHandlerContext outgoingCtx = cli.getCtx();
            if(outgoingCtx == null || outgoingCtx == ctx) continue;
            outgoingCtx.writeAndFlush(message);
        }
    }

    public void sendMessageToSingleRoomClient(String name, String message){
        if(getClientCTX(name) != null) getClientCTX(name).writeAndFlush(message);
    }
}
