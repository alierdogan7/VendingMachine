/**
 * Created by burak on 06.03.2016.
 */

import java.net.*;
import java.io.*;

public class VendingServer
{
    private ServerSocket welcomeSocket;

    public VendingServer (int port) throws IOException
    {
        welcomeSocket = new ServerSocket(port);
        welcomeSocket.setSoTimeout(100000);
    }

    public void start()
    {

        while(true)
        {
            System.out.println("Waiting for client on port " +
                    welcomeSocket.getLocalPort() + "...");
            Socket cliSocket = null;

            try {
                cliSocket = welcomeSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Just connected to "
                    + cliSocket.getRemoteSocketAddress());

            VendingServerThread cli = new VendingServerThread(cliSocket);
            cli.start();
        }


    }

    public class VendingServerThread extends Thread
    {
        private Socket cliSpecificSocket;

        public VendingServerThread(Socket cliSpecificSocket)
        {
            this.cliSpecificSocket = cliSpecificSocket;
        }

        public void run()
        {
            while(true)
            {
                try
                {
                    BufferedReader inFromClient = new BufferedReader(
                            new InputStreamReader(cliSpecificSocket.getInputStream()));

                    System.out.println(inFromClient.readLine());

                    DataOutputStream out =
                            new DataOutputStream(cliSpecificSocket.getOutputStream());
                    out.writeBytes("Thank you for connecting to "
                            + cliSpecificSocket.getLocalSocketAddress() );
                    out.flush();
                    //server.close();

                }catch(SocketTimeoutException s)
                {
                    System.out.println("Socket timed out!");
                    break;
                }catch(IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}