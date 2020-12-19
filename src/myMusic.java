import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class myMusic implements Runnable{
    private int song;

    myMusic(int x){
        song = x;
    }
    @Override
    public void run() {
            if (this.song == 0) {
                try {
                    File file = new File("resources/song1.WAV");
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(file));
                    clip.start();
                    Thread.sleep(clip.getMicrosecondLength() / 1000);
                    clip.close();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (song == 1 ) {
                try {
                    File file = new File("resources/song2.WAV");
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(file));
                    clip.start();
                    Thread.sleep(clip.getMicrosecondLength() / 1000);
                    clip.close();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }
}
