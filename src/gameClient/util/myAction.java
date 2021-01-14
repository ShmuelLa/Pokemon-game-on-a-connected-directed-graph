package gameClient.util;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 * this method is a KeyListener implantation. we use this to make the game easier you dont have to click the
 * Pokeball! you can insert the wanted level and press enter!
 * we use this method to listen to the keys that are being pressed while in the main screen, if the user inputs
 * a valid level and press enter the game will run.
 * @author gidon.avziz & shmuel.lavian
 */
public class myAction implements KeyListener {
    private String data;
    private Gframe frame;

    /**
     * this method has to have a Frame to work on, so we can pass the information back, there for
     * the builder gets the current frame.
     * @param gframe
     */
    public myAction(Gframe gframe){
        frame = gframe;
    }

    /**
     * this method uses a boolean condition on the KeyEven implemented by the KeyListener interface.
     * if the input is valid then a game status is changed accordingly and the String is passed as well.
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
       if ( e.getKeyCode() == KeyEvent.VK_ENTER ){
            if( check(frame.getJTextString())){
                frame.setLevel(frame.getJTextString());
                frame.setPressed(true);

            }else{
                JOptionPane.showMessageDialog(frame, "Invalid input");
                frame.setPressed(false);
                frame.getJText().setText("Enter a Level");
            }
        }

    }

    /**
     * this method isn't in use within out game.
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * this method is similar to the KeyPressed, but typed, to make sure the if the user ins typing Enter whith
     * the JTextField the and the input is valid then the game will start.
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if( e.getKeyChar() == '\n' && check(frame.getJTextString()) )
        {
            frame.setLevel(frame.getJTextString());
            frame.setPressed(true);
        }
    }

    /**
     * A boolean function to be used all around the class, this makes sure the input dose not contain letters,
     * and only contains integers.
     * @param string
     * @return
     */
    private boolean check(String string){
        if((string.matches("[0-9]+") ||
                string.matches("[0-9]-") || string.contains("-"))){
            return true;
        }
        return false;
    }

}
