import api.directed_weighted_graph;
import gameClient.Arena;
import gameClient.util.Range;
import gameClient.util.Range2D;

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
    private compAdapt compAdapt;
    private String level;
    private boolean pressed = false;
    private JButton button;
    private JTextField text;
    private myAction actionLis;
    private GPanel main;
    private BufferedImage ball;
    private float reScaleX;
    private float reScaleY;



    Gframe(){
        super();
    }
    public void updategame(Arena ar) {
        this._ar = ar;
        updateFrame();
    }
    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-50);
        Range ry = new Range(this.getHeight()-120,50);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
        GPanel game_panel = new GPanel(_ar,_w2f);
        compAdapt frl_com = new compAdapt(this);
        game_panel.setSize(800,600);
        setLayout(null);
        add(game_panel);

        addComponentListener(frl_com);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public void initMain(){
        main = new GPanel();
        main.setSize(800,600);
        setLayout(null);
        actionLis = new myAction(this);
        text = new JTextField(10);
        button = new JButton();
        compAdapt = new compAdapt(this);

        Color c = new Color(248,232,248);
        setBackground(c);
        button.setBackground(c);
        button.setBounds(352, 450, 60, 60);
        try{
            File file = new File("resources/pokeball.jpg");
            ball = ImageIO.read(file);
            Image im = ball.getScaledInstance(button.getWidth(),button.getHeight(),Image.SCALE_SMOOTH);
            ImageIcon ans = new ImageIcon(im);
            button.setIcon(ans);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Enter a Level
        text.setText("11");
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        text.setBounds(50, 50, 100, 25);
        text.addMouseListener(this);
        text.addKeyListener(actionLis);

        button.addMouseListener(this);
        button.addKeyListener(actionLis);


        addComponentListener(compAdapt);



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
    public JButton getButton() {
        return button;
    }
    private boolean check(String string){
        if(string.matches("[0-9]+") &&string.length() < 3 &&
                Integer.parseInt(string) < 24 ){
            return true;
        }
        return false;
    }
    public double getReScaleX() {
        return reScaleX;
    }
    public void setReScaleX(float reScaleX) {
        this.reScaleX = reScaleX;
    }
    public double getReScaleY() {
        return reScaleY;
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
}

