import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class GPanel extends JPanel {

    private Image background;
    private Arena _ar;
    private Range2Range _w2f;
    private Image gary;
    private Image pikachu;
    private Image caterpie;
    private float reScaleX;
    private float reScaleY;
    private int state;

    public GPanel(){
        state = 0;
    }
    public GPanel(Arena ar, Range2Range s){
        state = 1;
        _ar = ar;
        _w2f = s;

    }
    public void update(Arena ar) {
        this._ar = ar;
        updateFrame();
    }
    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
    }
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if(state == 1) {
            drawBackground(g2d);
            drawGraph(g2d);
            drawPokemons(g2d);
            drawAgants(g2d);
            drawInfo(g2d);
        }else if ( state == 0) {
            mainmenu(g2d);
        }
    }
    private void mainmenu(Graphics2D g){
        try {
            URL uri = getClass().getResource("/start.png");
            background = new ImageIcon(uri).getImage();
        }catch (Exception i){
            i.printStackTrace();
        }
        g.drawImage(background,-70,0,this);
    }
    private void drawInfo(Graphics2D g) {
        List<String> str = _ar.get_info();
        String dt = "none";
        for(int i=0;i<str.size();i++) {
            g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
        }

    }
    private void drawGraph(Graphics2D g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n,5,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext()) {
                edge_data e = itr.next();
                drawEdge(e, g);
            }
        }
    }
    private void drawPokemons(Graphics2D g) {
        Graphics2D g2d = (Graphics2D)g;
        List<CL_Pokemon> fs = _ar.getPokemons();
        if(fs!=null) {
            Iterator<CL_Pokemon> itr = fs.iterator();
            while(itr.hasNext()) {
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r=15;
                if(f.getType()<0) {
                    drawCaterpie(f,g);
                }
                else if (c!=null) {
                    try{
                        URL uri = getClass().getResource("/pikachu.png");
                        pikachu = new ImageIcon(uri).getImage();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    geo_location fp = this._w2f.world2frame(c);
                    g.drawImage(pikachu, (int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r, this);
                    g2d.setColor(Color.black);
                    g2d.setFont(new Font("",Font.BOLD,12));
                    g2d.drawString(""+f.getValue(), (int)fp.x(), (int)fp.y()-r);
                }
            }
        }
    }
    private void drawCaterpie(CL_Pokemon pokemon,Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        try {
            URL uri2 = getClass().getResource("/cat.png");
            caterpie = new ImageIcon(uri2).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Point3D point = pokemon.getLocation();
        geo_location fp = this._w2f.world2frame(point);
        int r = 15;
        g.drawImage(caterpie, (int)fp.x()-r,(int)fp.y()-r,2*r, 2*r,this);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("",Font.BOLD,12));
        g2d.drawString(""+pokemon.getValue(), (int)fp.x(), (int)fp.y()-r);
    }
    private void drawAgants(Graphics2D g) {
        Graphics2D g2d = (Graphics2D)g;
        List<CL_Agent> rs = _ar.getAgents();
        try{
            URL uri = getClass().getResource("/gary.png");
            gary = new ImageIcon(uri).getImage();
        }catch (Exception e){
            e.printStackTrace();
        }
        int i=0;
        while(rs!=null && i<rs.size()) {
            geo_location c = rs.get(i).getLocation();
            CL_Agent temp = rs.get(i);
            int r=15;
            i++;
            if(c!=null) {

                geo_location fp = this._w2f.world2frame(c);
                g.drawImage(gary, (int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r, this);
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("",Font.BOLD,12));
                g2d.drawString(""+temp.getID(), (int)fp.x(), (int)fp.y()-(2*r));
            }
        }
    }
    private void drawNode(node_data n, int r, Graphics2D g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }
    private void drawEdge(edge_data e, Graphics2D g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.setColor(new Color(13, 13, 13));
        g.setStroke(new BasicStroke(2));
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }
    private void drawBackground(Graphics2D g){

        try {
            URL uri = getClass().getResource("/1.jpg");
            background = new ImageIcon(uri).getImage();
            g.drawImage(background,0,0,this);
        }catch (Exception i){
            i.printStackTrace();
        }
    }
    public void setReScaleX(float reScaleX) {
        this.reScaleX = reScaleX;
    }
    public void setReScaleY(float reScaleY) {
        this.reScaleY = reScaleY;
    }


//    public Image rescaleImage(Image im){
//        Image renderedImage = im.getScaledInstance(w, h, Image.SCALE_SMOOTH);
//        return renderedImage;
//    }
}
