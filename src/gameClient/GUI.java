package gameClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.PortUnreachableException;
import java.net.URL;
import java.util.Scanner;
import javax.sound.sampled.*;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener {
    MenuItem _printshit;


    public GUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("ϞϞ(๑⚈ ․̫ ⚈๑)∩   Gotta Catch 'Em All!   Ƶƶ(☄￣▵—▵￣)");
        this.setSize(500,400);
        this.setVisible(true);
        ImageIcon logo = new ImageIcon("resources/pokeball.png");
        this.setIconImage(logo.getImage());
        addMenu();
        addPanel();
        playSound("resources/wild.WAV");
    }

    private void addMenu() {
        MenuBar menu_bar = new MenuBar();
        Menu menu = new Menu("Zibi");
        menu_bar.add(menu);
        _printshit = new MenuItem("Print \"Shit!\"");
        _printshit.addActionListener(this);
        menu.add(_printshit);
        this.setMenuBar(menu_bar);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == _printshit) {
            System.out.println("Shit!");
        }
    }

    void addPanel() {
        GUIPanel mypanel = new GUIPanel();
        this.add(mypanel);
        mypanel.setVisible(true);
        mypanel.setSize(50,200);
    }

    void playSound(String path) {
        try {
            File file = new File("resources/wild.WAV");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
            Thread.sleep(clip.getMicrosecondLength()/1000);
            clip.close();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
