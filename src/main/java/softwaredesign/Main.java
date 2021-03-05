package softwaredesign;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Game game = new Game();
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many players would you like in your game?");
        int numPlayer = scanner.nextInt();
        game.start(numPlayer);

    }


}
