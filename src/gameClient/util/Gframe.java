package gameClient.util;

import api.directed_weighted_graph;
import api.game_service;
import gameClient.Arena;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;


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
    private float rex;
    private float rey;
    private compAdapt adapt;


    /**
     * basic builder from custom JFrame, calling super() method to set defaults.
     */
    public Gframe(){
        super();

    }

    /**
     * this method updates this arena with a given arena, stating the whole update chain.
     * @param ar
     */
    public void updategame(Arena ar) {
        this._ar = ar;
        updateFrame();
    }

    /**
     * this method updates the main frame, setting the main panel.
     * in the this GPanel (that extends JPanel) will be painted the graph and all of his components.
     */
    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-50);
        Range ry = new Range(this.getHeight()-120,50);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
        GPanel main = new GPanel(_ar,_w2f,this);
        main.setSize(800,600);
        setLayout(new BorderLayout());
        add(main);
        this.addComponentListener(adapt);
    }

    /**
     * this method initialize the main screen setting all of his components and paint the background.
     * using JButton and JTextField to be implemented in our game.
     */
    public void initMain(){
        main = new GPanel(this);
        main.setSize(800,600);
        setLayout(new BorderLayout(0,0));
        actionLis = new myAction(this);
        text = new JTextField(10);
        button = new JButton();
        Color c = new Color(248,232,248);
        setBackground(c);
        button.setBackground(c);
        System.out.println(rex);
        button.setBounds(370, 497, 60, 60);
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
        text.setText("Enter a Level");
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        text.setBounds(50, 50, 100, 25);
        text.addMouseListener(this);
        text.addKeyListener(actionLis);

        button.addMouseListener(this);
        button.addKeyListener(actionLis);

        add(button);
        add(text);
        add(main);
    }

    /**
     * this class implements MouseListener, this is the implantation of it.
     * @param e
     */
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
                JOptionPane.showMessageDialog(null, "Invalid input");
                text.setText("Enter a Level");
            }
        }
        else if(!check(text.getText()) && !text.getText().equals("")){
            text.setText("Enter a Level");
        }
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

        if((string.matches("[0-9]+") ||
                string.matches("[0-9]-") || string.contains("-"))){
            return true;
        }
        return false;

    }


    public float getRex() {
        return rex;
    }

    public float getRey() {
        return rey;
    }

    public void setRex(float rex) {
        this.rex = rex;
    }

    public void setRey(float rey) {
        this.rey = rey;
    }
    public void moved(){
        button.setBounds((int)(370*rex), (int)(497*rey), 60, 60);
    }
}

