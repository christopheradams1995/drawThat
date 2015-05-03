/**
* This is a reaction based game. When you click a shape the shape reactions which
* affect shapes around it. The point of the game is to react every shape and you
* gain points the better you do.
*
* @author  Christopher Adams
* @version 1.0
* @since   2014-11-23
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
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class JoinMenu extends JComponent implements Runnable,MouseListener, MouseMotionListener
{
    // used for the button events including the movement of the arrow
    Font labelFont = new Font("Calibri", Font.PLAIN, 24);
    private static Rectangle recBack= new Rectangle(25,480,316,95)
            ,recNext = new Rectangle(460, 480,316,95);
    private static JTextField ipTf = new JTextField("25.63.61.160",20);
    private static JTextField nameTf = new JTextField("",20);
    private static JTextField portTf = new JTextField("7777",20);
    private static JLabel ipLabel = new JLabel("Enter the IP Address:");
    private static JLabel errorLabel = new JLabel("");
    private static JLabel portLabel = new JLabel("Enter the port:");
    private static JLabel nameLabel = new JLabel("Enter your name:");
    private Point p;
    private String ip = "";
    private String port = "";
    
    
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;
    
    public boolean isConnecting = false;
    
    JoinMenu()
    {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        //Load Images
        back = getImage("images/MainBack.png");
        button = getImage("images/MainmenuButton.png");
        arrow = getImage("images/arrow.png");
        
        //Add text fields and stuff for information input
        ipTf.setBounds(375, 155, 200, 25);
        ipLabel.setBounds(155, 155, 285, 25);
        ipLabel.setFont(labelFont);
        errorLabel.setBounds(255, 55, 285, 25);
        portTf.setBounds(375, 205, 100, 25);
        nameTf.setBounds(375, 105, 100, 25);
        portLabel.setBounds(155, 205, 285, 25);
        portLabel.setFont(labelFont);
        nameLabel.setBounds(155, 105, 285, 25);
        nameLabel.setFont(labelFont);
        
        this.add(ipTf);
        this.add(ipLabel);
        this.add(portTf);
        this.add(nameTf);
        this.add(portLabel);
        this.add(nameLabel);
        this.add(errorLabel);
        
        Thread t = new Thread(this);
        t.start();
    }
    
    public void run()
    {

        //Connects to the client and starts the game if the connection is sucessful.
        // otherwise the error is written to a log file.
        while(true)
        {
            try
            {
                this.repaint();
                this.revalidate();
                
                if(isConnecting)
                {
                    try
                    {
                        DTClient client = new DTClient(ipTf.getText(), Integer.parseInt(portTf.getText()));
                        Game game = new Game(client, nameTf.getText());
                        DrawThat.setDisplay(game);
                    }
                    catch(Exception er)
                    {
                        er.printStackTrace();
                        Game.logMessage(er.getMessage());
                        errorLabel.setText("Error connecting to the server");
                    }
                    isConnecting = false;
                }
                Thread.sleep(1000);
            }
            catch(Exception er)
            {
                er.printStackTrace();
                Game.logMessage(er.getMessage());
            }
        }
    }
    
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        try
        {
            
            //Draw Images
            g2d.drawImage(back, 0, 0, null);
            //g2d.drawImage(button, 230, 210, null);
            g2d.drawImage(button,25, 480, null);
            g2d.drawImage(button, 460, 480, null);
            //g2d.drawImage(title, 150, 0, null);
            
            //Add Text for buttons
            Font font = new Font("Serif", Font.PLAIN, 55);
            g2d.setFont(font);
            g2d.setColor(Color.white);
            
            //anti alias
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            g2d.drawString("Back", 105, 540);
            g2d.drawString("Connect", 530, 540);
            
            this.repaint();
            this.revalidate();
            
        }catch(Exception er)
        {
            System.out.println("ERROR");
            Game.logMessage(er.getMessage());
        }
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
            Game.logMessage(er.getMessage());
            return null;
        }
        
    }
    

    public void mouseClicked(MouseEvent e) 
    {
        Point p = e.getPoint();
        
        if(recBack.contains(p))
        {
            DrawThat.setDisplay(DrawThat.mm);
        }
        
        
        if(recNext.contains(p))
        {
            if(ipTf.getText().equals("")  && portTf.getText().equals(""))
            {
                errorLabel.setText("Please Enter the IP Address and port");
            }
            else if(ipTf.getText().equals(""))
            {
                errorLabel.setText("Please Enter the IP Address");
            }
            else if(portTf.getText().equals(""))
            {
                errorLabel.setText("Please Enter the port");
            }
            else
            {
                //Connects in the run method so the application doesn't freeze.
                isConnecting = true;
                errorLabel.setText("Connecting...Please wait");
            }
        }
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

    public void mouseDragged(MouseEvent e) 
    {
        
    }
    
    //Adjusts the location of the arrow that appears after the user hovers over
    // each button.
    public void mouseMoved(MouseEvent e) 
    {
        Point p = e.getPoint();
        if(recNext.contains(p))
        {
            this.p = new Point((int)recNext.getX()-110, (int)recNext.getY()+10);
        }
        else if(recBack.contains(p))
        {
            this.p = new Point((int)recBack.getX()-110, (int)recBack.getY()+10);
        }
        
    }
}
