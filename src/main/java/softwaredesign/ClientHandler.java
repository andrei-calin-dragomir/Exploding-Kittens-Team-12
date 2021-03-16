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
        String[] commands = message.trim().split(" ");
        switch(commands[0]){
            case "START":
                System.out.println("The game started!");
                break;
            case "ROOMCREATED":
                System.out.println("Room has been created. Waiting for players to connect...");
                break;
            case "ROOMFULL":
                System.out.println("The room is full, play offline or quit? Answer option: offline/quit");
                break;
            case "EXPLODING":
                System.out.println("You drew an exploding kitten, you must defuse it immediately!");
                break;
            case "DIED":
                System.out.println("You died due to a horrible furball explosion! May God rest your soul...");
                System.exit(0);
                break;
            case "PLACEKITTEN":
                System.out.println("The future of this game is in your hands!\n" +
                        "Place the exploding kitten between the top and bottom cards of the deck. Answer: place index");
                break;
            case "PLAYCONFIRMED":
                System.out.println("You played the " + ClientProgram.requestedCard + " card!");
                if(ClientProgram.ownHand.contains("exploding_kitten")) ClientProgram.ownHand.remove("exploding_kitten");
                ClientProgram.ownHand.remove(ClientProgram.requestedCard);
                break;
            case "CONNECTEDTOSERVER":
                System.out.println("Connection Successful.");
                switch(commands[1]){
                    case "NOROOM":
                        System.out.println("No room has been created, create one now or play offline?" +
                                "Answer options: create,offline");
                        break;
                    case "ROOMAVAILABLE":
                        System.out.println("A room has already been created, try to join or play offline? " +
                                "Answer options: join/offline");
                        break;
                }
                break;
            case "TURN":
                if(commands[1].equals(ClientProgram.username)) System.out.println("It's your turn!");
                else System.out.println("It's " + commands[1] + "'s turn!");
                break;
            case "JOINED":
                System.out.println(commands[1] + " joined the game.");
                break;
            case "JOINSUCCESS":
                System.out.println("You joined the game.\nPlayers in the room: " + commands[1]);
                break;
            case "LEFT":
                System.out.println(commands[1] + " left the game.\n Players in the room:" + commands[2]);
                break;
            case "UPDATEDECKS":
                System.out.println(commands.toString());
                ClientProgram.deckSize = commands[1];
                if(commands[2] != null) ClientProgram.discardDeckTop = commands[2];
                break;
            case "COMPUTER":
                switch(commands[2]){
                    case "exploded":
                        System.out.println(commands[1] + " has just exploded!");
                        break;
                    case "drew":
                        System.out.println(commands[1] + " has drawn a card.");
                        break;
                    case "drewexp":
                        System.out.println(commands[1] + " has drawn an exploding kitten!");
                        break;
                    case "defused":
                        System.out.println(commands[1] + " has defused the kitten.");
                        break;
                }
                break;
            case "UPDATEHAND":
                for(int i = 1; i < commands.length;i++) ClientProgram.ownHand.add(commands[i]);
                System.out.println(ClientProgram.ownHand);
                break;

        }
    }
}
