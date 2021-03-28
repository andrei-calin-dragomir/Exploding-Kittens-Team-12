package softwaredesign.gui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Random;

public class Sounds {

    private static final AudioClip clickSound = new AudioClip("file:resources/sounds/click.wav");
    private static final AudioClip winSound = new AudioClip("file:resources/sounds/winSound.mp3");
    private static final AudioClip meow_1 = new AudioClip("file:resources/sounds/meow_1.mp3");
    private static final AudioClip meow_2 = new AudioClip("file:resources/sounds/meow_2.mp3");
    private static final AudioClip meow_3 = new AudioClip("file:resources/sounds/meow_3.mp3");
    private static final AudioClip playerExplosion_1 = new AudioClip("file:resources/sounds/playerExplosion_1.mp3");
    private static final AudioClip playerExplosion_2 = new AudioClip("file:resources/sounds/playerExplosion_2.mp3");
    private static final AudioClip playerExplosion_3 = new AudioClip("file:resources/sounds/playerExplosion_3.mp3");
    private static final Media roomMusicWaiting = new Media("file:resources/sounds/roomMusicWaiting.mp3");

    private static Random rand = new Random();

    public static void playClick() { clickSound.play();}
    public static void playWin() { winSound.play();}
    public static void playRoomMusicWaiting() {
        MediaPlayer music = new MediaPlayer(roomMusicWaiting);
        music.setCycleCount(3);
    }
    public static void playExplosionSound() {
        int soundToPlay = rand.nextInt(2);
        switch (soundToPlay){
            case 0: playerExplosion_1.play(); break;
            case 1: playerExplosion_2.play(); break;
            case 2: playerExplosion_3.play(); break;
        }

    }
    public static void playMeow() {
        int soundToPlay = rand.nextInt(2);
        switch (soundToPlay){
            case 0: meow_1.play(); break;
            case 1: meow_2.play(); break;
            case 2: meow_3.play(); break;
        }
    }
}
