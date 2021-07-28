package gameClient.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class myMusic implements Runnable{
    private int song;
    static  String file;
    Clip clip;
    AudioInputStream audioInputStream;


    public myMusic(int x){
        song = x;
        String file = "resources/song1.WAV";
        try {
            audioInputStream =AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }
         catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
            if (this.song == 0) {
                try {
//                    File file = new File("resources/song1.WAV");
//                    Clip clip = AudioSystem.getClip();
//                    clip.open(AudioSystem.getAudioInputStream(file));
//                    clip.start();
//                    Thread.sleep(clip.getMicrosecondLength() / 1000);
//                    clip.close();
//                } catch (UnsupportedAudioFileException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (LineUnavailableException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


                }catch (Exception e){
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
