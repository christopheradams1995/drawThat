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
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class HostMenu extends JComponent implements MouseListener, MouseMotionListener
{
    //navigation
    private static Rectangle recBack= new Rectangle(25,480,316,95)
            ,recNext = new Rectangle(460, 480,316,95);
    
    //connecting inputs
    Font labelFont = new Font("Calibri", Font.PLAIN, 24);
    private static JTextField nameTf = new JTextField("",20);
    private static JTextField portTf = new JTextField("7777",20);
    private static JLabel errorLabel = new JLabel("");
    private static JLabel nameLabel = new JLabel("Enter your name:");
    private static JLabel portLabel = new JLabel("Enter the port:");
    
    //settings
    static JCheckBox easyPack = new JCheckBox();
    static JCheckBox mediumPack = new JCheckBox();
    static JCheckBox hardPack = new JCheckBox();
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 2, 8, 4);  
    
    
    private Point p;
    private String ip = "";
    private String port = "";
    static MainMenu mm = new MainMenu();
    
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;        
    
    HostMenu()
    {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        //Load Images
        back = getImage("images/HostBack.png");
        button = getImage("images/MainmenuButton.png");
        arrow = getImage("images/arrow.png");
        
        //Add text fields and stuff for information input
        
        errorLabel.setBounds(155, 105, 285, 25);
        nameTf.setBounds(255, 135, 100, 25);
        portTf.setBounds(255, 175, 50, 25);
        nameLabel.setBounds(55, 135, 285, 25);
        nameLabel.setFont(labelFont);
        portLabel.setBounds(55, 175, 285, 25);
        portLabel.setFont(labelFont);
        easyPack.setBounds(625,188,18,18);
        easyPack.setOpaque(false);
        mediumPack.setBounds(625,220,18,18);
        mediumPack.setOpaque(false);
        hardPack.setBounds(626,252,18,18);
        hardPack.setOpaque(false);
        
        slider.setBounds(619,301,100,60);
        slider.setOpaque(false);
        slider.setPaintLabels(true);  
        slider.setPaintTicks(true);  
        slider.setMinorTickSpacing(2);  
        slider.setMajorTickSpacing(1);  
        
        this.add(slider);
        this.add(easyPack);
        this.add(mediumPack);
        this.add(hardPack);
        this.add(nameTf);
        this.add(portTf);
        this.add(nameLabel);
        this.add(portLabel);
        this.add(errorLabel);
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
            
            g2d.drawString("Back", 115, 543);
            g2d.drawString("Host", 560, 540);
            
            this.repaint();
            this.revalidate();
            
        }catch(Exception er){System.out.println("ERROR");}
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
    

    public void mouseClicked(MouseEvent e) 
    {
        Point p = e.getPoint();
        
        if(recBack.contains(p))
        {
            
            DrawThat.setDisplay(mm);
        }
        
        if(recNext.contains(p))
        {
            if(portTf.getText().equals(""))
            {
                errorLabel.setText("Please Enter the port");
            }
            else
            {
                try
                {
                    if(!easyPack.isSelected() && !mediumPack.isSelected() && !hardPack.isSelected())
                    {
                        errorLabel.setText("Please select atleast one word pack");
                    }
                    else
                    {
                            
                        DTServer server = new DTServer(Integer.parseInt(portTf.getText()), easyPack.isSelected() , this.mediumPack.isSelected(), this.hardPack.isSelected(),slider.getValue());
                        DTClient client = new DTClient(InetAddress.getLocalHost().getHostAddress(), Integer.parseInt(portTf.getText()));
                        Game game = new Game(client, nameTf.getText());
                        DrawThat.setDisplay(game);
                        errorLabel.setText("Server is running!");
                    }
                }
                catch(Exception er)
                {
                    er.printStackTrace();
                }
                //DrawThat.setDisplay("mainMenu");
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
