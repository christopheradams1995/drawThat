/**
 * DTChatServer
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
 */

package drawthat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;

//Deals with all the server side game logic and client logic
public class DTChatServer implements Runnable {
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
    
    //Starts a new server depending on what settings were given
    public DTChatServer(boolean easyPack ,boolean medPack ,boolean hardPack , int maxPlayers) {
        this.maxplayers = maxPlayers;
        this.easyPack = easyPack;
        this.medPack = medPack;
        this.hardPack = hardPack;
        
        scores  = new int[maxplayers];
        players = new String[maxplayers];
        playerTurn = players[numTurn];
        
        loadPacks(easyPack , medPack , hardPack);
        
        for(int i=0;i<maxplayers;i++) {
            scores[i] = 0;
        }

        Thread t = new Thread(this);
        t.start();
    }
    
    //Adds the client streams and gives the user their ID
    public void addClient(DataInputStream in, DataOutputStream out) {
        itIn.add(in);
        itOut.add(out);
        try {
            out.writeUTF("[OP_ID]" + (numPlayers+1));//gives the player their ID
            System.out.println("ID = " + (numPlayers+1));
            
        }
        catch(Exception er) {
            DrawThat.logMessage(er.getMessage());
        }
    }
    
    //Reads the word files and saves them
    public void loadPacks(boolean easy, boolean med , boolean hard) {
        Scanner scan = null;
        File easyFile = new File("words/easyWords.txt");
        File medFile = new File("words/mediumWords.txt");
        File hardFile = new File("words/hardWords.txt");
        
        try {
            if(easy) {
                scan = new Scanner(easyFile);
                while(scan.hasNextLine()) {
                    String s = scan.nextLine();
                    if(!s.equals("") && !s.equals(" ")) {
                        words.add(s);
                    }
                }
            }
            if(med) {
                scan = new Scanner(medFile);
                while(scan.hasNextLine()) {
                    String s = scan.nextLine();
                    if(!s.equals("") && !s.equals(" ")) {
                        words.add(s);
                    }
                }
            }
            if(hard) {
                scan = new Scanner(hardFile);
                while(scan.hasNextLine()) {
                    String s = scan.nextLine();
                    if(!s.equals("") && !s.equals(" ")) {
                        words.add(s);
                    }
                }
            }
        }
        catch(Exception er)
        {
            er.printStackTrace();
            DrawThat.logMessage(er.getMessage());
        }
        nextWord = ran.nextInt(words.size());
    }
    
    public void startNewGame() {
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
                DrawThat.logMessage(er.getMessage());
            }
        }
    }
    
    //Changes whos turn it is then updates the player that it's their turn
    public void updateTurn() {
        playerTurn = players[numTurn];
        updatePlayer("[OP_TURN]"+playerTurn + "," + words.get(nextWord));
    }
    
    //Sends a message to all players
    public void updatePlayer(String message) {
        itOut = clientsOutput.listIterator();
        while(itOut.hasNext()) {
            try {
                DataOutputStream out = (DataOutputStream)itOut.next();
                out.writeUTF(message);
            
                out.flush();
            }
            catch(Exception er) {
                DrawThat.logMessage(er.getMessage());
                er.printStackTrace();
            }
        }
    }
    
    //Checks to see if a message was recieved from a client. Decodes the message
    // and responds if it was for the server. Broadcasts the message to all the 
    // client
    public void run() {

        String message = "";
        boolean newMessage = false;
        int i = 0;
        //for the index of the current person.
        int cur = 1;
        
        while(!isEnd) {
            i++;
            
            try {
                itIn = clientsInput.listIterator();
                itOut = clientsOutput.listIterator();
                
                while(itIn.hasNext()) {
                    // checks the input from each client that's connected to the server
                    DataInputStream in = (DataInputStream)itIn.next();
                    cur++;
                    int count = in.available();
                    
                    if(count > 0) {
                        message = in.readUTF(); 
                        newMessage = true;
                        
                        //reads the message and does something if it's a server command
                    
                        int index = message.indexOf("]"); //gets the index of the end of the OP code
                        String Op = message.substring(0, index+1); // used for sending different messages
                    
                        if(Op.equals("[OP_newPlayer]")) {
                            String name = message.substring(index+1);
                            System.out.println(name);
                            name = getNewName(name);
                            System.out.println("name had to change to: " + name);
                            this.players[this.numPlayers] = name;
                            
                            //sends the new name to the player
                            message = "[OP_PlayerMessage]"+(numPlayers+1) + ",newName,"+name;
                            numPlayers++;
                        }
                        // checks if the player correctly guessed the word
                        if(Op.equals("[OP_MESSAGE]")){ //chat message
                        
                            String text = message.substring(index+1).toLowerCase();
                            String currentWord = (String)words.get(nextWord);
                            if(text.contains(currentWord.toLowerCase())) {
                                message = "[OP_PlayerMessage]"+cur+ ",correctGuess";
                                scores[cur-1] += 1;
                                scores[numTurn] += 2;
                                nextWord = ran.nextInt(words.size());
                                numTurn++;
                                if(numTurn >= this.numPlayers)
                                    numTurn = 0;
                            }
                        }
                        //skips the current word and gives the next
                        if(Op.equals("[OP_SKIP]")) {
                            nextWord = ran.nextInt(words.size());
                        }
                    }
                    
                    //if a message is received from one of the users then it's broadcasted to everyone.
                    while(itOut.hasNext() && newMessage) {
                        DataOutputStream out = (DataOutputStream)itOut.next();
                        //System.out.println("sent message from server: " + message);
                        out.writeUTF(message);
                        out.flush();
                    }
                    
                    if(i>100 && this.numPlayers >= 2) {
                        for(int j=0;j<numPlayers;j++) {
                            updatePlayer("[OP_SCORE]"+players[j] +","+scores[j]);
                        }
                        
                        updateTurn();
                        i = 0;
                    }
                    
                    newMessage = false;
                }
                Thread.sleep(5);
            }
            catch(SocketTimeoutException s) {
                try {
                    DrawThat.logMessage(s.getMessage());
                }catch(Exception er) {
                    er.printStackTrace();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                DrawThat.logMessage(e.getMessage());
                break;
            }
            cur = 0;
        }
    }
    
    //checks to see if a player's name already exists. If it does then it adds a number to the end.
    // returns the original name if it doesn't exist or returns the new name if it does exist with the number appended
    public String getNewName(String name) {
        String newname = name;
        int num = 0;
        
        for(int i=0;i<this.numPlayers;i++) {
            if(players[i].equals(newname)) {
                newname = name + num++;
                i = 0;
            }
        }
        return newname;
    }  
}
