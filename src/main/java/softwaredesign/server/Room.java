package softwaredesign.server;

import io.netty.channel.ChannelHandlerContext;
import softwaredesign.cards.DefuseCard;
import softwaredesign.cards.ExplodingKittenCard;

import java.util.*;

public class Room {
    public HashMap<String, Client> roomPlayerList = new HashMap<>();
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
        if(onlineGame.gameManager != null &&  !isAlive(ctx)){
            ctx.writeAndFlush("NOTALLOWED DEAD");
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
                System.out.println(Arrays.toString(message));
                onlineGame.gameManager.mainDeck.insertCard(new ExplodingKittenCard(),Integer.parseInt(message[1]));
                sendMessageToRoomClients(null, "UPDATEDECKS " + onlineGame.gameManager.mainDeck.getDeckSize()
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

    private Boolean isAlive(ChannelHandlerContext ctx){ return onlineGame.gameManager.isAlive(getClientName(ctx)); }
    public int getMaxPlayers(){ return gameRules[0]; }
    private boolean hasFreeSpots(){ return roomPlayerList.size() < getMaxPlayers(); }
    public HashMap<String, Client> getRoomPlayerList() { return roomPlayerList; }

    public String getHostName(){ return currentHost.getClientName(); }
    public String getRoomName() { return roomName; }

    public String playerListAsString(String delim){
        ArrayList<String> allPlayers = new ArrayList<>();
        for(Client client : roomPlayerList.values()) allPlayers.add(client.getClientName());
        return String.join(delim, allPlayers); // @@ is the delimiter which the client side will use
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
    private String getClientName(ChannelHandlerContext ctx){
        for(Client client : roomPlayerList.values())
            if(client.getCtx() != null && client.getCtx().equals(ctx))
                return client.getClientName();
        return null;
    }

    private ChannelHandlerContext getClientCTX(String clientName){ return roomPlayerList.get(clientName).getCtx(); }

    private Client getClientByName(String name){
        for(Client cli : roomPlayerList.values()) if(name == cli.getClientName()) return cli;
        return null;
    }

    public boolean addPlayer(Client client){
        if(hasFreeSpots()) {
            roomPlayerList.put(client.getClientName(), client);
            return true;
        }
        return false;
    }

    // Is there a case when a player has to be removed but he is not in roomsPlayersList? This is not checked
    public void removePlayer(String player){
        System.out.println(roomPlayerList.toString());
        roomPlayerList.remove(getClientCTX(player));
        System.out.println(roomPlayerList.toString());
    }

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
