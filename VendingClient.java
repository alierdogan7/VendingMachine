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

    public void run()
    {
        while (true)
        {
            try {
                String sentence;
                String response;

                BufferedReader inFromUser =
                        new BufferedReader(new InputStreamReader(System.in));


                DataOutputStream outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());

                BufferedReader inFromServer =
                        new BufferedReader(new
                                InputStreamReader(clientSocket.getInputStream()));

                sentence = inFromUser.readLine();
                outToServer.writeBytes(sentence + '\n');
                outToServer.flush();

                response = inFromServer.readLine();
                System.out.println("Response:\n" + response);

                //clientSocket.close();

            }
            catch ( IOException ie)
            {
                ie.printStackTrace();
            }
        }
    }

}