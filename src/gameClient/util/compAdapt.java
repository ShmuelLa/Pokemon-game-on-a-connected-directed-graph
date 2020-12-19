package gameClient.util;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class compAdapt implements ComponentListener {
    private Gframe frame;
    private float rescaleFactorx;
    private float rescaleFactory;
    private int initx;
    private int inity;
    public compAdapt(Gframe frame) {
        super();
        this.frame = frame;
        rescaleFactorx = 1;
        rescaleFactory = 1;
        initx = frame.getWidth();
        inity = frame.getHeight();
    }
    @Override
    public void componentResized(ComponentEvent e) {
            scalef(frame);
    }
    private void  scalef(Gframe frame){
        int facx = frame.getWidth();
        int facy = frame.getHeight();

        if (facx > initx) {
            float temp = (float) facx / initx;
            frame.setReScaleX(temp);
            rescaleFactorx = temp;
            System.out.println(temp);
        } else if (facx < initx) {
            float temp = (float) initx / facx;
            frame.setReScaleX(1 / temp);
            rescaleFactorx = temp;
        } else frame.setReScaleX(1);

        if (facy > inity) {
            float temp = (float) facy / inity;
            frame.setReScaleY(temp);
            rescaleFactory = temp;
        } else if (facy < inity) {
            float temp = (float) inity / facy;
            frame.setReScaleY(1/temp);
            rescaleFactory =temp;
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
    public float getRescaleFactorx() {
        return rescaleFactorx;
    }
    public float getRescaleFactory() {
        return rescaleFactory;
    }
}
