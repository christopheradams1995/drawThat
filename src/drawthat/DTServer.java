/**
 * DTServer
 * 
 * v1.0
 * 
 * 15/05/2015
 * 
 * This file is owned by Christopher Adams
 */

package drawthat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;

//Creates a server then waits for people ot join and if they do then adds them to
// the DTChatServer class.
public class DTServer implements Runnable {
    private ServerSocket serverSocket;
    private final boolean isEnd = false;
    DataInputStream in;
    DataOutputStream out;
    static DTChatServer chatServer;
    int j = 1;
    // stores the settings to give to the child chat server class
    boolean easyPack , medPack , hardPack;
    int maxPlayers;
    int backlog = 10;
    
    public DTServer(int port, boolean easyPack , boolean medPack , boolean hardPack, int maxPlayers) {
        try {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        
        this.easyPack = easyPack;
        this.medPack = medPack;
        this.hardPack = hardPack;
        this.maxPlayers = maxPlayers;
        }
        catch(IOException er) {
            er.printStackTrace();
            DrawThat.logMessage(er.getMessage());
            serverSocket = null;
        }
        catch(Exception er) {
            er.printStackTrace();
            DrawThat.logMessage(er.getMessage());
            serverSocket = null;
        }
    }
    
    public void startThread() {
        Thread t = new Thread(this);
        t.start();
    }
    
    @Override
    public void run() {
        chatServer = new DTChatServer(easyPack, medPack , hardPack , maxPlayers);
        while(!isEnd) {
            try {
                //waits for someone to join the server
                Socket server = serverSocket.accept();

                out = new DataOutputStream(server.getOutputStream());
                out.flush();
                in = new DataInputStream(server.getInputStream());
                
                chatServer.addClient(in,out);
            }
            catch(BindException e) {
                System.out.println("Port already in use");
                HostMenu.errorLabel.setText("Port is already in use");
            }
            catch(SocketTimeoutException e) {
                //System.out.println("Note: Socket timed out");
            }
            catch(Exception e)
            {
                DrawThat.logMessage(e.getMessage());
            }
        }
    }
    
    //Returns the public IP address
    public static String getIP() {
        String IP = "";
        BufferedReader in = null;
        
        try
        {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(
            whatismyip.openStream()));
            IP = in.readLine();
            in.close();
        }
        catch(Exception er)
        {er.printStackTrace();}
        
        return IP;
    }
    
    //Returns the local IP address
    public String getLocalIP() {
        String IP = "";
        IP = serverSocket.getLocalSocketAddress().toString();
        
        return IP;
    }
            
}
