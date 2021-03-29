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

import java.util.LinkedHashMap;

public class ClientProgram {
    static ChannelFuture correspondenceChannel;
    static EventLoopGroup group;


    public static Boolean isInteger(String intString){
        try { Integer.parseInt(intString); }
        catch(NumberFormatException e){ return false; }
        return true;
    }

    // starts the client, which will communicate with the server. If its offline it will create a multithreaded shadow server which it can communicate with (aka local play)
    public static void startClient(String IP, boolean offline) {
        if(offline){
            ClientInfo.setServer(new ServerProgram());
            ClientInfo.getServer().start();
            connectAndLoop("127.0.0.1", true);
        }else{
            connectAndLoop(IP, false);
        }
    }

    // Kills the server if you exit out of offline
    public static void killOffline(){
        killConnectionSafely();
        ClientInfo.getServer().stop();
    }

    // Disconnects from a server and resets server info so that reconnects are possible
    public static void killConnectionSafely() {
        try{
            if(correspondenceChannel != null){
                correspondenceChannel.channel().writeAndFlush("DISCONNECTING");
                correspondenceChannel.channel().closeFuture().sync();
                group.shutdownGracefully();
                correspondenceChannel = null;
                group = null;
                ClientInfo.setCurrentServer("");
            }

        } catch (Exception e){
            System.out.println("exception" + e);
        }
    }

    // Tries to connect to a server, will return false if it fails and true if it succeeds
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
            ClientInfo.setCurrentServer(HOST);
            ClientInfo.setOfflineGame(offline);
            return true;
        }catch(Exception e){
            System.out.println("Connection failed.\nClosing...");
            return false;
        }
    }

    // Handles commands passed by the GUI, which will then be sent on to the server
    public static void handleCommand(String cmd){
        String[] cmdList = cmd.split(" ");
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
                    ClientInfo.setPlayerNamesAndHandSizes(new LinkedHashMap<>());
                    ClientInfo.getPlayerNamesAndHandSizes().put(ClientInfo.getUsername(), -1);
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
                    ClientInfo.setRequestedCard(cmdList[1]);
                    String target = "";
                    if(cmdList.length > 2) target = cmdList[2];
                    sendRequestToServer("PLAY " + ClientInfo.getOwnHand().indexOf(cmdList[1]) + " " + target);
                    break;
                case "draw":
                    sendRequestToServer("DRAW");
                    break;
                case "place":
                    sendRequestToServer("PLACE " + cmdList[1]);
                    break;
                case "chat":
                    sendRequestToServer(cmd);
                    break;
                case "hand":
                    System.out.println(ClientInfo.getOwnHand());
                    break;
                case "give":
                    //GIVE + CARD + TARGET
                    sendRequestToServer("GIVE " + ClientInfo.getOwnHand().indexOf(cmdList[1]) + " " + cmdList[2]);
                    ClientInfo.getOwnHand().remove(ClientInfo.getOwnHand().indexOf(cmdList[1]));    // Removes card you are giving away
                    break;
                default:
                    System.out.println("Unknown command, try again");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.exit(0);
        }
    }

    // Sends the message to the server
    private static void sendRequestToServer(String message) throws Exception{
        Channel channel = correspondenceChannel.sync().channel();
        channel.writeAndFlush(message);
    }
}
