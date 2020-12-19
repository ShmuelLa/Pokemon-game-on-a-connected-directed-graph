package gameClient.util;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class compAdapt implements ComponentListener {
    private Gframe frame;


    public compAdapt() {
        super();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        scale(frame.getWidth(),frame.getHeight(),800,600);
    }
    private void  scale(int width,int height,int x,int y){

        if (width > x) {
            float temp = (float) width / x;
            //frame.setReScaleX(temp);
            frame.initMain(temp,1);
        } else if (width < x) {
            float temp = (float) width / x;
           // frame.setReScaleX(1 / temp);
            frame.initMain(temp,1);
        } else frame.setReScaleX(1);

        if (height > y) {
            float temp = (float) height / y;
           // frame.setReScaleY(temp);
            frame.initMain(1,temp);
        } else if (height < y) {
            float temp = (float) y / height;
            //frame.setReScaleY(temp);
            frame.initMain(1,temp);
        } else frame.setReScaleY(1);
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