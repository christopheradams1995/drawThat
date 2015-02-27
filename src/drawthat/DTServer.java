
package drawthat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
                in =
                        new DataInputStream(server.getInputStream());
                
                out =
                        new DataOutputStream(server.getOutputStream());
                
                
                
                
                System.out.println("Client has been accepted");
                chatServer.addClient(in,out);
                
                            
                
            }
            catch(SocketTimeoutException s)
            {
                
                //break;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                break;
            }
        }
    }
            
}
