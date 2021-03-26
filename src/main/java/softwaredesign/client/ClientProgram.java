package softwaredesign.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import softwaredesign.server.ServerProgram;

import java.util.*;

public class ClientProgram {
    static String username;
    static ChannelFuture correspondenceChannel;
    public static ArrayList<String> ownHand = new ArrayList<>();
    public static LinkedHashMap<String,Integer> playerNamesAndHandSizes = new LinkedHashMap<>();
    public static String requestedCard = "";
    public static String deckSize;
    public static String discardDeckTop;
    public static String serverMessage;

    public static void main(String[] args) throws Exception {
        ClientProgram.startClient();
    }

    private static Boolean isInteger(String intString){
        try { Integer.parseInt(intString); }
        catch(NumberFormatException e){ return false; }
        return true;
    }

    private static void startClient() throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        if (scanner.hasNext()) username = scanner.nextLine();
        System.out.println("Welcome to Exploding Kittens, " + username + "!");
        System.out.println("Would you like to play offline or online? Answer options: offline/online");
        while (scanner.hasNext()) {
            String[] input = scanner.nextLine().split(" ");
            switch (input[0]) {
                case "offline": startGame(true);break;
                case "online" : startGame(false);break;
                default       : System.out.println("Unexpected command, " +
                        " again.");
            }
        }
    }

    private static void startGame(boolean offline) throws Exception{
        final String HOST = "127.0.0.1";
        final int PORT = 8007;
        Scanner scanner = new Scanner(System.in);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipe = ch.pipeline();

                            pipe.addLast("framer", new LengthFieldBasedFrameDecoder(Short.MAX_VALUE,0,2,0,2));
                            pipe.addLast("framer-prepender", new LengthFieldPrepender(2, false));
                            pipe.addLast(new StringDecoder());
                            pipe.addLast(new StringEncoder());
                            // This is our custom client handler which will have logic for chat.
                            pipe.addLast(new ClientHandler());
                        }
                    });

            // Start the client.
            if(!offline) {
                System.out.println("Attempting connection to " + HOST + " on port " + PORT + "...");
            }
            correspondenceChannel = bootstrap.connect(HOST, PORT).sync();

            if(offline) {
                sendRequestToServer("USERNAME " + username + " SOLO");
                sendRequestToServer("CREATE " + createGame(offline) + " SOLO");
            }
            else
                sendRequestToServer("USERNAME " + username);
            /*
             * Iterate & take chat message inputs from user & then send to server.
             */
            while (scanner.hasNext()) {
                String input = scanner.nextLine();
                String[] inputArray = input.split(" ");
                System.out.println(input);
                switch(inputArray[0].toLowerCase(Locale.ROOT)) {
                    case "start":
                        sendRequestToServer("START");
                        break;
                    case "leave":
                        sendRequestToServer("LEAVE");
                        break;
                    case "join":
                        sendRequestToServer("JOIN " + inputArray[1]);
                        break;
                    case "create":
                        sendRequestToServer("CREATE " + createGame(offline));
                        break;
                    case "quit":
                        sendRequestToServer("LEAVE");
                        group.shutdownGracefully();
                        System.exit(0);
                        break;
//TODO                    case "target":
//                        sendRequestToServer("TARGETED " + inputArray[1]);
                    case "play":
                        String target = "";
                        requestedCard = inputArray[1];
                        if(inputArray.length > 2) target = inputArray[2];
                        sendRequestToServer("PLAY " + ClientProgram.ownHand.indexOf(inputArray[1]) + " " + target);
                        break;
                    case "draw":
                        sendRequestToServer("DRAW");
                        break;
                    case "place":
                        if(inputArray.length != 2) System.out.println("Please specify the location you want to place the card");
                        if(!isInteger(inputArray[1])) System.out.println("Invalid location, try again");
                        else sendRequestToServer("PLACE " + inputArray[1]);
                        break;
                    case "chat":
                        sendRequestToServer(input);
                        break;
                    case "hand":
                        System.out.println(ownHand);
                        break;
                    default:
                        System.out.println("Unknown command, try again");
                        break;
                }
            }
            // Wait until the connection is closed.
            correspondenceChannel.channel().closeFuture().sync();
        }catch(Exception e){
            System.out.println("Connection failed.\nGo offline or try going online again? Answer options: offline/online");
            while (scanner.hasNext()) {
                String[] input = scanner.nextLine().split(" ");
                switch (input[0]) {
                    case "offline":
                        startGame(true);
                        break;
                    case "online" :
                        startGame(false);
                        break;
                    default       : System.out.println("Unexpected command, try again.");
                }
            }
        }finally{
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
    private static void sendRequestToServer(String message) throws Exception{
        Channel channel = correspondenceChannel.sync().channel();
        channel.writeAndFlush(message);
    }

//    private static void playOffline() throws Exception {
//        new ServerProgram();
//        startGame(true);
////        Game offlineGame = new Game();
////        //offlineGame.start(4,3);
//    }

    private static String createGame(boolean offline) {
        Scanner scanner = new Scanner(System.in);
        int parameter1 = 0;
        int parameter2 = -1;
        String roomName = "";
        if (!offline) {
            System.out.println("Give a room name: ");
            roomName = scanner.nextLine();
            System.out.println("How many players do you want in your game? Answer options: 2-5");
            while (parameter1 == 0) {
                if (scanner.hasNext()) {
                    int roomSize = scanner.nextInt();
                    if (roomSize < 2 || roomSize > 5) System.out.println("Cannot handle this number of players.");
                    else parameter1 = roomSize;
                }
            }
            System.out.println("How many computers do you want in your game? Minimum: 0 Maximum: " + (parameter1 - 1));
            while (parameter2 == -1) {
                if (scanner.hasNext()) {
                    int numberOfComputers = scanner.nextInt();
                    if (numberOfComputers < 0 || numberOfComputers > (parameter1 - 1))
                        System.out.println("Invalid number of computers");
                    else parameter2 = numberOfComputers;
                }
            }
            return roomName + "," + parameter1 + "," + parameter2;

        }
        else {
            roomName = "SOLO_GAME";
            System.out.println("How many computers do you want in your game? Minimum: 0 Maximum: " + 3);
            while (parameter2 == -1) {
                if (scanner.hasNext()) {
                    int numberOfComputers = scanner.nextInt();
                    if (numberOfComputers < 0 || numberOfComputers > 4)
                        System.out.println("Invalid number of computers");
                    else parameter2 = numberOfComputers;
                }
            }
        }
        return roomName + "," + 1 + "," + parameter2;

    }
}
