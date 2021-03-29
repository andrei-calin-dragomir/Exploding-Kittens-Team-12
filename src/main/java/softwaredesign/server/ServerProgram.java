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

public final class ServerProgram extends Thread {

    // Port where chat server will listen for connections.
    static final int PORT = 8007;

    public void run(){

        /*
         * Configure the server.
         */

        // Create boss & worker groups. Boss accepts connections from client. Worker
        // handles further communication through connections.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup) // Set boss & worker groups
                    .channel(NioServerSocketChannel.class)// Use NIO to accept new connections.
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("framer", new LengthFieldBasedFrameDecoder(Short.MAX_VALUE,0,2,0,2));
                            p.addLast("framer-prepender", new LengthFieldPrepender(2, false));
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());

                            // This is our custom server handler which will have the legit for game commands
                            p.addLast(new ServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = null;
            try { f = b.bind(new InetSocketAddress("0.0.0.0", PORT)).sync(); }
            catch (InterruptedException ignore) {}
            System.out.println("Exploding Kittens Server started. Ready to accept players.");
            try { f.channel().closeFuture().sync(); }
            catch (InterruptedException ignore) {}
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // Used to hyperthread the server to play locally.
    public static void main(String[] args){
        ServerProgram server = new ServerProgram();
        server.run();
    }
}
