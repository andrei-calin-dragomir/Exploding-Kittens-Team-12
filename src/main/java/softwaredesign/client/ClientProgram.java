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
    public static String username;
    static ChannelFuture correspondenceChannel;
    static EventLoopGroup group;
    public static ArrayList<String> ownHand = new ArrayList<>();
    public static LinkedHashMap<String,Integer> playerNamesAndHandSizes = new LinkedHashMap<>(); //todo maybe needs a getter/setter?
    public static String requestedCard = "";
    public static String currentServer = "";
    public static String roomName = "";
    public static String[] gameRules = new String[]{"", ""};
    public static String deckSize;
    public static String currentDeck;
    public static String discardDeckTop;
    public static Boolean offlineGame;
    public static ServerProgram server;
    public static LinkedList<String> serverMessage = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        ClientProgram.startClient("127.0.0.1",false);
    }
    public static Boolean isInteger(String intString){
        try { Integer.parseInt(intString); }
        catch(NumberFormatException e){ return false; }
        return true;
    }

    public static void startClient(String IP, boolean offline) {
        if(offline){
            server = new ServerProgram();
            server.start();
            connectAndLoop("127.0.0.1", true);
        }else{
            connectAndLoop(IP, false);
        }
    }

    public static void killOffline(){
        server.stop();
        killConnectionSafely();
    }

    public static void killConnectionSafely() {
        try{
            if(correspondenceChannel != null){
                correspondenceChannel.channel().writeAndFlush("DISCONNECTING");
                correspondenceChannel.channel().closeFuture().sync();
                group.shutdownGracefully();
                correspondenceChannel = null;
                group = null;
                currentServer = "";
            }

        } catch (Exception e){
            System.out.println("exception" + e);
        }
    }
    public static Boolean connectAndLoop(String HOST,boolean offline){
        System.out.println("Trying to connect");
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
            System.out.println("Trying to connect 2");
            if(!offline) {
                System.out.println("Attempting connection to " + HOST + " on port " + PORT + "...");
                correspondenceChannel = bootstrap.connect(HOST, PORT).sync();
                System.out.println("Connected");

            }else{
                correspondenceChannel = bootstrap.connect(HOST, PORT).sync();
                System.out.println("Connected");
                sendRequestToServer("USERNAME You SOLO");
            }
            currentServer = HOST;
            offlineGame = offline;
            return true;
        }catch(Exception e){
            System.out.println("Connection failed.\nClosing...");
            return false;
        }

    }
    public static void handleCommand(String cmd){
        String[] cmdList = cmd.split(" ");
        System.out.println("handling commands: " + Arrays.toString(cmdList));
        try {
            switch (cmdList[0]) {
                case "list_rooms":
                    sendRequestToServer("AVAILABLEROOMS");
                    break;
                case "username":
                    sendRequestToServer("USERNAME " + cmdList[1]);
                    break;
                case "start":
                    sendRequestToServer("START");
                    break;
                case "leave":
                    playerNamesAndHandSizes = new LinkedHashMap<>();
                    playerNamesAndHandSizes.put(username, -1);
                    sendRequestToServer("LEAVEROOM");
                    break;
                case "join":
                    sendRequestToServer("JOIN " + cmdList[1]);
                    break;
                case "create":
                    sendRequestToServer("CREATE " + cmdList[1]);
                    break;
                case "create_solo":
                    sendRequestToServer("CREATE" + cmdList[1] + "SOLO");
                case "offline":
                    killConnectionSafely();
                    startClient("127.0.0.1", true);
                    break;
                case "quit":
                    killConnectionSafely();
                    System.exit(0);
                    break;
                case "play":
                    requestedCard = cmdList[1];
                    String target = "";
                    if(cmdList.length > 2) target = cmdList[2];
                    sendRequestToServer("PLAY " + ClientProgram.ownHand.indexOf(cmdList[1]) + " " + target);
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
                case "give":
                    //GIVE + CARD + TARGET
                    sendRequestToServer("GIVE " + ownHand.indexOf(cmdList[1]) + " " + cmdList[2]);
                    ownHand.remove(ownHand.indexOf(cmdList[1]));
                    break;
                default:
                    System.out.println("Unknown command, try again");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Errorrrrr: " + e);
            System.exit(0);
        }
    }
    private static void sendRequestToServer(String message) throws Exception{
        Channel channel = correspondenceChannel.sync().channel();
        channel.writeAndFlush(message);
    }
}
