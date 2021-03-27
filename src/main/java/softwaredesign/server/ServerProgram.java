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
import softwaredesign.client.ClientProgram;

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
            ChannelFuture f = null;
            try {
                f = b.bind(PORT).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Exploding Kittens Server started. Ready to accept players.");
            // Wait until the server socket is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        ServerProgram server = new ServerProgram();
        server.run();
    }
}
