/**
 * Game
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
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
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.shape.Line;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

/**
 * This class deals with the game's UI and all the game logic.
 * All the drawing , chats and user interaction is done here.
 */
public class Game extends JComponent implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    //Rectangles to sense mouse clicks
    private static final Rectangle chatSend= new Rectangle(754,415,35,33);
    private static final Rectangle drawingArea = new Rectangle(0, 109, 573,341);
    private static final Rectangle redArea = new Rectangle(10, 484, 64,44);
    private static final Rectangle yellowArea = new Rectangle(74, 473, 67,40);
    private static final Rectangle orangeArea = new Rectangle(146, 464, 66,42);
    private static final Rectangle greenArea = new Rectangle(218, 467, 67,47);
    private static final Rectangle blueArea = new Rectangle(291, 473, 69,47);
    private static final Rectangle pinkArea = new Rectangle(369, 491, 66,47);
    private static final Rectangle blackArea = new Rectangle(452, 517, 64,47);
    private static final Rectangle whiteArea = new Rectangle(15, 547, 45,43);
    private static final Rectangle restartArea = new Rectangle(14, 61, 80,42);
    private static final Rectangle skipArea = new Rectangle(479, 58, 89,45);
    private static final Rectangle brush1Area = new Rectangle(131, 533, 168,9);
    private static final Rectangle brush2Area = new Rectangle(131, 548, 171,13);
    private static final Rectangle brush3Area = new Rectangle(132, 565, 169,11);
    private static final Rectangle brush4Area = new Rectangle(132, 583, 169,11);
    private static Rectangle shinyColorLoc = new Rectangle(452, 517, 64,47);
    private static Rectangle shinyBrushLoc = new Rectangle(132, 565, 169,11);
    
    //Chat components
    public static JTextArea chatTA = new JTextArea("",20,20);
    public DefaultCaret caret;
    private JScrollPane scroll;
    private static final JTextField chatTF = new JTextField();
    static String chat = "";
    
    //Gets the date for the log file
    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    private static DTClient client;
    boolean sendmessage = false;
    static boolean isRefresh = false;
    
    //Drawing settings
    static int pointSize = 0;
    static int lineSize = 0;
    public static Point points[] = new Point[20000];
    
    //these points are sent to other clients
    public static List<Point[]> linesToSend = new CopyOnWriteArrayList<>();
    
    //List of lines to be added to linesToSend on the second Thread
    public static List<Point[]> waitingLines = new CopyOnWriteArrayList<>();
    
    //Doesn't send these points across the network , only displays them locally while drawing
    public static List<Point> pointsToDrawLocally = new ArrayList<>();
    
    //Lines that are recieved from across the network that will be drawn
    public static List<DrawLine> linesToDraw = new CopyOnWriteArrayList<>();
    
    //Drawing brush settings
    private String color = "black";
    private String brushSize = "3";
    Stroke stroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0,
    new float[] { 3, 1 }, 0);
    
    //Used to create the drawing to send
    static Point start;
    public static List<Point> pointsToSend = new CopyOnWriteArrayList<>();
    static Point end;
    
    //Images that are being drawn
    BufferedImage back;
    BufferedImage button;
    BufferedImage title;
    BufferedImage arrow;      
    BufferedImage shinyColor; 
    BufferedImage shinyErase;
    BufferedImage shiny; 
    BufferedImage shinyBrush; 
    
    //Sets player's name , adds chat and the welcome message
    Game(DTClient client, String name) {
        this.client = client;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);
        this.requestFocus();
        
        //sets the default player's name to their IP address
        try {
            if(name.equals("") || name.equals(" ")) {
                name = InetAddress.getLocalHost().getHostAddress();
                System.out.println("game constructor name: " + name);
            }
        }
        catch(Exception er) {
            er.printStackTrace();
        }
        
        client.sendMessage("[OP_newPlayer]"+name);

        //Load Images
        back = DrawThat.getImage("images/gameBack.png");
        shinyColor = DrawThat.getImage("images/shinyColor.png");
        shinyErase = DrawThat.getImage("images/shinyErase.png");
        shinyBrush = DrawThat.getImage("images/shinyBrush.png");
        shiny = shinyColor;
        
        //Add text fields and stuff for information input
        chatTF.setBounds(591, 418, 164, 30);
        
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
        
        //Adds the welcome message
        client.messages.add("Welcome to Draw that! The game is pre-alpha so please be patient and "
                + "inform the developers of any issues.");
        client.messages.add(" \n \n Also please wait for 2 or more people to join.");
        
        // Gives the host the IP to share with friends
        try {
            client.messages.add("\n\n Your IP to share is: " + InetAddress.getLocalHost().getHostAddress());
        }
        catch(Exception er) {
            DrawThat.logMessage(er.getMessage());
        }
        refreshChat();
    }
    
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
        
        //drawing canvas
        Graphics2D back2D = back.createGraphics();
        back2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
        
        try {
            if(client.clearGame) {
                points = new Point[20000];
                pointSize = 0;
                client.clearGame = false;
                //back = getImage("images/gameBack.png");
                back = DrawThat.getImage("images/gameBack.png");
                //back2D = back.createGraphics();
            }
            //Draw Images
            g2d.drawImage(back, 0, 0, null);
            g2d.setColor(Color.black);
            g2d.drawRect(0, 109, 573,341);

            g2d.drawImage(shiny, shinyColorLoc.x-6,shinyColorLoc.y-6, this);
            g2d.drawImage(shinyBrush, shinyBrushLoc.x,shinyBrushLoc.y, this);
            
            //These points were recieved from the network or locally if the user
            //is the host
            Iterator<DrawLine> lines = linesToDraw.iterator();
            while(lines.hasNext()) {
                DrawLine line = lines.next();
                if(line == null) continue;
                
                back2D.setColor(line.getColor());
                g2d.setColor(line.getColor());
                setBrushSize(line.getSize());
                Point[] points = line.getPoints();
                back2D.setStroke(stroke);
                g2d.setStroke(stroke);
                
                for(int j=0;j<points.length;j++) {
                    if(j <= points.length-2 && points[j] != null && points[j+1] != null) {
                        back2D.drawLine(points[j].x,points[j].y,points[j+1].x,points[j+1].y);
                    }
                }
                linesToDraw.remove(line);//once a line has been added to the back bufferedImage it's removed
            }
            
            //Draws the points while the user is drawing
            Point[] pointsLoc = (pointsToDrawLocally.toArray(new Point[pointsToDrawLocally.size()]));
            for(int i=0;i<pointsLoc.length-1;i++) {
                g2d.setColor(DTClient.stringToColor(color));
                setBrushSize(Integer.parseInt(brushSize));
                g2d.setStroke(stroke);
                Point p = pointsLoc[i];
                Point p2 = pointsLoc[i+1];
                g2d.drawLine(p.x, p.y, p2.x, p2.y);
            }
            
            Font font = new Font("Calibri", Font.PLAIN, 16);
            Font fontForWord = new Font("Calibri", Font.PLAIN, 32);
            g2d.setFont(font);
            g2d.setColor(Color.blue);

            // The scores for each player
            Set set = client.scores.entrySet();
            Iterator i = set.iterator();
            int x = 596;
            int y = 489;
            
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                String name = (String)me.getKey();
                
                //checks if it's the player's turn
                if(client.turnPlayer.equals(name)) {
                    if(client.name.equals(name)) {
                        name = name + " (Your Turn)";
                        g2d.setFont(fontForWord);
                        g2d.setColor(Color.black);
                        g2d.drawString("Your word to draw is...", 127, 37);
                        g2d.setColor(Color.red);
                        g2d.drawString(client.wordToGuess, 127, 77);
                        g2d.setColor(Color.blue);
                        g2d.setFont(font);
                    }
                    else {
                        name = name + " (His Turn)";
                    }
                }
                int score = (int)me.getValue();
                
                g2d.drawString(name + " : " + score, x, y);
                y += 20;
            }
            
        }
        catch(Exception er) {
            er.printStackTrace();
        }
    }
    
    public void refreshScreen() {
        this.repaint();
        this.revalidate();
    }
    
    public void setBrushSize(int size) {
        stroke = new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0,
        new float[] { 3, 1 }, 0);
    }
    
    public void skipTurn() {
        client.sendMessage("[OP_SKIP]");
    }
    
    //Sends the drawing across the network to other users and refreshes the 
    //screen if points are recieved.
    @Override
    public void run() {
        while(true) {
            try {
                Iterator<Point[]> lts = linesToSend.iterator();
                while(lts.hasNext()) {
                    Point[] points = lts.next();
                    Point p;
                    
                    for(int i=0;i<points.length;i++) {
                        p = points[i];
                        if(p == null) System.out.println("NULL AT POINT AT:"+i);
                        if(i==0) {
                            System.out.println("FIRST");
                            if(p!= null)
                                client.sendMessage("[OP_POINT_FIRST]" + p.x +","+ p.y + ","+brushSize +","+ color);
                        }
                        if(i==points.length-1) {
                            System.out.println(p);
                            if(p!= null)
                                client.sendMessage("[OP_POINT_LAST]" + p.x +","+ p.y);
                            
                        }
                        else {
                            if(p!= null) client.sendMessage("[OP_POINT]" + p.x +","+ p.y);
                        }
                    }
                    linesToSend.remove(points);
                }
                
                Iterator<Point[]> lines = waitingLines.iterator();
                while(lines.hasNext()) {
                    Point[] dl = lines.next();
                    sendLine(dl);
                    waitingLines.remove(dl);
                }
                
                if(!pointsToDrawLocally.isEmpty()) {
                    refreshScreen();
                }
                else if(!linesToDraw.isEmpty()) {
                    refreshScreen();
                }
                if(isRefresh) {
                    refreshScreen();
                    isRefresh = false;
                }
            }
            catch(Exception er) {
                er.printStackTrace();
            }
        }
    }
    
    //refreshes the chat and makes sure each message starts a new line if the 
    // line is too long. 
    public static void refreshChat() {
        try {
            ListIterator it = client.messages.listIterator();
            while(it.hasNext()) {
                String nit = (String)it.next();
                outerloop:
                for(int i=30;i<=nit.length();i += 30) {
                    char s;
                    int j = i;
                    do {
                        if(j > 0) {
                            s = nit.charAt(j);
                            j--;
                        }
                        else {
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
        }
        catch(Exception er) {
            DrawThat.logMessage("refreshChat method in class Game/n"+er.getMessage());
        }
    }
    
    /**
     * Sends the chat message to the other clients
     */
    public void sendMessage() {
        try {
            if(!chatTF.getText().equals("") && !chatTF.getText().equals(" ")) {
                client.sendMessage("[OP_MESSAGE]"+"\n("  + client.name + ") "+chatTF.getText());
                System.out.println("message has been sent with client : " + client.name);
                
                chatTF.setText("");
            }
        }catch(Exception er) {
            er.printStackTrace();
        }
    }
    
    //Sets the paint brush settings if the user has clicked the toolbar
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        
        shiny = shinyColor;
        if(chatSend.contains(p)) {
            System.out.println("clicked");
            sendMessage();
        }
        else if(redArea.contains(p)) {
            color = "red";
            shinyColorLoc = redArea;
        }
        else if(yellowArea.contains(p)) {
            color = "yellow";
            shinyColorLoc = yellowArea;
        }
        else if(orangeArea.contains(p)) {
            color = "orange";
            shinyColorLoc = orangeArea;
        }
        else if(greenArea.contains(p)) {
            color = "green";
            shinyColorLoc = greenArea;
        }
        else if(blueArea.contains(p)) {
            color = "blue";
            shinyColorLoc = blueArea;
        }
        else if(pinkArea.contains(p)) {
            color = "pink";
            shinyColorLoc = pinkArea;
        }
        else if(blackArea.contains(p)) {
            color = "black";
            shinyColorLoc = blackArea;
        }
        else if(whiteArea.contains(p)) {
            color = "white";
            Rectangle rec = new Rectangle(whiteArea.x-6,whiteArea.y-6,whiteArea.width,whiteArea.height);
            shinyColorLoc = rec;
        }
        if(restartArea.contains(p)) {
            if(client.name.equals(client.turnPlayer))
                client.sendMessage("[OP_CLEAR]");
        }
        if(skipArea.contains(p)) {
            if(client.name.equals(client.turnPlayer)) {
                this.skipTurn();
                client.sendMessage("[OP_CLEAR]");
            }
        }
        
        if(whiteArea.contains(p)) {
            shiny = shinyErase;
        }
        
        if(brush1Area.contains(p)) {
            brushSize = "1";
            shinyBrushLoc = brush1Area;
        }
        else if(brush2Area.contains(p)) {
            brushSize = "2";
            shinyBrushLoc = brush2Area;
        }
        else if(brush3Area.contains(p)) {
            brushSize = "3";
            shinyBrushLoc = brush3Area;
        }
        else if(brush4Area.contains(p)) {
            brushSize = "4";
            shinyBrushLoc = brush4Area;
        }
    }
    
    //Method which recieves a point from across the network and adds it into the
    //array to be drawn
    public static void addLineToDraw(DrawLine points) {
        linesToDraw.add(points);
        pointsToDrawLocally.clear();
    }
    
    // Adds the line to local array if the user is drawing and sends it across 
    // to the network to other users
    public void sendLine(Point[] pointsToSend) {
        //checks to see if all the points are valid
        Point[] points = pointsToSend;
        for(int i=0;i<points.length;i++) {
            if(points[i] == null) continue;
            if(!drawingArea.contains(points[i])) {
                pointsToSend[i] = null;
            }
        }
        if(client.name.equals(client.turnPlayer)) {
            //Draws the points locally
            Color color = DTClient.stringToColor(this.color);
            this.addLineToDraw(new DrawLine(start,pointsToSend,end,color,Integer.parseInt(brushSize)));
            
            //Sends the points to other users
            linesToSend.add(pointsToSend);
            
        }
    }
    
    /** Adds a new set of points to send which sits in an array until it's sent as a message
     * the array will be iterated over in the run() method.
    **/
    public void addLineToSend() {
        Point[] pointsToSend = new Point[this.pointsToSend.size()+2];
        pointsToSend[0] = start;
        pointsToSend[pointsToSend.length-1] = end;
        for(int i=1;i<this.pointsToSend.size();i++) {
            pointsToSend[i] = this.pointsToSend.get(i);
        }
        
        waitingLines.add(pointsToSend);
        //The mouse points are cleared
        this.pointsToSend.clear();
        start = null;
        end = null;
    }
    
    /**
     * This takes a point and draws it locally for this user so they can see
     * what they are drawing. This point does not get sent to other users
     * @param p to be drawn
     */
    public void addPoint(Point p) {
        if(drawingArea.contains(p)) {
            pointsToDrawLocally.add(p);
        }
    }

    public void mousePressed(MouseEvent e) {
        if(client.name.equals(client.turnPlayer)) {
            if(start == null) {
                start = e.getPoint();
                addPoint(e.getPoint());
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(client.name.equals(client.turnPlayer)) {
            if(end == null) {
                end = e.getPoint();
                addPoint(e.getPoint());
                addLineToSend();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        
    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        if(client.name.equals(client.turnPlayer)) {
            if(start != null && end == null) {
                pointsToSend.add(p);
                addPoint(p);
            }
        }
    }
    
    public void mouseMoved(MouseEvent e) {
        
    }

    public void keyTyped(KeyEvent e) {
        
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    public void keyReleased(KeyEvent e) {
        
    }
}
