package softwaredesign.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public final class ServerProgram {

    // Port where chat server will listen for connections.
    static final int PORT = 9009;
    static final String ADDRESS = "77.251.240.28";

    public static void main(String[] args) throws Exception {

        /*
         * Configure the server.
         */

        // Create boss & worker groups. Boss accepts connections from client. Worker
        // handles further communication through connections.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
//            b.localAddress(new InetSocketAddress(ADDRESS, PORT));
            b.group(bossGroup, workerGroup) // Set boss & worker groups
                    .channel(NioServerSocketChannel.class)// Use NIO to accept new connections.
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            /*
                             * Socket/channel communication happens in byte streams. String decoder &
                             * encoder helps conversion between bytes & String.
                             */
                            p.addLast("framer", new LengthFieldBasedFrameDecoder(Short.MAX_VALUE,0,2,0,2));
                            p.addLast("framer-prepender", new LengthFieldPrepender(2, false));
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());

                            // This is our custom server handler which will have logic for chat.
                            p.addLast(new ServerHandler());
                        }
                    });

            // Start the server.
            InetSocketAddress a = new InetSocketAddress("0.0.0.0", 8007);
            System.out.println(a);
            ChannelFuture f = b.bind(a).sync();
            System.out.println("Exploding Kittens Server started. Ready to accept players.");
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
