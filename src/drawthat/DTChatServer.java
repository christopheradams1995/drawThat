
package drawthat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;

public class DTChatServer implements Runnable
{
    private boolean isEnd = false;
    public int maxplayers = 8;
    static ArrayList clientsInput = new ArrayList();
    static ArrayList clientsOutput = new ArrayList();
    
    static ListIterator itIn = clientsInput.listIterator();
    static ListIterator itOut = clientsOutput.listIterator();
    public String [] players;
    public int [] scores;
    public int numPlayers = 0;
    public int numTurn = 0;
    public String playerTurn;
    
    //words to draw
    static ArrayList words = new ArrayList();
    public int nextWord = 0;
    public static Random ran = new Random();
    //packs of words to add to the game
    boolean easyPack , medPack, hardPack;
    
    public DTChatServer(boolean easyPack ,boolean medPack ,boolean hardPack , int maxPlayers)
    {
        this.maxplayers = maxPlayers;
        this.easyPack = easyPack;
        this.medPack = medPack;
        this.hardPack = hardPack;
        
        
        
        scores  = new int[maxplayers];
        players = new String[maxplayers];
        playerTurn = players[numTurn];
        
        loadPacks(easyPack , medPack , hardPack);
        //System.out.println(words.toString());
        
        for(int i=0;i<maxplayers;i++)
        {
            scores[i] = 0;
        }

        Thread t = new Thread(this);
        t.start();
    }
    
    public void addClient(DataInputStream in, DataOutputStream out)
    {
        itIn.add(in);
        itOut.add(out);
        try
        {
            out.writeUTF("[OP_ID]" + (numPlayers+1));//gives the player their ID
            System.out.println("ID = " + (numPlayers+1));
            //sends the scoreboard over
            //updateScores();
            
        }catch(Exception er){Game.logMessage(er.getMessage());}
        //System.out.println("Client has been added to the arrayList");
    }
    
    public void loadPacks(boolean easy, boolean med , boolean hard)
    {
        Scanner scan = null;
        File easyFile = new File("words/easyWords.txt");
        File medFile = new File("words/mediumWords.txt");
        File hardFile = new File("words/hardWords.txt");
        
        try
        {
            if(easy)
            {
                scan = new Scanner(easyFile);
                while(scan.hasNextLine())
                {
                    String s = scan.nextLine();
                    if(!s.equals("") && !s.equals(" "))
                    {
                        words.add(s);
                    }
                }
            }
            if(med)
            {
                scan = new Scanner(medFile);
                while(scan.hasNextLine())
                {
                    String s = scan.nextLine();
                    if(!s.equals("") && !s.equals(" "))
                    {
                        words.add(s);
                    }
                }
            }
            if(hard)
            {
                scan = new Scanner(hardFile);
                while(scan.hasNextLine())
                {
                    String s = scan.nextLine();
                    if(!s.equals("") && !s.equals(" "))
                    {
                        words.add(s);
                    }
                }
            }
        }catch(Exception er)
        {
            er.printStackTrace();
            Game.logMessage(er.getMessage());
        }
        nextWord = ran.nextInt(words.size());
    }
    
    public void startNewGame()// not used yet
    {
        itOut = clientsOutput.listIterator();
        while(itOut.hasNext())
        {
            try
            {
                DataOutputStream out = (DataOutputStream)itOut.next();
                //System.out.println("sent message from server: " + message);
                for(int i=0;i<numPlayers;i++)
                {
                    out.writeUTF("[OP_NEWGAME]"+players[i] +","+scores[i]);
                }
            
                out.flush();
            }catch(Exception er)
            {
                Game.logMessage(er.getMessage());
            }
        }
    }
    
    public void updateTurn()
    {
        playerTurn = players[numTurn];
        updatePlayer("[OP_TURN]"+playerTurn + "," + words.get(nextWord));
        
    }
    
