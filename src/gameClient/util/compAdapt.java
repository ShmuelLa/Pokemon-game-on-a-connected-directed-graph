package gameClient.util;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class compAdapt implements ComponentListener {
    private Gframe frame;
    private final int width = 800;
    private final int height = 600;


    public compAdapt() {
        super();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if( frame.getWidth() > width){
            float num = (float) frame.getWidth()/width;
            frame.setRex(num);
            frame.repaint();
        }else if ( frame.getWidth() < width){
            float num = (float) frame.getWidth()/width;
            frame.setRex(num);
            frame.repaint();
        }else frame.setRex(1);

        if ( frame.getHeight() > height){
            float num = (float) frame.getHeight() / height;
            frame.setRey(num);
            frame.repaint();

        }else if ( frame.getHeight() < height){
            float num = (float) frame.getHeight() /height;
            frame.setRey(num);
            frame.repaint();
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
    public void setFrame(Gframe frame) {
        this.frame = frame;
    }

}