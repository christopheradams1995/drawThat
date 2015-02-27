
package drawthat;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class DrawThat extends JComponent implements Runnable, MouseListener
{
    public static JFrame frame = new JFrame("Draw That!");
    public static Point point = new Point(0,0);
    public static MainMenu mm = new MainMenu();
    public static JoinMenu jm = new JoinMenu();
    public static HostMenu hm = new HostMenu();

    
    //images to load
    BufferedImage back;
    
    public DrawThat()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(810,630);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setContentPane(mm);
        frame.setBackground(Color.white);
        
        //Load Images
        //back = getImage("images/space1_0.png");
        this.addMouseListener(this);
        
        
    }
    
    public static void main(String[] args) 
    {
        
        DrawThat frame = new DrawThat();// main frame and starts with the mainMenu

    }
    
    public void run()
    {
        
    }
    

    
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Draw Images
        g2d.drawImage(back,0,0, null);

        
        this.repaint();
        this.revalidate();
    }
    
    /**
     * Changes the current paint componenet that the jframe uses. When called
     * this method will change the current "frame" that is being displayed.
     */
    public static void setDisplay(Container con)
    {
        frame.setContentPane(con);
        frame.revalidate();
        frame.repaint();
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) 
    {

    }

    public void mouseReleased(MouseEvent e) 
    {
        
    }

    public void mouseEntered(MouseEvent e) 
    {
       
    }

    public void mouseExited(MouseEvent e) 
    {
        
    }
    
    /**
     * Accepts the location of an image then returns the bufferedImage version
     * of it.
     * @param url : the location of the file
     * @return BufferedImage of the file from the location
     */
    public BufferedImage getImage(String s)
    {
        try
        {
            //uses the classLoader to get the path where the classes are
            URL url = MainMenu.class.getResource(s);
            
            BufferedImage in = ImageIO.read(url);
            return in;
        }
        catch(Exception er)
        {
            er.printStackTrace();
            return null;
        }
        
    }
}
