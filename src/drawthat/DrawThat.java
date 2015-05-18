/**
 * DrawThat
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
 */

package drawthat;

import static drawthat.Game.dateFormat;
import java.awt.Color;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JFrame;


// Creates the frame and switches the screens
public class DrawThat {
    private static final JFrame frame = new JFrame("Draw That!");
    public static final MainMenu MAIN_MENU = new MainMenu();
    public static final JoinMenu JOIN_MENU = new JoinMenu();
    public static final HostMenu HOST_MENU = new HostMenu();
    private static final Date date = new Date();
    
    // Sets the frame details and sets the first screen as mainMenu
    public DrawThat() {
        JOIN_MENU.startThread();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(805,628);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setContentPane(MAIN_MENU);
        frame.setBackground(Color.white);
        frame.setResizable(false);
        
    }
    
    public static void main(String[] args) {
        DrawThat frame = new DrawThat();
    }
    
    // Changes the current paint component that the jframe uses.
    public static void setDisplay(Container con) {
        frame.setContentPane(con);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Accepts the location of an image then returns the bufferedImage version
     * of it.
     * @param url : the location of the file
     * @return BufferedImage of the file from the location
     */
    public static BufferedImage getImage(String s) {
        try {
            URL url = DrawThat.class.getResource(s);
            BufferedImage in = ImageIO.read(url);
            
            return in;
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
            DrawThat.logMessage("File not found");
            return null;
        }
        catch(EOFException e) {
            e.printStackTrace();
            DrawThat.logMessage("End of file reached");
            return null;
        }
        catch(IOException e) {
            e.printStackTrace();
            DrawThat.logMessage(e.getMessage());
            return null;
        }
    }
    
    /**
     * Creates a folder called logs and for each instance of the program a new
     * text file is created with the date and every error message is put to a new
     * line.
     */
    public static void logMessage(String er) {
        try {
            File dir = new File("logs");
            if(!dir.exists()){
                dir.mkdir();
            }
            File f = new File("logs/log_"+dateFormat.format(date)+".txt");
            if(!f.exists()) f.createNewFile();
            
            FileWriter writer = new FileWriter(f, true);
        
            writer.write(er+ "\n");
            writer.flush();
            writer.close();
                        
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found");
        }
        catch(EOFException e) {
            System.out.println("End of file reached");
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
