package softwaredesign.gui;

import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Random;

public class Sounds {

    private static AudioClip clickSound;
    private static AudioClip chatReceived;
    private static AudioClip chatSent;
    private static AudioClip meow_1;
    private static AudioClip meow_2;
    private static AudioClip meow_3;
    private static AudioClip playerExplosion_1;
    private static AudioClip playerExplosion_2;
    private static AudioClip playerExplosion_3;
    private static AudioClip winSound;
    private static AudioClip drawnKitten;
    private static AudioClip drawnCard;
    private static AudioClip playCard;
    private static AudioClip roomMusicWaiting;

    static {
        try {
            drawnCard = new AudioClip(new File("resources/sounds/drawnCard.mp3").toURI().toURL().toExternalForm());
            playCard = new AudioClip(new File("resources/sounds/playCard.mp3").toURI().toURL().toExternalForm());
            chatReceived = new AudioClip(new File("resources/sounds/chatReceived.mp3").toURI().toURL().toExternalForm());
            chatSent = new AudioClip((new File("resources/sounds/chatSent.mp3").toURI().toURL().toExternalForm()));
            drawnKitten = new AudioClip(new File("resources/sounds/ticking.mp3").toURI().toURL().toExternalForm());
            clickSound = new AudioClip(new File("resources/sounds/click.mp3").toURI().toURL().toExternalForm());
            meow_1 = new AudioClip(new File("resources/sounds/meow_1.mp3").toURI().toURL().toExternalForm());
            meow_2 = new AudioClip(new File("resources/sounds/meow_2.mp3").toURI().toURL().toExternalForm());
            meow_3 = new AudioClip(new File("resources/sounds/meow_3.mp3").toURI().toURL().toExternalForm());
            playerExplosion_1 = new AudioClip(new File("resources/sounds/playerExplosion_1.mp3").toURI().toURL().toExternalForm());
            playerExplosion_2 = new AudioClip(new File("resources/sounds/playerExplosion_2.mp3").toURI().toURL().toExternalForm());
            playerExplosion_3 = new AudioClip(new File("resources/sounds/playerExplosion_3.mp3").toURI().toURL().toExternalForm());
            winSound = new AudioClip(new File("resources/sounds/winSound.mp3").toURI().toURL().toExternalForm());
            roomMusicWaiting = new AudioClip(new File("resources/sounds/roomMusicWaiting.mp3").toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static Random rand = new Random();

    public static void playClick() { clickSound.play(0.2);}
    public static void playWin() { winSound.play(0.3);}
    public static void playRoomMusicWaiting() {
        roomMusicWaiting.play(0.2);
        roomMusicWaiting.setCycleCount(AudioClip.INDEFINITE);
    }
    public static void playPlayCard(){ playCard.play(0.2); }
    public static void drawnCard(){ drawnCard.play(0.2); }
    public static void playExplodingKittenDrawn(){ drawnKitten.play(0.1); }
    public static void playExplosionSound() {
        int soundToPlay = rand.nextInt(2);
        switch (soundToPlay){
            case 0: playerExplosion_1.play(0.2); break;
            case 1: playerExplosion_2.play(0.2); break;
            case 2: playerExplosion_3.play(0.2); break;
        }
    }
    public static void playChatSound(Boolean state){
        if(state) chatReceived.play(0.2);
        else chatSent.play(0.2);
    }
    public static void playMeow() {
        int soundToPlay = rand.nextInt(2);
        switch (soundToPlay){
            case 0: meow_1.play(0.2); break;
            case 1: meow_2.play(0.2); break;
            case 2: meow_3.play(0.2); break;
        }
    }
    public static void stopSound() {
        if(drawnKitten.isPlaying()) drawnKitten.stop();
        else if(roomMusicWaiting.isPlaying()) roomMusicWaiting.stop();
        else if(winSound.isPlaying()) winSound.stop();
    }
}
