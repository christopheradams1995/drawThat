
package drawthat;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class DTClient implements Runnable
{
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
    //what words to add to the game
    
    
    
    public DTClient(String serverName , int port)  throws IOException, Exception
    {
        Socket client = null;
        
        try
        {
            // Joins the server
            
            client = new Socket(serverName, port);
            
            OutputStream outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            
            
            InputStream inFromServer = client.getInputStream();
            in = 
                    new DataInputStream(inFromServer);
            System.out.println("Client has been created");
            t = new Thread(this);
            t.start();
        }
        finally
        {
            try
            {
                //client.close();
            }catch(Exception er)
            {
                
            }
        }
    }
    
    /**
     * 
     * @param message: text to be sent to the server in UTF format. In drawThat
     * the standard format has the type of message at the start in brackets then
     * the parameters of the message are seperated by ",". The points , chat and
     * any other kind of message is sent through this method.
     */
    public void sendMessage(String message)
    {
        try
        {
            this.out.writeUTF(message);
            out.flush();
        }
        catch(Exception er)
        {
            er.printStackTrace();
        }
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                
                int count = in.available();
                
                if(count > 0)
                {
                    String message = in.readUTF();
                    int index = message.indexOf("]"); //gets the index of the end of the OP code
                    String Op = message.substring(0, index+1); // used for sending different messages
                        
                    if(Op.equals("[OP_MESSAGE]"))//chat message
                    {
                        messages.add(message.substring(12));
                        Game.refreshChat();
                    }
                    else if(Op.equals("[OP_POINT]"))//point to paint
                    {
                        String props = message.substring(index+1);
                        String [] split = props.split(",");
                        
                        int x = Integer.parseInt(split[0]);
                        int y = Integer.parseInt(split[1]);
                        int size = Integer.parseInt(split[2]);
                        String colorString = split[3];
                        
                        Color color = Color.black;
                        
                        switch(colorString)
                        {
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
                        
                        Game.addPoint(x, y, size, color);
                        
                    }
                    else if(Op.equals("[OP_ID]"))// gives the user ID
                    {
                        String ID = message.substring(index+1);
                        this.ID = Integer.parseInt(ID);
                    }
                    else if(Op.equals("[OP_CLEAR]"))// gives the user ID
                    {
                        this.clearGame = true;
                    }
                    else if(Op.equals("[OP_PlayerMessage]"))// a specific message to a certain player
                    {
                        String props = message.substring(index+1);
                        String [] split = props.split(",");
                        int ID = Integer.parseInt(split[0]);
                        
                        if(ID == this.ID)
                        {
                            String playerMessage = split[1];
                            
                            if(playerMessage.equals("newName"))
                            {
                                this.name = split[2];
                                System.out.println("new name has been set on client to " + split[2]);
                            }
                            else if(playerMessage.equals("correctGuess"))
                            {
                                if(!turnPlayer.equals(name))
                                {
                                    messages.add("\nCorrect Guess!");
                                    this.sendMessage("[OP_MESSAGE]\n Player: " + this.name + " has guessed the word");
                                    this.sendMessage("[OP_CLEAR]");
                                }
                            }
                        }
                    }
                    else if(Op.equals("[OP_SCORE]"))// gets a score from the server
                    {
                        String props = message.substring(index+1);
                        String [] split = props.split(",");
                        String name = split[0];
                        int score = Integer.parseInt(split[1]);
                        scores.put(name, score);
                    }
                    
                    //("[OP_TURN]"+playerTurn + "," + words.get(nextWord));
                    else if(Op.equals("[OP_TURN]"))// gets a score from the server
                    {
                        String props = message.substring(index+1);
                        String [] split = props.split(",");
                        String name = split[0];
                        String word = split[1];
                        
                        this.wordToGuess = word;
                        this.turnPlayer = name;
                        
                    }
                    else if(Op.equals("[OP_TOOL]"))
                    {
                        String props = message.substring(index+1);
                        
                        if(props.equals("undo"))
                        {
                            Game.undo();
                        }
                        else if(props.equals("[RESTART]"))
                        {
                            //Game.restart = true;
                        }
                    }
                    else if(Op.equals("[OP_ENDPOINT]"))//used instead of [OP_Released]
                    {
                        int xindex = message.indexOf(",");
                        int x = Integer.parseInt(message.substring(index+1,xindex));
                        int yindex = message.substring(xindex+1).indexOf(",");
                        int y = Integer.parseInt(message.substring(xindex+1));
                        //System.out.println("Added the point : " + x + " " + y);
                        Game.addEndPoint(x, y);
                        
                        // (x , y , size , color)
                    }
                    
                }
                Thread.sleep(5);
            }catch(Exception er){er.printStackTrace();}
        }
    }

}
