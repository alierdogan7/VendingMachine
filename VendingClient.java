import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by burak on 06.03.2016.
 */
public class VendingClient {

    Socket clientSocket;
    List<Stock> stockList;

    public VendingClient (String ip, int port) {
        try {
            this.clientSocket = new Socket(ip, port);
            this.stockList = new ArrayList<Stock>();
        }
        catch (UnknownHostException ue) {
            ue.printStackTrace();
        }
        catch ( IOException ie )
        {
            ie.printStackTrace();
        }
    }

    public String readMultilineMessage( BufferedReader reader ) throws IOException {
        boolean messageFinished = false;
        StringBuffer buffer = new StringBuffer();
        String response;


        while( !messageFinished )
        {
            response = reader.readLine();

            if(response.equals(""))
                messageFinished = true;
            else
            {
                buffer.append(response + "\n");
            }
        }

        return buffer.toString();
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
            try {
                System.out.println("Choose a message type (GET ITEM (L)IST, (G)ET ITEM, (Q)UIT): ");
                String userCommand = inFromUser.readLine();

                if(userCommand.equals("L"))
                {
                    outToServer.writeBytes("GET ITEM LIST\r\n\r\n");
                    outToServer.flush();

                    System.out.println(readMultilineMessage(inFromServer));
                }
                else if(userCommand.equals("G"))
                {
                    System.out.print("Give the item id:");
                    int id = Integer.parseInt(inFromUser.readLine());
                    System.out.print("Give the number of items:");
                    int amount = Integer.parseInt(inFromUser.readLine());
                    outToServer.writeBytes("GET ITEM\r\n" + id + " " + amount + "\r\n\r\n");
                    outToServer.flush();

                    String serverResponse = readMultilineMessage(inFromServer);
                    if(serverResponse.equals("SUCCESS"))
                    {
                        boolean purchasedBefore = false;
                        for( Stock s: stockList)
                        {
                            if( id == s.getProductId() )
                            {
                                s.setAmount( s.getAmount() + amount );
                                purchasedBefore = true;
                                break;
                            }
                        }
                        if( !purchasedBefore)
                        {
                            stockList.add( new Stock(id, "unknown", amount));
                        }
                        System.out.println("SUCCESS");
                    }
                    else
                    {
                        System.out.println("OUT OF STOCK");
                    }

                }
                else if(userCommand.equals("Q"))
                {
                    System.out.println("The summary of received items");
                }
                else
                {
                    System.out.println("Wrong command.");
                }



            } catch (IOException ie) {
                ie.printStackTrace();
            }
    }

}