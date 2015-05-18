/**
 * JoinMenu
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
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

// This menu asks the user to input their name and the server information to join.
public class JoinMenu extends JComponent implements Runnable, MouseListener, MouseMotionListener {
    //Rectangles for navigation
    private static final Rectangle recBack= new Rectangle(25,480,316,95)
            ,recNext = new Rectangle(460, 480,316,95);
    
    //Texts for the server information and name
    private static final JTextField ipTf = new JTextField("192.168.0.15",20);
    private static final JTextField nameTf = new JTextField("",20);
    private static final JTextField portTf = new JTextField("7777",20);
    private static final JLabel ipLabel = new JLabel("Enter the IP Address:");
    private static final JLabel errorLabel = new JLabel("");
    private static final JLabel portLabel = new JLabel("Enter the port:");
    private static final JLabel nameLabel = new JLabel("Enter your name:");
    Font labelFont = new Font("Calibri", Font.PLAIN, 24);
    
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;
    
    public boolean isConnecting = false;
    
    // Adds the textfields , labels, listeners to the frame and loads the images
    JoinMenu()
    {
        //Load Images
        back = DrawThat.getImage("images/MainBack.png");
        button = DrawThat.getImage("images/MainmenuButton.png");
        arrow = DrawThat.getImage("images/arrow.png");
        
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
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void startThread() {
        Thread t = new Thread(this);
        t.start();
    }
    
    //Connects to the client and starts the game if the connection is sucessful.
    //otherwise the error is written to a log file.
    @Override
    public void run() {
        while(true) {
            try {
                this.repaint();
                this.revalidate();
                
                if(isConnecting) {
                    try {
                        DTClient client = new DTClient(ipTf.getText(), Integer.parseInt(portTf.getText()));
                        Game game = new Game(client, nameTf.getText());
                        DrawThat.setDisplay(game);
                    }
                    catch(Exception er) {
                        er.printStackTrace();
                        DrawThat.logMessage(er.getMessage());
                        errorLabel.setText("Error connecting to the server");
                    }
                    isConnecting = false;
                }
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            catch(Exception er) {
                er.printStackTrace();
                DrawThat.logMessage(er.getMessage());
                
            }
            finally {
                isConnecting = false;
            }
        }
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        try {
            g2d.drawImage(back, 0, 0, null);
            g2d.drawImage(button,25, 480, null);
            g2d.drawImage(button, 460, 480, null);
            
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
            
        }
        catch(Exception er) {
            System.out.println("ERROR");
            DrawThat.logMessage(er.getMessage());
        }
    }
    
    // Checks if the buttons are clicked. If the next button is clicked
    // there is input validation then a isConnecting variable is set to 
    // true for the run method.
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        
        if(recBack.contains(p)) {
            DrawThat.setDisplay(DrawThat.MAIN_MENU);
        }
        
        if(recNext.contains(p)) {
            if(ipTf.getText().equals("")  && portTf.getText().equals("")) {
                errorLabel.setText("Please Enter the IP Address and port");
            }
            else if(ipTf.getText().equals("")) {
                errorLabel.setText("Please Enter the IP Address");
            }
            else if(portTf.getText().equals("")) {
                errorLabel.setText("Please Enter the port");
            }
            else {
                //Connects in the run method so the application doesn't freeze.
                isConnecting = true;
                errorLabel.setText("Connecting...Please wait");
            }
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
    
    public void mouseMoved(MouseEvent e) {
        // Not used
    }
}
