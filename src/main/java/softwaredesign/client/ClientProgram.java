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

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ClientProgram {
    static String username;
    static ChannelFuture correspondenceChannel;
    static EventLoopGroup group;
    public static ArrayList<String> ownHand = new ArrayList<>();
    public static LinkedHashMap<String,Integer> playerNamesAndHandSizes = new LinkedHashMap<>(); //todo maybe needs a getter/setter?
    public static String requestedCard = "";
    public static String deckSize;
    public static String discardDeckTop;
    public static String serverMessage;
    public static Boolean newMessage;



    public static void main(String[] args) throws Exception {
        ClientProgram.startClient();
    }

    private static Boolean isInteger(String intString){
        try { Integer.parseInt(intString); }
        catch(NumberFormatException e){ return false; }
        return true;
    }

    public static void startClient() throws Exception {
        connectAndLoop();
    }

    private void killConnectionSafely() {
        try{
            correspondenceChannel.channel().closeFuture().sync();
            group.shutdownGracefully();
        } catch (Exception e){
            System.out.println("exception" + e);
        }
    }

    private static void connectAndLoop(){
        final String HOST = "127.0.0.1";
        final int PORT = 8007;
        group = new NioEventLoopGroup();
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
            System.out.println("Attempting connection to " + HOST + " on port " + PORT + "...");
            correspondenceChannel = bootstrap.connect(HOST, PORT).sync();
            System.out.println("Connected");
        }catch(Exception e){
            System.out.println("Connection failed.\nClosing...");
        }
    }
    private static void sendRequestToServer(String message) throws Exception{
        Channel channel = correspondenceChannel.sync().channel();
        channel.writeAndFlush(message);
    }

    private static void playOffline() throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Game offlineGame = new Game();
        offlineGame.start(4,3);
    }

    static public void handleCommand(String cmd){
        String[] cmdList = cmd.split(" ");
        System.out.println("cmdlist = " + Arrays.toString(cmdList));
        try {
            switch (cmdList[0]) {
                case "username":
                    System.out.println("SENDING SIGNAL");
                    sendRequestToServer("USERNAME " + cmdList[1]);
                    break;
                case "start":
                    sendRequestToServer("START");
                    break;
                case "leave":
                    sendRequestToServer("LEAVE");
                    break;
                case "join":
                    sendRequestToServer("JOIN " + cmdList[1]);
                    break;
                case "create":
                    sendRequestToServer("CREATE " + cmdList[1]);
                    break;
                case "offline":
                    playOffline();
                    break;
                case "quit":
                    sendRequestToServer("LEAVE");
                    System.exit(0);
                    break;
                //TODO                    case "target":
                //                        sendRequestToServer("TARGETED " + inputArray[1]);
                case "play":
                    requestedCard = cmdList[1];
                    sendRequestToServer("PLAY " + ClientProgram.ownHand.indexOf(cmdList[1]));
                    break;
                case "draw":
                    sendRequestToServer("DRAW");
                    break;
                case "place":
                    if (cmdList.length != 2)
                        System.out.println("Please specify the location you want to place the card");
                    if (!isInteger(cmdList[1])) System.out.println("Invalid location, try again");
                    else sendRequestToServer("PLACE " + cmdList[1]);
                    break;
                case "chat":
                    sendRequestToServer(cmd);
                    break;
                case "hand":
                    System.out.println(ownHand);
                    break;
                case "exit":
                    break;
                default:
                    System.err.println("Unknown command, try again");
                    break;
            }
        } catch (Exception e){
            System.out.println("Errorrrrr");
            System.exit(0);
        }
    }

    private static String createRoomString(){
        Scanner scanner = new Scanner(System.in);
        int parameter1 = 0;
        int parameter2 = -1;
        String roomName;
        System.out.println("Give a room name: ");
        roomName = scanner.nextLine();
        System.out.println("How many players do you want in your game? Answer options: 2-5");
        while(parameter1 == 0) {
            if (scanner.hasNext()) {
                int roomSize = scanner.nextInt();
                if (roomSize < 2 || roomSize > 5) System.out.println("Cannot handle this number of players.");
                else parameter1 = roomSize;
            }
        }
        System.out.println("How many computers do you want in your game? Minimum: 0 Maximum: " + (parameter1 - 1));
        while(parameter2 == -1){
            if(scanner.hasNext()){
                int numberOfComputers = scanner.nextInt();
                if( numberOfComputers < 0 || numberOfComputers > (parameter1 - 1))
                    System.out.println("Invalid number of computers");
                else parameter2 = numberOfComputers;
            }
        }
        return roomName + "," + parameter1 + "," + parameter2;
    }
}
