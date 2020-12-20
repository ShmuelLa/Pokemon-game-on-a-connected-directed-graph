package gameClient.util;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class myAction implements KeyListener {
    private String data;
    private Gframe frame;

    myAction(Gframe gframe){
        frame = gframe;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() != KeyEvent.VK_ENTER &&  e.getKeyCode() != KeyEvent.VK_ESCAPE){

        }else if ( e.getKeyCode() == KeyEvent.VK_ENTER ){
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

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if( e.getKeyChar() == '\n' && check(frame.getJTextString()) )
        {
            frame.setLevel(frame.getJTextString());
            frame.setPressed(true);
        }
    }
    private boolean check(String string){
        if((string.matches("[0-9]+") ||
                string.matches("[0-9]-") || string.contains("-"))){
            return true;
        }
        return false;
    }

}
