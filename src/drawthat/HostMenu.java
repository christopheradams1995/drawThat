/**
 * HostMenu
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
import java.net.InetAddress;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

// This menu asks the user to input their name and server information to host.
public class HostMenu extends JComponent implements MouseListener, MouseMotionListener {
    //Rectangles for navigation
    private static final Rectangle recBack= new Rectangle(25,480,316,95)
            ,recNext = new Rectangle(460, 480,316,95);
    
    //Texts for the server information and name
    private static final JTextField nameTf = new JTextField("",20);
    private static final JTextField portTf = new JTextField("7777",20);
    public static final JLabel errorLabel = new JLabel("");
    private static final JLabel nameLabel = new JLabel("Enter your name:");
    private static final JLabel portLabel = new JLabel("Enter the port:");
    Font labelFont = new Font("Calibri", Font.PLAIN, 24);
    
    //Settings input
    static JCheckBox easyPack = new JCheckBox();
    static JCheckBox mediumPack = new JCheckBox();
    static JCheckBox hardPack = new JCheckBox();
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 2, 8, 4);  
    
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;        
    
    // Adds the textfields , labels, listeners to the frame and loads the images
    HostMenu() {
        //Load Images
        back = DrawThat.getImage("images/HostBack.png");
        button = DrawThat.getImage("images/MainmenuButton.png");
        arrow = DrawThat.getImage("images/arrow.png");
        
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
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        try {
            //Draw Images
            g2d.drawImage(back, 0, 0, null);
            g2d.drawImage(button,25, 480, null);
            g2d.drawImage(button, 460, 480, null);
            
            //Add Text for buttons
            Font font = new Font("Serif", Font.PLAIN, 55);
            g2d.setFont(font);
            g2d.setColor(Color.white);
            
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            g2d.drawString("Back", 115, 543);
            g2d.drawString("Host", 560, 540);
            
            this.repaint();
            this.revalidate();
            
        }
        catch(Exception er) {
            DrawThat.logMessage(er.getMessage());
            System.out.println(er.getMessage());
        }
    }
    
    // Checks if the buttons are clicked. If the next button is clicked
    // there is input validation then a new server , client and game is
    // created.
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        
        if(recBack.contains(p)) {
            DrawThat.setDisplay(DrawThat.MAIN_MENU);
        }
        
        if(recNext.contains(p)) {
            if(portTf.getText().equals("")) {
                errorLabel.setText("Please Enter the port");
            }
            else {
                try {
                    if(!easyPack.isSelected() && !mediumPack.isSelected() && !hardPack.isSelected()) {
                        errorLabel.setText("Please select atleast one word pack");
                    }
                    else {  
                        DTServer server = new DTServer(Integer.parseInt(portTf.getText()), easyPack.isSelected() , this.mediumPack.isSelected(), this.hardPack.isSelected(),slider.getValue());
                        server.startThread();
                        DTClient client = new DTClient(InetAddress.getLocalHost().getHostAddress(), Integer.parseInt(portTf.getText()));
                        Game game = new Game(client, nameTf.getText());
                        DrawThat.setDisplay(game);
                        errorLabel.setText("Server is running!");
                    }
                }
                catch(Exception er) {
                    er.printStackTrace();
                    DrawThat.logMessage(er.getMessage());
                }
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
