package softwaredesign;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.javatuples.Pair;

public class ClientHandler extends SimpleChannelInboundHandler<String>{
    /*
     * Handle message received from server.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message){
        System.out.println(message);
        String[] commands = message.split(" ");
        switch(commands[0]){
            case "START":
                System.out.println("The game started");
                break;
            case "ROOMCREATED":
                System.out.println("Room has been created. Waiting for players to connect...");
                break;
            case "ROOMFULL":
                System.out.println("The room is full, play offline or quit? Answer option: offline/quit");
                break;
            case "CONNECTEDTOSERVER":
                System.out.println("Connection Successful.");
                if(commands[1].equals("NOROOM")){
                    System.out.println("No room has been created, create one now or play offline?" +
                            "Answer options: create,offline");
                }else if(commands[1].equals("ROOMAVAILABLE")){
                    System.out.println("A room has already been created, try to join or play offline? " +
                            "Answer options: join/offline");
                }
                break;
            case "JOINED":
                System.out.println(commands[1] + " joined the game.");
                if(commands[1].equals("START")) System.out.println("The game started");
                break;
            case "JOINSUCCESS":
                System.out.println("You joined the game.");
                System.out.println("Players in the room: " + commands[2]);
                if(commands[1].equals("START")) System.out.println("The game started");
                break;
            case "LEFT":
                System.out.println(commands[1] + " left the game.\n Players:" + commands[2]);
                break;
            case "UPDATEDECKS":
                ClientProgram.deck = Pair.with(commands[1],commands[2]);
                ClientProgram.discardDeck = Pair.with(commands[3],commands[4]);
                break;
            case "UPDATEHAND":
                for(int i = 1; i < commands.length;i++) ClientProgram.ownHand.add(commands[i]);
                break;

        }
    }
}
