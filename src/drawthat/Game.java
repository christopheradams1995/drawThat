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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class Game extends JComponent implements Runnable, MouseListener, MouseMotionListener, KeyListener
{
    // used for the button events including the movement of the arrow
    private static Rectangle chatSend= new Rectangle(754,415,35,33);
    private static Rectangle drawingArea = new Rectangle(0, 109, 573,341);
    private static Rectangle redArea = new Rectangle(10, 484, 64,44);
    private static Rectangle yellowArea = new Rectangle(74, 473, 67,40);
    private static Rectangle orangeArea = new Rectangle(146, 464, 66,42);
    private static Rectangle greenArea = new Rectangle(218, 467, 67,47);
    private static Rectangle blueArea = new Rectangle(291, 473, 69,47);
    private static Rectangle pinkArea = new Rectangle(369, 491, 66,47);
    private static Rectangle blackArea = new Rectangle(452, 517, 64,47);
    private static Rectangle whiteArea = new Rectangle(15, 547, 45,43);
    private static Rectangle undoArea = new Rectangle(19, 65, 68,36);
    private static Rectangle restartArea = new Rectangle(489, 68, 60,32);
    public static JTextArea chatTA = new JTextArea("",20,20);
    public DefaultCaret caret;
    private JScrollPane scroll;
    private static JTextField chatTF = new JTextField();
    private Point p;
    private String ip = "";
    private String port = "";
    
    //Gets the date for the log file
    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    static Date date = new Date();

    private static DTClient client;
    boolean sendmessage = false;
    
    Stroke stroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0,
    new float[] { 3, 1 }, 0);

    public static Point points[] = new Point[20000];
    public static ArrayList pointsToSend = new ArrayList();
    static int pointSize = 0;
    public static boolean isReleased = false;
    public static boolean isPressed = false;
    private static Point[] endPoints = new Point[10000];
    private static Color[] colorPoints = new Color[10000];
    static int endPointsSize = 0;
    private String color = "black";
    
    static String chat = "";
    
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;        
    
    Game(DTClient client, String name)
    {
        this.client = client;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);
        this.requestFocus();
        
        
        //sets the default player's name to their IP address
        try
        {
            if(name.equals("") || name.equals(" "))
            {
                name = InetAddress.getLocalHost().getHostAddress();
                System.out.println("game constructor name: " + name);
            }
        }
        catch(Exception er){er.printStackTrace();}
        
        client.sendMessage("[OP_newPlayer]"+name);

        //Load Images
        back = getImage("images/gameBack.png");
        //button = getImage("images/MainmenuButton.png");
        //arrow = getImage("images/arrow.png");
        
        //Add text fields and stuff for information input
        chatTF.setBounds(591, 418, 164, 30);
        //chatTF.
        
        //Set up scrollable text area
        chatTA.setBounds(589, 20, 195, 425);
        chatTA.setEditable(false);
        caret = (DefaultCaret) chatTA.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        scroll = new JScrollPane(chatTA);
        scroll.setBounds(589, 20, 196, 398);
        scroll.setWheelScrollingEnabled(true);
        scroll.setViewportView(chatTA);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        refreshChat();
        this.add(scroll);
        this.add(chatTF);
        chatTF.addKeyListener(this);
        

        
        Thread t = new Thread(this);
        t.start();
        
        client.messages.add("Welcome to Draw that! The game is pre-alpha so please be patient and "
                + "inform the developers of any issues.");
        client.messages.add(" \n \n Also please wait for 2 or more people to join.");
        try
        {
            client.messages.add("\n\n Your IP to share is: " + InetAddress.getLocalHost().getHostAddress());
        }
        catch(Exception er){Game.logMessage(er.getMessage());}
        refreshChat();
    }
    
    
    public static void addPoint(int x , int y , int size , Color color)
    {
        if(drawingArea.contains(new Point(x,y)))
        {
            colorPoints[pointSize] = color;
            points[pointSize++] = new Point(x,y);
            
        }
    }
    
    public static void changeName(String newName)
    {
        
    }
    
    public static void addEndPoint(int x , int y)
    {
        if(drawingArea.contains(new Point(x,y)))
        {
            endPoints[endPointsSize++] = new Point(x,y);
            
        }
    }
    
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
        try
        {
            if(client.clearGame)
            {
                points = new Point[20000];
                endPoints = new Point[10000];
                endPointsSize = 0;
                colorPoints = new Color[10000];
                pointSize = 0;
                client.clearGame = false;
            }
            //Draw Images
            g2d.drawImage(back, 0, 0, null);
            g2d.setColor(Color.black);
            g2d.drawRect(0, 109, 573,341);
            g2d.setStroke(stroke);
            //g2d.drawImage(button, 230, 210, null);
            //g2d.drawImage(button,25, 480, null);
            //g2d.drawImage(button, 460, 480, null);
            //g2d.drawImage(title, 150, 0, null);
            for(int i=0;i<pointSize;i++)
            {
                Point p = points[i];
                
                if(p != null)
                {
                    //g2d.fillOval(p.x, p.y, 5, 5);
                    if(i > 0)
                    {
                        for(int j=0;j<endPointsSize;j++)
                        {
                            if(endPoints[j] != null && points[i] != null)
                            {
                                if(endPoints[j].x == points[i].x && endPoints[j].y == points[i].y)
                                {
                                
                                    //g2d.drawRect(endPoints[j].x, endPoints[j].y, 10, 10);
                                    if(i != pointSize)
                                        i++;
                                }
                                //System.out.println(endPoints[j].x);
                            }
                            else
                            {
                                continue;
                            }
                        }
                        g2d.setColor(colorPoints[i]);
                        g2d.drawLine(points[i-1].x, points[i-1].y, p.x, p.y);
                        
                        
                    }
                }
            }
            
            
            Font font = new Font("Calibri", Font.PLAIN, 16);
            Font fontForWord = new Font("Calibri", Font.PLAIN, 32);
            g2d.setFont(font);
            g2d.setColor(Color.blue);
            
            
            //g2d.drawString("Back", 105, 540);
            //g2d.drawString("Connect", 530, 540);
            
            // The scores for each player
            
            Set set = client.scores.entrySet();
            Iterator i = set.iterator();
            int x = 596;
            int y = 489;
            
            while(i.hasNext())
            {

                Map.Entry me = (Map.Entry)i.next();
                String name = (String)me.getKey();
                //System.out.println("turn player = " + client.turnPlayer);
                if(client.turnPlayer.equals(name))
                {
                    if(client.name.equals(name))
                    {
                        name = name + " (Your Turn)";
                        g2d.setFont(fontForWord);
                        g2d.setColor(Color.black);
                        g2d.drawString("Your word to draw is...", 127, 37);
                        g2d.setColor(Color.red);
                        g2d.drawString(client.wordToGuess, 127, 77);
                        g2d.setColor(Color.blue);
                        g2d.setFont(font);
                    }
                    else
                    {
                        name = name + " (His Turn)";
                    }
                }
                int score = (int)me.getValue();
                
                g2d.drawString(name + " : " + score, x, y);
                y += 20;
            }
            
            this.repaint();
            this.revalidate();
            
        }catch(Exception er)
        {
            er.printStackTrace();
        }
    }
    
    
    public static void undo()
    {
        //points[pointSize] = null;
            outerloop:for(int i=pointSize;i>0;i--)
            {
                Point p = points[i];
                for(int j=endPointsSize-1;j>0;j--)
                {
                    if(endPoints[j] != null && points[i] != null)
                    {
                        if(endPoints[j].x == points[i].x && endPoints[j].y == points[i].y)
                        {
                            System.out.println("END");
                            pointSize = i;
                            endPointsSize = j-1;
                            break outerloop;
                        }
                        else
                        {
                            System.out.println("Point deleted");
                            points[i] = null;
                        }
                                //System.out.println(endPoints[j].x);
                    }
                    else
                    {
                        continue;
                    }
                }
                
            }
    }
    

    
    public void run() 
    {
        while(true)
        {
            try
            {
                ListIterator pts = pointsToSend.listIterator();
                while(pts.hasNext())
                {
                    Point p = (Point)pts.next();
                    client.sendMessage("[OP_POINT]" + p.x +","+ p.y + ","+"1" +","+ color);
                    pts.remove();
                }
                
                
                
                
                Thread.sleep(2);
            }catch(Exception er){er.printStackTrace();}
        }
    }
    
    //refreshes the chat and makes sure each message starts a new line if the 
    // line is too long. 
    public static void refreshChat()
    {
        try
        {
            ListIterator it = client.messages.listIterator();
            String messages = "";
            while(it.hasNext())
            {
                String nit = (String)it.next();
                outerloop:
                for(int i=30;i<=nit.length();i += 30)
                {
                    char s;
                    int j = i;
                    do
                    {
                        if(j > 0)
                        {
                            s = nit.charAt(j);
                            j--;
                        }
                        else
                        {
                            break outerloop;
                        }
                    }
                    while(s != ' ');
                    nit = new StringBuilder(nit).insert(j+1, "\n").toString();
                }
            
                chat += nit;
                it.remove();
            }
            chatTA.setText(chat);
            //
        }catch(Exception er)
        {
            Game.logMessage("refreshChat method in class Game/n"+er.getMessage());
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
            return null;
        }
        
    }
    
    public void sendMessage()
    {
        try
        {
            if(!chatTF.getText().equals("") && !chatTF.getText().equals(" "))
            {
                client.sendMessage("[OP_MESSAGE]"+"\n("  + client.name + ") "+chatTF.getText());
                System.out.println("message has been sent with client : " + client.name);
                
                chatTF.setText("");
            }
        }catch(Exception er){er.printStackTrace();}
    }
    

    public void mouseClicked(MouseEvent e) 
    {
        Point p = e.getPoint();
        
        if(chatSend.contains(p))
        {
            System.out.println("clicked");
            sendMessage();
        }
        else if(redArea.contains(p))
        {
            color = "red";
            //client.sendMessage("[OP_TOOL]" + "" +","+ "");
        }
        else if(yellowArea.contains(p))
        {
            color = "yellow";
        }
        else if(orangeArea.contains(p))
        {
            color = "orange";
        }
        else if(greenArea.contains(p))
        {
            color = "green";
        }
        else if(blueArea.contains(p))
        {
            color = "blue";
        }
        else if(pinkArea.contains(p))
        {
            color = "pink";
        }
        else if(blackArea.contains(p))
        {
            color = "black";
        }
        else if(whiteArea.contains(p))
        {
            color = "white";
        }
        else if(undoArea.contains(p))
        {
            //client.sendMessage("[OP_TOOL]" + "undo");
        }
        else if(restartArea.contains(p))
        {
            //client.sendMessage("[OP_TOOL]" + "restart");
        }
    }

    public void mousePressed(MouseEvent e) 
    {
        isPressed = true;
    }

    public void mouseReleased(MouseEvent e) 
    {
        //System.out.println("Mouse released");
        if(client.name.equals(client.turnPlayer))
        {
            isReleased = true;
            Point p = e.getPoint();
            client.sendMessage("[OP_ENDPOINT]" + p.x +","+ p.y);
        }
        //System.out.println("released at " + e.getX() + " " + e.getY());
        
    }

    public void mouseEntered(MouseEvent e) 
    {
       
    }

    public void mouseExited(MouseEvent e) 
    {
        
    }

    public void mouseDragged(MouseEvent e) 
    {
        if(client.name.equals(client.turnPlayer))
        {
            Point p = e.getPoint();
            pointsToSend.add(p);
        }
        
        
    }
    
    //Adjusts the location of the arrow that appears after the user hovers over
    // each button.
    public void mouseMoved(MouseEvent e) 
    {
        
    }

    public void keyTyped(KeyEvent e) 
    {
        //System.out.println(e.getKeyChar());
    }

    public void keyPressed(KeyEvent e) 
    {
        int key = e.getKeyCode();
        //System.out.println(key);
        
        if(key == KeyEvent.VK_ENTER)
        {
            sendMessage();
        }
    }

    public void keyReleased(KeyEvent e) 
    {
        //System.out.println(e.getKeyChar());
    }
    
    public static void logMessage(String er)
    {

        System.out.println(dateFormat.format(date)); //2014-08-06_15-59-48
        
        try
        {
            File dir = new File("logs");
            if(!dir.exists())
            {
                dir.mkdir();
            }
            File f = new File("logs/log_"+dateFormat.format(date)+".txt");
            if(!f.exists())
                f.createNewFile();
        
            FileWriter writer = new FileWriter(f, true);
        
            writer.write(er+ "\n");
            writer.flush();
            writer.close();
                        
        }catch(Exception err){}
    }


}
