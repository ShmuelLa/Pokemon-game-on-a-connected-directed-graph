package gameClient.util;

import api.directed_weighted_graph;
import gameClient.Arena;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 *
 */
public class Gframe extends JFrame implements MouseListener{
    private Arena _ar;
    private gameClient.util.Range2Range _w2f;
    private String level;
    private boolean pressed = false;
    private JButton button;
    private JTextField text;
    private myAction actionLis;
    private GPanel main;
    private BufferedImage ball;
    private float reScaleX = 1;
    private float reScaleY = 1;



    public Gframe(){
        super();
    }
    public void updategame(Arena ar) {
        this._ar = ar;
        updateFrame();
    }
    private void updateFrame() {
        int rx_x = 20;
        int rx_y = this.getWidth()-50;
        int ry_x = this.getHeight()-120;
        int ry_y = 50;
        System.out.println(reScaleX);
        Range rx = new Range((int)(rx_x*reScaleX),(int)(rx_y*reScaleY));
        Range ry = new Range((int)(reScaleX*ry_x),(int)(reScaleY*ry_y));

        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
        GPanel main = new GPanel(_ar,_w2f);
        main.setSize(800,600);
        setLayout(new BorderLayout());
        add(main);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public void initMain(float reScaleX,float reScaleY){
        main = new GPanel();
        main.setSize((int)(800*reScaleX),(int)(600*reScaleY));
        setLayout(null);
        actionLis = new myAction(this);
        text = new JTextField(10);
        button = new JButton();



        Color c = new Color(248,232,248);
        setBackground(c);
        button.setBackground(c);

        button.setBounds((int)(370*reScaleX), (int)(495*reScaleY), 60, 60);
        try{
            File file = new File("resources/pokeball1.png");
            ball = ImageIO.read(file);
            Image im = ball.getScaledInstance(button.getWidth(),button.getHeight(),Image.SCALE_SMOOTH);
            ImageIcon ans = new ImageIcon(im);
            button.setBorder(null);
            button.setIcon(ans);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Enter a Level
        text.setText("11");
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        text.setBounds((int)(50*reScaleX), (int)(50*reScaleY), 100, 25);
        text.addMouseListener(this);
        text.addKeyListener(actionLis);

        button.addMouseListener(this);
        button.addKeyListener(actionLis);


        add(button);
        add(text);
        add(main);


    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if( e.getSource() == text){
            text.setText("");
        }
        if(e.getSource() == button){
            if( check(getJTextString())) {
                pressed = true;
                level = getJTextString();
            }else{
                JOptionPane.showMessageDialog(null, "Please choose a " +
                        "a level from 0 - 23");
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        if( e.getSource() == text){
            text.setText("");
        } if(e.getSource() == button){
            if( check(getJTextString())) {
                pressed = true;
                level = getJTextString();
            }else{
                JOptionPane.showMessageDialog(null, "Please choose a " +
                        "a level from 0 - 23");
            }
        }else if(!check(text.getText())){
            text.setText("Enter a Level");
        }
    }
    public boolean getPressed(){
        return pressed;
    }
    public JTextField getJText() {
        return text;
    }
    public void setJText(String t) {
        text.setText(t);
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getJTextString() {
        return text.getText();
    }
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
    private boolean check(String string){
        if(string.matches("[0-9]+") &&string.length() < 3 &&
                Integer.parseInt(string) < 24 ){
            return true;
        }
        return false;
    }
    public void setReScaleX(float reScaleX) {
        this.reScaleX = reScaleX;
    }
    public void setReScaleY(float reScaleY) {
        this.reScaleY = reScaleY;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
    public GPanel getMain() {
        return main;
    }
}

