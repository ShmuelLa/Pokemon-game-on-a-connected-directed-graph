package gameClient.util;

import api.*;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;
import gameClient.Ex2;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
/**
 * this class is our use of the JPanel class, we use a lot of Images , such as out Agents our
 * Pokemons, and our background. we use the Range2Range class to translate the position of each object
 * on the graph and we rescale it if the screen is being resized. each game state here has an integer to represent
 * what state it is ( 1 for playing 0 for main screen)
 * @author gidon.avziz & shmuel.lavian
 */
public class GPanel extends JPanel {
    private game_service game;
    private Gframe frame;
    private Image background;
    private Arena _ar;
    private Range2Range _w2f;
    private Image gary;
    private Image pikachu;
    private Image caterpie;
    private float rex =1;
    private float rey =1;
    private int state;

    /**
     * A simple constructor, this class needs to keep track and interact with the main frame, so we set in inside
     * the main constructors.
     * @param frame
     */
    public GPanel(Gframe frame) {
        state = 0;
        this.frame = frame;
    }

    /**
     * A second constructors. with twicks for our uses, it changes the status of the Frame
     * so we keep continuity of the program.
     * @param ar
     * @param s
     * @param frame
     */
    public GPanel(Arena ar, Range2Range s,Gframe frame) {
        state = 1;
        _ar = ar;
        _w2f = s;
        this.frame = frame;
        frame.setPressed(true);

    }

    /**
     *This method calls the updateFrame method setting the Arena.
     * @param ar
     */
    public void update(Arena ar) {
        this._ar = ar;
        updateFrame();
    }

