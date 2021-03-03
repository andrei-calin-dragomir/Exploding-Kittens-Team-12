package softwaredesign;


//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Hashtable;
import java.util.List;

import org.javatuples.Pair;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
    // List of connected client channels with format => String: Username String: inWhichRoom Channel: connection credentials
    static Hashtable<ChannelHandlerContext,Pair> connectionLog = new Hashtable<>();
    static Hashtable<String,ServerHeldGame> roomsOnline = new Hashtable<>();

    /*
     * Whenever client connects to server through channel, add his channel to the
     * list of channels.
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx);
        connectionLog.put(ctx,Pair.with("Unnamed","NoRoom"));
    }

    /*
     * When a message is received from client, send that message to all channels.
     * FOr the sake of simplicity, currently we will send received chat message to
     * all clients instead of one specific client. This code has scope to improve to
     * send message to specific client as per senders choice.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server received - " + msg);
        channelRespond(ctx,msg);
    }
     public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
         String[] message = msg.split("\\s+");
         if(message[0].equals("USERNAME:")) connectionLog.replace(ctx,Pair.with(message[1],"NoRoom"));
         else if(message[0].equals("SHOWROOMS")){
             if(roomsOnline.isEmpty()) ctx.writeAndFlush("NOROOMS");
             else ctx.writeAndFlush("Rooms online: " + roomsOnline.toString());
         }else if(message[0].equals("CREATE")){     // 1: RoomName, 2: RoomSize, 3: NumberOfComputers
             Pair oldLogEntry = connectionLog.get(ctx);
             connectionLog.replace(ctx,Pair.with(oldLogEntry.getValue0(),message[1]));
             roomsOnline.put(message[1],new ServerHeldGame());
             roomsOnline.get(message[1]).start(Integer.parseInt(message[2]),Integer.parseInt(message[3]));
         }
     }

    /*
     * In case of exception, close channel. One may chose to custom handle exception
     * & have alternative logical flows.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Closing connection for client - " + ctx);
        ctx.close();
    }
}
