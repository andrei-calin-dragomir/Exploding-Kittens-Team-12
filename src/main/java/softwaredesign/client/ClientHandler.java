package softwaredesign.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import softwaredesign.gui.GameViewController;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;

public class ClientHandler extends SimpleChannelInboundHandler<String>{
    /*
     * Handle message received from server.
     */


    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message){
        String tempString = "";     // Used for reconstructing messages with multiple whitespaces, saves a declaration for multiple "cases"
        String[] commands = message.trim().split(" ");
        System.out.println("Message received from server: " + message);
        ClientProgram.serverMessage = message;
        ClientProgram.newMessage = true;
        switch(commands[0]){
            case "START":
                ClientProgram.ownHand = new ArrayList<>();
//                System.out.println("The game started!");
                break;
            case "LEAVEREGISTERED":
                System.out.println("You have left the game!");
                ClientProgram.ownHand = new ArrayList<>();
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
                System.out.println("You died due to a horrible furball explosion! May God rest your soul...\n" +
                        "You are now spectating the game.");
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "PLACEKITTEN":
                ClientProgram.ownHand.remove("ExplodingKittenCard");
                System.out.println("The future of this game is in your hands!\n" +
                        "Place the exploding kitten between the top and bottom cards of the deck. Answer: place index");
                break;
            case "ENDED":
                System.out.println("The game has ended, you can still chat or you can leave with \"leave\"");
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "PLAYCONFIRMED":
//TODO                if(ClientProgram.requestedCard.equals("FavorCard")){
//                    System.out.println("Chose your target... 'target playername'");
//                }
                System.out.println("You played the " + ClientProgram.requestedCard + " card!");
                ClientProgram.ownHand.remove(ClientProgram.requestedCard);
                break;
//TODO            case "GIVECARD":
//                System.out.println("You must give a card to ");
            case "CANTSTART":
                //message= "CANTSTART name", with name being name of room master
                System.out.println("You are not allowed to start the game, " + commands[1] + " is the room master.");
                break;
            case "ROOMNOTFOUND":
                System.out.println("That room doesn't exist");
                break;
            case "NOSTART":
                //message= "NOSTART n", with n = num of players needed
                System.out.println("Not enough players, " + commands[1] + " more needed to start the game!");
                break;
            case "NOTALLOWED":
                if(commands[1].equals("DEAD")) System.out.println("You can't do that because you have already exploded.");
                else if(commands[1].equals("BADPLACE")) System.out.println("Invalid card placement, try placing again.");
                else if(commands[1].equals("NOTEXPLODING")) System.out.println("You can only play a defuse card when you draw an Exploding Kitten!");
                else if(commands[1].equals("MUSTDEFUSE")) System.out.println("You have to play a defuse card when you draw an Exploding Kitten!");
                else if(commands[1].equals("INVALIDPLAY")) System.out.println("Trying to play invalid card.");
                else if(commands[1].equals("NOTYOURTURN")) System.out.println("It is not your turn.");
//TODO                else if(commands[1].equals("WRONGNAME")) System.out.println("The player name you entered is wrong, try again.");
                break;
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
            case "WINNER":
                String theWinner = commands[1];
                if(theWinner == ClientProgram.username) System.out.println("Congratulations, you have won the game!");
                else System.out.println("The winner of the game is: " + theWinner);
                ClientProgram.ownHand = new ArrayList<>();
                break;
            case "JOINSUCCESS":
                String[] playersInRoom = commands[1].split("@@");
                for(String player : playersInRoom) {
                    tempString = tempString + player + " ";
                }
                System.out.println("You joined the game.\nPlayers in the room: " + tempString);
                break;
            case "LEFT":
                String[] playersInRoom2 = commands[2].split("@@");
                ClientProgram.playerNamesAndHandSizes.remove(commands[1]);
                System.out.println(commands[1] + " left the game.\n" +
                        "Players in the room: " + ClientProgram.playerNamesAndHandSizes.keySet().toString());
                break;
            case "UPDATEHAND":
                for(int i = 1; i < commands.length;i++) ClientProgram.ownHand.add(commands[i]);
                System.out.println(ClientProgram.ownHand);
                break;
            case "SEEFUTURE":
                for(int i = 1; i < commands.length; ++i) tempString = tempString + commands[i] + " ";
                System.out.println("The top cards are: " + tempString);
                break;
            case "CHAT":
                for(int i = 1; i < commands.length; ++i) tempString = tempString + commands[i] + " ";
                System.out.println(tempString);
                break;
            case "PLAYER":
                switch(commands[2]){
                    case "exploded":
                        System.out.println(commands[1] + " has just exploded!");
                        ClientProgram.playerNamesAndHandSizes.remove(commands[1]);
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
                    case "played":
                        System.out.println(commands[1] + " played the " + commands[3] + " card.");
                        break;
                }
                break;
            case "UPDATEPLAYERHANDS":
                for(int i = 1; i < commands.length; i+=2) {
                    if (ClientProgram.playerNamesAndHandSizes.containsKey(commands[i])) {
                        ClientProgram.playerNamesAndHandSizes.replace(commands[i], Integer.parseInt(commands[i + 1]));
                    }
                }
//                System.out.println(ClientProgram.playerNamesAndHandSizes.toString());
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
                System.out.println("Unknown server command, wtf?");
                System.out.println(message);
                break;
        }
    }
}
