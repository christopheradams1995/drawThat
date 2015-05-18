/**
 * MainMenu
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
 */

package drawthat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

//This is the home screen where the user selects where to go
public class MainMenu extends JComponent implements MouseListener, MouseMotionListener {
    // Used for the button events including the movement of the arrow
    private static final Rectangle recJoin= new Rectangle(230,210,316,95)
            ,recHost = new Rectangle(230,330,316,95),
            recInstructions = new Rectangle(230,450,316,95);
    private Point p;
    
    //Images to be drawn
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;        
    
    MainMenu() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        //Load Images
        back = DrawThat.getImage("images/MainBack.png");
        button = DrawThat.getImage("images/MainmenuButton.png");
        title = DrawThat.getImage("images/MainmenuTitle.png");
        arrow = DrawThat.getImage("images/arrow.png");
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        try {
            //Draw Images
            g2d.drawImage(back, 0, 0, null);
            g2d.drawImage(button, 230, 210, null);
            g2d.drawImage(button, 230, 330, null);
            g2d.drawImage(button, 230, 450, null);
            g2d.drawImage(title, 150, 0, null);
            
            if(p != null) {
                g2d.drawImage(arrow, p.x, p.y, null);
            }
            
            //Adds Text for buttons
            Font font = new Font("Serif", Font.PLAIN, 55);
            g2d.setFont(font);
            g2d.setColor(Color.white);
            
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            g2d.drawString("Join", 340, 270);
            g2d.drawString("Host", 330, 390);
            g2d.drawString("Instructions", 250, 510);
            
            this.repaint();
            this.revalidate();
            
        }
        catch(Exception er) {
            er.printStackTrace();
            DrawThat.logMessage(er.getMessage());
        }
    }

    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        
        if(recJoin.contains(p)) {
            DrawThat.setDisplay(DrawThat.JOIN_MENU);
            
        }
        else if(recHost.contains(p)) {
            DrawThat.setDisplay(DrawThat.HOST_MENU);
        }
    }

    public void mousePressed(MouseEvent e) {
        // Not used
    }

    public void mouseReleased(MouseEvent e) {
        // Not used
    }

    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    public void mouseExited(MouseEvent e) {
        // Not used
    }

    public void mouseDragged(MouseEvent e) {
        // Not used
    }
    
    //Adjusts the location of the arrow that appears after the user hovers over
    // each button.
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        if(recJoin.contains(p)) {
            this.p = new Point((int)recJoin.getX()-110, (int)recJoin.getY()+10);
        }
        else if(recHost.contains(p)) {
            this.p = new Point((int)recHost.getX()-110, (int)recHost.getY()+10);
        }
        else if(recInstructions.contains(p)) {
            this.p = new Point((int)recInstructions.getX()-110, (int)recInstructions.getY()+10);
        }
        else {
            this.p = null;
        }
    }
}
