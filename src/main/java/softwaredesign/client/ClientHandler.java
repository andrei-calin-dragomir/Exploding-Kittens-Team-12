package softwaredesign.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String>{
    /*
     * Handle message received from server.
     */

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message){
        String tempString = "";     // Used for reconstructing messages with multiple whitespaces, saves a declaration for multiple "cases"
        String[] commands = message.trim().split(" ");
        switch(commands[0]){
            case "START":
                System.out.println("The game started!");
                break;
            case "LEAVEREGISTERED":
                System.out.println("You have left the game!");
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
            case "CANTSTART":
                System.out.println("You are not allowed to start the game, " + commands[1] + " is the game master.");
                break;
            case "ROOMNOTFOUND":
                System.out.println("That room doesn't exist");
                break;
            case "NOSTART":
                System.out.println("Not enough players, " + commands[1] + " more needed to start the game!");
                break;
            case "NOTALLOWED":
                if(commands[1].equals("DEAD")) System.out.println("You can't do that because you have already exploded.");
            case "CONNECTEDTOSERVER":
                System.out.println("Connection Successful.");
                switch(commands[1]){
                    case "NOROOM":
                        System.out.println("No room has been created, create one now or play offline?" +
                                "Answer options: create,offline");
                        break;
                    case "ROOMAVAILABLE":
                        String[] allRooms = commands[2].split(",");
                        for(String room : allRooms) tempString = tempString + room + " ";
                        System.out.println(allRooms.length + " room have been found, join one or create a custom room?\nAvailable rooms: " + tempString); // fix grammer
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
                String[] playersInRoom = commands[1].split("@@");
                for(String player : playersInRoom) tempString = tempString + player + " ";
                System.out.println("You joined the game.\nPlayers in the room: " + tempString);
                break;
            case "LEFT":
                String[] playersInRoom2 = commands[2].split("@@");
                for(String player : playersInRoom2) tempString = tempString + player + " ";
                System.out.println(commands[1] + " left the game.\nPlayers in the room: " + tempString);
                break;
            case "UPDATEDECKS":
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
            case "CHAT":
                for(int i = 1; i < commands.length; ++i) tempString = tempString + commands[i] + " ";
                System.out.println(tempString);
                break;
            default:
                System.out.println("Unknown server command, wtf?");
                System.out.println(message);
                break;
        }
    }
}
