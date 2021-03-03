package softwaredesign;

import java.util.Scanner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientProgram {

    static String username;
    static int sizeOfGame;
    static int numberOfComputers;
    static ChannelFuture correspondenceChannel;
    private static void startClient() throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your username: ");
        if (scanner.hasNext()) username = scanner.nextLine();
        System.out.println("Welcome to Exploding Kittens, " + username + "!");
        System.out.println("Would you like to play offline or online? Answer options: offline/online");
        if (scanner.hasNext()) {
            if(scanner.nextLine().equals("offline")){
                System.out.println("How many players do you want in your game? Answer options: 2-5");
            }else{
                System.out.println("going online");
                ClientProgram.goOnline();
            }
        }
        if (scanner.hasNext()) sizeOfGame = Integer.parseInt(scanner.nextLine());
        System.out.println("How many computers do you want in your game? Minimum: 0 Maximum: " + (sizeOfGame - 1));
        if (scanner.hasNext()){
            numberOfComputers = Integer.parseInt(scanner.nextLine());
            Game game = new Game();
            game.start(sizeOfGame,numberOfComputers);
        }
    }
    /*
     * Configure the client.
     */

    // Since this is client, it doesn't need boss group. Create single group.
    private static void goOnline() throws Exception{
        final String HOST = "127.0.0.1";
        final int PORT = 8007;
        Scanner scanner = new Scanner(System.in);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group) // Set EventLoopGroup to handle all eventsf for client.
                    .channel(NioSocketChannel.class)// Use NIO to accept new connections.
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipe = ch.pipeline();
                            /*
                             * Socket/channel communication happens in byte streams. String decoder &
                             * encoder helps conversion between bytes & String.
                             */
                            pipe.addLast(new StringDecoder());
                            pipe.addLast(new StringEncoder());

                            // This is our custom client handler which will have logic for chat.
                            pipe.addLast(new ClientHandler());

                        }
                    });

            // Start the client.
            System.out.println("Attempting Connection");
            correspondenceChannel = bootstrap.connect(HOST, PORT).sync();
            System.out.println("Connection Successful");

            sendRequestToServer("USERNAME: " + username,correspondenceChannel);                 //TODO reaction on server side
            System.out.println("Would you like to join or create a game? Answer options: join/create");
            if (scanner.hasNext()) {
                if(scanner.nextLine().equals("join")){
                    sendRequestToServer("SHOWROOMS",correspondenceChannel);                         //TODO reaction on server side
                }else{
                    String roomDetails = "";   //0: RoomName, 1: RoomSize, 2: NumberOfComputers
                    System.out.println("What should the room be named?");
                    if (scanner.hasNext()) roomDetails.concat(scanner.nextLine() + " ");
                    System.out.println("How many players do you want in your game? Answer options: 2-5");
                    if (scanner.hasNext()) {
                        String roomSize = scanner.nextLine();
                        roomDetails.concat(roomSize + " ");
                        System.out.println("How many computers do you want in your game? Minimum: 0 Maximum: " + (Integer.parseInt(roomSize) - 1));
                    }
                    if (scanner.hasNext()) roomDetails.concat(scanner.nextLine());
                    System.out.println(roomDetails);
                    sendRequestToServer("CREATE " + roomDetails,correspondenceChannel);
                }
            }

            /*
             * Iterate & take chat message inputs from user & then send to server.
             */
            while (scanner.hasNext()) {
                String input = scanner.nextLine();
                Channel channel = correspondenceChannel.sync().channel();
                channel.writeAndFlush("[" + username + "]: " + input);
                channel.flush();
            }

            // Wait until the connection is closed.
            correspondenceChannel.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
    public static void sendRequestToServer(String message, ChannelFuture correspondenceChannel) throws Exception{
        Channel channel = correspondenceChannel.sync().channel();
        channel.writeAndFlush(message);
        channel.flush();
    }


    public static void main(String[] args) throws Exception {
        ClientProgram.startClient();
    }
}