    /**
     * Give this method an OP message to send to all players
     */
    public void updatePlayer(String message)
    {
        itOut = clientsOutput.listIterator();
        while(itOut.hasNext())
        {
            try
            {
                DataOutputStream out = (DataOutputStream)itOut.next();
                //System.out.println("sent message from server: " + message);
                    //out.writeUTF("[OP_SCORE]"+players[i] +","+scores[i]);
                    out.writeUTF(message);
            
                out.flush();
            }catch(Exception er)
            {
                Game.logMessage(er.getMessage());
                er.printStackTrace();
            }
        }
    }
    
    public void run()
    {

        String message = "";
        boolean newMessage = false;
        int i = 0;
        //for the index of the current person.
        int cur = 1;
        
        while(!isEnd)
        {
            i++;
            
            try
            {
                itIn = clientsInput.listIterator();
                itOut = clientsOutput.listIterator();
                
                
                while(itIn.hasNext())
                {
                    // checks the input from each client that's connected to the server
                    DataInputStream in = (DataInputStream)itIn.next();
                    cur++;
                    int count = in.available();
                    
                    if(count > 0)
                    {
                        message = in.readUTF(); 
                        newMessage = true;
                        
                        //reads the message and does something if it's a server command
                    
                        int index = message.indexOf("]"); //gets the index of the end of the OP code
                        String Op = message.substring(0, index+1); // used for sending different messages
                    
                        if(Op.equals("[OP_newPlayer]"))
                        {
                            String name = message.substring(index+1);
                            System.out.println(name);
                            name = getNewName(name);
                            System.out.println("name had to change to: " + name);
                            this.players[this.numPlayers] = name;
                            
                            //sends the new name to the player
                            message = "[OP_PlayerMessage]"+(numPlayers+1) + ",newName,"+name;
                            numPlayers++;
                        }
                        if(Op.equals("[OP_MESSAGE]"))//chat message
                        {
                            String text = message.substring(index+1).toLowerCase();
                            String currentWord = (String)words.get(nextWord);
                            if(text.contains(currentWord.toLowerCase()))
                            {
                                message = "[OP_PlayerMessage]"+cur+ ",correctGuess";
                                scores[cur-1] += 1;
                                scores[numTurn] += 2;
                                nextWord = ran.nextInt(words.size());
                                numTurn++;
                                if(numTurn >= this.numPlayers)
                                    numTurn = 0;
                                
                            }
                        }
                    }
                    

                    
                    //if a message is received from one of the users then it's broadcasted to everyone.
                    while(itOut.hasNext() && newMessage)
                    {
                            
                        DataOutputStream out = (DataOutputStream)itOut.next();
                        //System.out.println("sent message from server: " + message);
                        out.writeUTF(message);
                        out.flush();
                        
                    }
                    
                    if(i>100 && this.numPlayers >= 2)
                    {
                        
                        for(int j=0;j<numPlayers;j++)
                        {
                            //out.writeUTF("[OP_SCORE]"+players[i] +","+scores[i]);
                            updatePlayer("[OP_SCORE]"+players[j] +","+scores[j]);
                        }
                        
                        updateTurn();
                        i = 0;
                    }
                    
                    newMessage = false;
                }
                Thread.sleep(5);
                
                            
                
            }
            catch(SocketTimeoutException s)
            {
                try
                {
                    Game.logMessage(s.getMessage());
                }catch(Exception er){er.printStackTrace();}
                //break;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Game.logMessage(e.getMessage());
                break;
            }
            cur = 0;
        }
    }
    
    //checks to see if a player's name already exists. If it does then it adds a number to the end.
    // returns the original name if it doesn't exist or returns the new name if it does exist with the number appended
    public String getNewName(String name)
    {
        String newname = name;
        int num = 0;
        
        for(int i=0;i<this.numPlayers;i++)
        {
            if(players[i].equals(newname))
            {
                newname = name + num++;
                i = 0;
            }
        }
        
        return newname;
    }
            
}
