package gameClient;

import java.awt.*;
import java.net.PortUnreachableException;
import javax.swing.*;

public class GUI extends JFrame {

    public GUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Gotta Catch 'Em All!  ϞϞ(๑⚈ ․̫ ⚈๑)∩  Ƶƶ(☄￣▵—▵￣)");
        this.setSize(500,400);
        this.setVisible(true);
        ImageIcon logo = new ImageIcon("GameContent/pokeball.png");
        this.setIconImage(logo.getImage());
        this.getContentPane().setBackground(Color.BLACK);
    }
}
