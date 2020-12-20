package gameClient.util;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
/**
 * This class is the brain of the hole resize and rescale functionality of the game.
 * We implement the ComponentListener Interface, we set the default size of the screen as 800x600,
 * if the screen is resized, this class listens to it and calculates byt how much.
 * @author gidon.avziz & shmuel.lavian
 */
public class compAdapt implements ComponentListener {
    private Gframe frame;
    private final int width = 800;
    private final int height = 600;

    /**
     * a simple constructor.
     */
    public compAdapt() {
        super();
    }
    /**
     * This method is the one we use to calculate how much the screen has grown or how much it shrunk.
     * we use if statements to check if the current frame size is bigger then the initial frame size,
     * splitting the checks to the width ( the x axis) and the height ( the y axis), if it has changed,
     * we set the the value of the change to be multiplied by all of the components that make the frame.
     * After that we call repaint to set the frame right.
     * @param e
     */
    @Override
    public void componentResized(ComponentEvent e) {
        if( frame.getWidth() > width){
            float num = (float) frame.getWidth()/width;
            frame.setRex(num);
            frame.repaint();
            if( ! frame.getPressed()){
                frame.moved();
            }
        }else if ( frame.getWidth() < width){
            float num = (float) frame.getWidth()/width;
            frame.setRex(num);
            frame.repaint();
            if( ! frame.getPressed()){
                frame.moved();
            }
        }else frame.setRex(1);

        if ( frame.getHeight() > height){
            float num = (float) frame.getHeight() / height;
            frame.setRey(num);
            frame.repaint();
            if( ! frame.getPressed()){
                frame.moved();
            }
        }else if ( frame.getHeight() < height){
            float num = (float) frame.getHeight() /height;
            frame.setRey(num);
            frame.repaint();
            if( ! frame.getPressed()){
                frame.moved();
            }
        }else frame.setRey(1);


    }
    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    /**
     * This method is used in other classes to set the Frame to be calculated here.
     * @param frame
     */
    public void setFrame(Gframe frame) {
        this.frame = frame;
    }

}