
package drawthat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;

public class DTServer implements Runnable
{
    private ServerSocket serverSocket;
    private boolean isEnd = false;
    DataInputStream in;
    DataOutputStream out;
    static DTChatServer chatServer;
    int j = 1;
    // stores the settings to give to the child chat server class
    boolean easyPack , medPack , hardPack;
    int maxPlayers;
    int backlog = 10;
    
    public DTServer(int port, boolean easyPack , boolean medPack , boolean hardPack, int maxPlayers) throws IOException
    {
        serverSocket = new ServerSocket(port);
        
        serverSocket.setSoTimeout(1000);
        
        this.easyPack = easyPack;
        this.medPack = medPack;
        this.hardPack = hardPack;
        this.maxPlayers = maxPlayers;
        
        Thread t = new Thread(this);
        t.start();

    }
    
    public void run()
    {
        chatServer = new DTChatServer(easyPack, medPack , hardPack , maxPlayers);
        
        while(!isEnd)
        {
            try
            {
                //waits for someone to join the server
                Socket server = serverSocket.accept();
                System.out.println("server info : inetaddress" + server.getInetAddress());
                System.out.println("server info : localaddress" + server.getLocalAddress());
                System.out.println("server info : reuseaddress" + server.getReuseAddress());
                System.out.println("server info : localsocketaddress" + server.getLocalSocketAddress());

                out =
                        new DataOutputStream(server.getOutputStream());
                out.flush();
                in =
                        new DataInputStream(server.getInputStream());
                
                chatServer.addClient(in,out);
                
                            
                
            }
            catch(SocketTimeoutException s)
            {
                
                //Game.logMessage(s.getMessage());
            }
            catch(Exception e)
            {
                Game.logMessage(e.getMessage());
                break;
            }
        }
    }
    
    //Returns the public IP address
    public static String getIP()
    {
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
    public String getLocalIP()
    {
        String IP = "";
        IP = serverSocket.getLocalSocketAddress().toString();
        
        return IP;
    }
            
}
