import java.io.*;
import java.net.*;

/**
 * Created by burak on 06.03.2016.
 */
public class VendingClient {

    Socket clientSocket;

    public VendingClient (String ip, int port) {
        try {
            this.clientSocket = new Socket(ip, port);

        }
        catch (UnknownHostException ue) {
            ue.printStackTrace();
        }
        catch ( IOException ie )
        {
            ie.printStackTrace();
        }
    }

    public void run() throws IOException {
        String sentence;
        String response;

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));


        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer =
                new BufferedReader(new
                        InputStreamReader(clientSocket.getInputStream()));

        System.out.println("I connected to server " + clientSocket.getRemoteSocketAddress() );

        while (true)
        {
            try {

                response = inFromServer.readLine();
                System.out.println(response);

                sentence = inFromUser.readLine();
                outToServer.writeBytes(sentence + "\r\n");
                outToServer.flush();
                //clientSocket.close();

                response = inFromServer.readLine();
                System.out.println(response);


            }
            catch ( IOException ie)
            {
                ie.printStackTrace();
            }
        }
    }

}