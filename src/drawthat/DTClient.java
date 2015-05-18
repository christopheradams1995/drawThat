/**
 * DTClient
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
 */

package drawthat;

import java.awt.Color;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//Deals with all the client information and recieves messages from the server
//then sends it to the game class
public class DTClient implements Runnable {
    DataOutputStream out;
    DataInputStream in;
    static Thread t;
    
    static ArrayList messages = new ArrayList();
    public int ID;
    public String name;
    HashMap scores = new HashMap();
    String turnPlayer = "";
    String wordToGuess = "";
    boolean clearGame = false;
    
    Point start;
    List<Point> pointsToSend = new CopyOnWriteArrayList<>();
    DrawLine points;
    Point end;
    
    //Creates the client and joins the server
    public DTClient(String serverName , int port) throws IOException, Exception {
        Socket client;
        try {
            // Joins the server
            System.out.println("Created a server at " + serverName);
            client = new Socket(serverName, port);
            
            OutputStream outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            
            InputStream inFromServer = client.getInputStream();
            in = new DataInputStream(inFromServer);
            System.out.println("Client has been created");
            t = new Thread(this);
            t.start();
        }
        catch(Exception er) {
            er.printStackTrace();
        }
    }
    
    /**
     * @param message: text to be sent to the server in UTF format. In drawThat
     * the standard format has the type of message at the start in brackets then
     * the parameters of the message are separated by ",". The points , chat and
     * any other kind of message is sent through this method.
     * For example "[OP_TURN]Chris,tree" for telling a user they have to draw the
     * word "tree"
     */
    public void sendMessage(String message) {
        try {
            this.out.writeUTF(message);
            out.flush();
        }
        catch(Exception er) {
            er.printStackTrace();
        }
    }
    
    public static Color stringToColor(String colorString) {
        Color color = Color.black;    
        switch(colorString) {
            case "red":
                color = Color.red;
                break;
            case "yellow":
                color = Color.yellow;
                break;
            case "orange":
                color = Color.orange;
                break;
            case "green":
                color = Color.green;
                break;
            case "blue":
                color = Color.blue;
                break;
            case "pink":
                color = Color.pink;
                break;
            case "white":
                color = Color.white;
                break;
        }
        return color;
    }
    
    //Checks to see if a message is recieved and if it has then it decodes it by
    //first getting the op message then it's parameters. 
    @Override
    public void run() {
        while(true) {
            try {
                int count = in.available();
                
                if(count > 0) {
                    String message = in.readUTF();
                    int index = message.indexOf("]"); //gets the index of the end of the OP code
                    String Op = message.substring(0, index+1); // used for sending different messages
                    switch (Op) {
                        case "[OP_MESSAGE]": //Message for the chat
                            messages.add(message.substring(12));
                            Game.refreshChat();
                            break;
                        case "[OP_POINT_FIRST]": { //start of a line
                                String props = message.substring(index+1);
                                String [] split = props.split(",");
                                int x = Integer.parseInt(split[0]);
                                int y = Integer.parseInt(split[1]);
                                int size = Integer.parseInt(split[2]);
                                String colorString = split[3];
                                Color color = stringToColor(colorString);
                                points = new DrawLine(new Point(x,y),color,size);
                                System.out.println("[OP_POINT_FIRST] Recieved the first point");
                                break;
                            }
                        case "[OP_POINT]": { //middle points of a line
                                String props = message.substring(index+1);
                                String [] split = props.split(",");
                                int x = Integer.parseInt(split[0]);
                                int y = Integer.parseInt(split[1]);
                                pointsToSend.add(new Point(x,y));
                                //Game.addPoint(new DrawPoint(new Point(x,y), color, size));
                                System.out.println("Made new point");
                                break;
                            }
                        case "[OP_POINT_LAST]": { //gets the last point of a line then builds the line
                                System.out.println("OP_POINT_LAST Recieved the last point");
                                String props = message.substring(index+1);
                                String [] split = props.split(",");
                                int x = Integer.parseInt(split[0]);
                                int y = Integer.parseInt(split[1]);
                                if(points != null) {
                                    System.out.println("OP_POINT_LAST points is not null");
                                    points.setEnd(new Point(x,y));
                                    points.setMid(pointsToSend.toArray(new Point[pointsToSend.size()]));
                                    
                                    Game.addLineToDraw(points);
                                    start = null;
                                    end = null;
                                    points = null;
                                    pointsToSend.clear();
                                }
                                else {
                                    System.out.println("NILL POINT LAST");
                                    DrawThat.logMessage("Found a nill point");
                                }       break;
                            }
                        case "[OP_ID]": {
                            String ID = message.substring(index+1);
                                this.ID = Integer.parseInt(ID);
                                break;
                        }
                        case "[OP_CLEAR]":
                        this.clearGame = true;
                        break;
                        case "[OP_PlayerMessage]": { // a specific message to a certain player
                            String props = message.substring(index+1);
                            String [] split = props.split(",");
                            int ID = Integer.parseInt(split[0]);
                            if(ID == this.ID) {
                                String playerMessage = split[1];
                                
                                if(playerMessage.equals("newName")) {
                                    this.name = split[2];
                                    System.out.println("new name has been set on client to " + split[2]);
                                }
                                else if(playerMessage.equals("correctGuess")) {
                                    if(!turnPlayer.equals(name)) {
                                        messages.add("\nCorrect Guess!");
                                        this.sendMessage("[OP_MESSAGE]\n Player: " + this.name + " has guessed the word");
                                        this.sendMessage("[OP_CLEAR]");
                                    }   
                                }
                            }
                            break;
                        }
                        case "[OP_SCORE]": { // gets a score from the server
                            String props = message.substring(index+1);
                            String [] split = props.split(",");
                            String name = split[0];
                                int score = Integer.parseInt(split[1]);
                                scores.put(name, score);
                                break;
                        }
                        case "[OP_TURN]": { //gets whos turn it is
                            String props = message.substring(index+1);
                            String [] split = props.split(",");
                            String name = split[0];
                            String word = split[1];
                            this.wordToGuess = word;
                            this.turnPlayer = name;
                                Game.isRefresh = true;
                                break;
                            }
                    }
                }
                Thread.sleep(5);
            }
            catch(Exception er) {
                DrawThat.logMessage(er.getMessage());
                er.printStackTrace();
            }
        }
    }
}
