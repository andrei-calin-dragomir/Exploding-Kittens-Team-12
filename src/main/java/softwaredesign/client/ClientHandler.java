package softwaredesign.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;

public class ClientHandler extends SimpleChannelInboundHandler<String>{
    /*
     * Handle messages received from server.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message){
        String tempString = "";     // Used for reconstructing messages with multiple whitespaces, saves a declaration for multiple "cases"
        String[] commands = message.trim().split(" ");
        ClientInfo.getServerMessage().add(message);
        switch(commands[0]){
            case "START":
            case "LEAVEREGISTERED":
            case "DIED":
            case "ENDED":
            case "WINNER":
                ClientInfo.setOwnHand(new ArrayList<>());  // Empty the hand because the player stopped playing in some way or form.
                break;
            case "USERNAMEACCEPTED":
                ClientInfo.setUsername(commands[1]);
                break;
            case "PLACEKITTEN":
                ClientInfo.getOwnHand().remove("ExplodingKittenCard");
                break;
            case "PLAYCONFIRMED":
                ClientInfo.getOwnHand().remove(ClientInfo.getRequestedCard());
                break;
            case "JOINED":
                ClientInfo.getPlayersInfo().put(commands[1], -1);
                break;
            case "JOINSUCCESS":
                String[] playersInRoom = commands[1].split("@@");
                for(String player : playersInRoom) {
                    tempString = tempString + player + " ";
                    ClientInfo.getPlayersInfo().put(player, -1);
                }
                String[] rules = commands[2].split(",");
                ClientInfo.setGameRules(new String[]{rules[0], rules[1]});
                break;
            case "LEFT":
                ClientInfo.getPlayersInfo().remove(commands[1]);
                break;
            case "UPDATEHAND":
                for(int i = 1; i < commands.length;i++) ClientInfo.getOwnHand().add(commands[i]);
                break;
            case "PLAYER":
                switch(commands[2]){
                    case "EXPLODED":
                        ClientInfo.getPlayersInfo().remove(commands[1]);
                        break;
                    default:
                        break;
                }
                break;
            case "UPDATEPLAYERHANDS":
                for(int i = 1; i < commands.length; i+=2) {
                    if (ClientInfo.getPlayersInfo().containsKey(commands[i])) {
                        ClientInfo.getPlayersInfo().replace(commands[i], Integer.parseInt(commands[i + 1]));
                    }
                }
                break;
            case "CREATEPLAYERHANDS":
                for(int i = 1; i < commands.length; i+=2) {
                    ClientInfo.getPlayersInfo().put(commands[i], Integer.parseInt(commands[i + 1]));
                }
                break;
            case "UPDATEDECKS":
                ClientInfo.setDeckSize(commands[1]);
                if(commands[2] != null) ClientInfo.setDiscardTop(commands[2]);
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Server has ended connection abruptly");
        System.out.println(cause);
        ClientInfo.getServerMessage().add("SERVERCRASH");
        ctx.close();
    }
}
