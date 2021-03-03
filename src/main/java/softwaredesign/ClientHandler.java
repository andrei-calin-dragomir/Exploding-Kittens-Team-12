package softwaredesign;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Scanner;

public class ClientHandler extends SimpleChannelInboundHandler<String>{
    /*
     * Print chat message received from server.
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
        channelRespond(ctx,msg);
    }
    public void channelRespond(ChannelHandlerContext ctx, String msg) throws Exception {
        Scanner scanner = new Scanner(System.in);
        if(msg.equals("NOROOMS")) System.out.println("There are no rooms available, create one? Yes/No");
        if(scanner.hasNext()){
            if(scanner.nextLine().equals("Yes")){
                String roomDetails = "";   //0: RoomName, 1: RoomSize, 2: NumberOfComputers
                System.out.println("What should the room be named?");
                if (scanner.hasNext()) roomDetails.concat(scanner.nextLine() + ",");
                System.out.println("How many players do you want in your game? Answer options: 2-5");
                if (scanner.hasNext()) {
                    String roomSize = scanner.nextLine();
                    roomDetails.concat(roomSize + ",");
                    System.out.println("How many computers do you want in your game? Minimum: 0 Maximum: " + (Integer.parseInt(roomSize) - 1));
                }
                if (scanner.hasNext()) roomDetails.concat(scanner.nextLine());
                System.out.println(roomDetails);
                ClientProgram.sendRequestToServer("CREATE " + roomDetails,ClientProgram.correspondenceChannel);
            }
        }
    }
}