    /**
     * This method sets up the arena and translates all of the components positions/
     */
    private void updateFrame() {
        Range rx = new Range( 20,this.getWidth()-50);
        Range ry = new Range(this.getHeight()-120,50);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g, frame);
    }

    /**
     * This method is an Override to JPanel method paint. calling mypaint .
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        mypaint(g2d);
    }

    /**
     * This method is what draws the graph with all of his components !
     * @param g2d
     */
    public void mypaint(Graphics2D g2d){
        if (state == 1) {
            drawBackground(g2d);
            drawGraph(g2d);
            drawPokemons(g2d);
            drawAgants(g2d);
        } else if (state == 0) {
            mainmenu(g2d);
        }
    }

    /**
     * This method is set to draw the background of the main screen, using the Graphics2D obj.
     * @param g
     */
    private void mainmenu(Graphics2D g) {
        try {
            URL uri = getClass().getResource("/start.png");
            background = new ImageIcon(uri).getImage();
        } catch (Exception i) {
            i.printStackTrace();
        }
        g.drawImage(background, 0, 0,this.getWidth(),this.getHeight(), this);
    }

    /**
     * This method is the brain that draws the actual graph , nodes and edges. calling then by their turn,
     * keeping the radius of each node that will be drawn to be 5, setting his color to blue.
     * While the game is resized we rescale the nodes position.
     * @param g
     */
    private void drawGraph(Graphics2D g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n, 5, g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while (itr.hasNext()) {
                edge_data e = itr.next();
                drawEdge(e, g);
            }
        }
    }

    /**
     * This method draws the pokemons. their radius is bigger (15) from the nodes, we draw their value above so
     * the we could double check our algorithm ( to find the biggest one that is the nearest).
     * We use an ImageIcon to load a .png file from the resources folder.
     * While the game is resized we rescale the pokemon position, and the edges length.
     * @param g
     */
    private void drawPokemons(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        List<CL_Pokemon> fs = _ar.getPokemons();
        if (fs != null) {
            Iterator<CL_Pokemon> itr = fs.iterator();
            while (itr.hasNext()) {
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r = 15;
                if (f.getType() < 0) {
                    drawCaterpie(f, g);
                } else if (c != null) {
                    try {
                        URL uri = getClass().getResource("/pikachu.png");
                        pikachu = new ImageIcon(uri).getImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    geo_location fp = this._w2f.world2frame(c);
                    g.drawImage(pikachu, (int) ((fp.x()-r)*(frame.getRex())), (int)((fp.y()-r)*(frame.getRey())), 2 * r, 2 * r, this);
                    g2d.setColor(Color.black);
                    g2d.setFont(new Font("", Font.BOLD, 12));
                    g2d.drawString("" + f.getValue(), (int) (fp.x()*(frame.getRex())), (int) (fp.y()*(frame.getRey())) -  r);
                }
            }
        }
    }

    /**
     * This function in only to draw the other type of pokemons, there are two types, this is the type that
     * the Agents need to eat from lower node to bigger, so we could check our algorithm (and for fun)
     * we used a different image here. While the game is resized we rescale the pokemon position.
     * @param pokemon
     * @param g
     */
    private void drawCaterpie(CL_Pokemon pokemon, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        try {
            URL uri2 = getClass().getResource("/cat.png");
            caterpie = new ImageIcon(uri2).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Point3D point = pokemon.getLocation();
        geo_location fp = this._w2f.world2frame(point);
        int r = 15;
        g.drawImage(caterpie, (int)Math.ceil(((fp.x()-r)*(frame.getRex()))), (int)Math.ceil((fp.y() - r)*(frame.getRey())), 2 * r, 2 * r, this);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("", Font.BOLD, 12));
        g2d.drawString("" + pokemon.getValue(), (int) (fp.x()*(frame.getRex())), (int) (fp.y()*(frame.getRey())) -r);
    }

    /**
     * This method is used to draw the Agants. we draw them the same way we draw the other pieces of the game.
     * Note : we use this method to paint and repaint the Timer while the game is running.
     * @param g
     */
    private void drawAgants(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        List<CL_Agent> rs = _ar.getAgents();
        try {
            URL uri = getClass().getResource("/gary.png");
            gary = new ImageIcon(uri).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        while (rs != null && i < rs.size()) {
            geo_location c = rs.get(i).getLocation();
            CL_Agent temp = rs.get(i);
            int r = 15;
            i++;
            if (c != null) {

                geo_location fp = this._w2f.world2frame(c);
                g.drawImage(gary, (int) ((fp.x() - r)*(frame.getRex())), (int) ((fp.y() - r)*(frame.getRey())), 2 * r, 2 * r, this);
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("", Font.BOLD, 12));
                g2d.drawString("" + temp.getID(), (int) (fp.x()*(frame.getRex())), (int) (fp.y()*(frame.getRey())) - (2 * r));
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial",Font.BOLD, 14));
                g.drawString("Time left : " +(Ex2.getime()/1000),((int)670*(frame.getRex())),((int)15*(frame.getRex())));
            }
        }
    }
    /** This method is use to draw the nodes in the graph ( used by the drawGraph func).
     * we rescale them when the game is being resized.
     */
    private void drawNode(node_data n, int r, Graphics2D g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int) ((fp.x() - r)*(frame.getRex())), (int) ((fp.y() - r)*(frame.getRey())), 2 * r, 2 * r);
        g.drawString("" + n.getKey(), (int) (fp.x()*(frame.getRex())), (int) (fp.y()*(frame.getRey())) - 4 * r);
    }
    /** This method is use to draw the edges in the graph ( used by the drawGraph func).
     * we rescale them when the game is being resized.
     */
    private void drawEdge(edge_data e, Graphics2D g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.setColor(new Color(13, 13, 13));
        g.setStroke(new BasicStroke(2));
        g.drawLine((int) (s0.x()*frame.getRex()) , (int)(s0.y()*frame.getRey()), (int) (d0.x()*frame.getRex()), (int) (d0.y()*frame.getRey()));
    }
    /** This method is used to draw the background of the second screen. the battle scene.
     */
    private void drawBackground(Graphics2D g) {

        try {
            URL uri = getClass().getResource("/battle.jpg");
            background = new ImageIcon(uri).getImage();
            g.drawImage(background, 0, 0,this.getWidth(),this.getHeight(), this);
        } catch (Exception i) {
            i.printStackTrace();
        }
    }


}

