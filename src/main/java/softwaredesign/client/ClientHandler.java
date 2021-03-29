package softwaredesign.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;

public class ClientHandler extends SimpleChannelInboundHandler<String>{
    /*
     * Handle message received from server.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message){
        String tempString = "";     // Used for reconstructing messages with multiple whitespaces, saves a declaration for multiple "cases"
        String[] commands = message.trim().split(" ");
//        System.out.println("Message received from server: " + message);
        ClientProgram.serverMessage.add(message);
        switch(commands[0]){
            case "START":
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "LEAVEREGISTERED":
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "USERNAMEACCEPTED":
                ClientProgram.username = commands[1];
                break;
            case "DIED":
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "PLACEKITTEN":
                ClientProgram.ownHand.remove("ExplodingKittenCard");
                break;
            case "ENDED":
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "PLAYCONFIRMED":
                ClientProgram.ownHand.remove(ClientProgram.requestedCard);
                break;
            case "JOINED":
                ClientProgram.playerNamesAndHandSizes.put(commands[1], -1);
                break;
            case "WINNER":
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "JOINSUCCESS":
                String[] playersInRoom = commands[1].split("@@");
                for(String player : playersInRoom) {
                    tempString = tempString + player + " ";
                    ClientProgram.playerNamesAndHandSizes.put(player, -1);
                }
                String[] rules = commands[2].split(",");
                ClientProgram.gameRules = new String[]{rules[0], rules[1]};
                break;
            case "LEFT":
                ClientProgram.playerNamesAndHandSizes.remove(commands[1]);
                break;
            case "UPDATEHAND":
                for(int i = 1; i < commands.length;i++) ClientProgram.ownHand.add(commands[i]);
                break;
            case "PLAYER":
                switch(commands[2]){
                    case "EXPLODED":
                        ClientProgram.playerNamesAndHandSizes.remove(commands[1]);
                        break;
                    default:
                        break;
                }
                break;
            case "UPDATEPLAYERHANDS":
                for(int i = 1; i < commands.length; i+=2) {
                    if (ClientProgram.playerNamesAndHandSizes.containsKey(commands[i])) {
                        ClientProgram.playerNamesAndHandSizes.replace(commands[i], Integer.parseInt(commands[i + 1]));
                    }
                }
                break;
            case "CREATEPLAYERHANDS":
                for(int i = 1; i < commands.length; i+=2) {
                    ClientProgram.playerNamesAndHandSizes.put(commands[i], Integer.parseInt(commands[i + 1]));
                }
                break;
            case "UPDATEDECKS":
                ClientProgram.deckSize = commands[1];
                if(commands[2] != null) ClientProgram.discardDeckTop = commands[2];
                break;
            default:
                break;
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        System.out.println("Server has ended connection abruptly");
        System.out.println(cause);
        ClientProgram.serverMessage.add("SERVERCRASH");
        ctx.close();
    }
}
