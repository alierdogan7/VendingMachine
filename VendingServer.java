/**
 * Created by burak on 06.03.2016.
 */

import sun.rmi.runtime.Log;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VendingServer
{
    private ServerSocket welcomeSocket;
    private ArrayList<Stock> stocks;


    public VendingServer (int port) throws IOException
    {
        initStocks("item_list.txt");

        welcomeSocket = new ServerSocket(port);
        welcomeSocket.setSoTimeout(100000);
    }

    public void initStocks(String filename) throws IOException {

        stocks = new ArrayList<Stock>();

        // Open the file
        FileInputStream fstream = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        String pattern = "(\\d+) ([^ \\t\\n\\r]+) (\\d+)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern, Pattern.MULTILINE);

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {

            // Now create matcher object.
            Matcher m = r.matcher(strLine);
            if (m.find()) {

                //parse parameters from text
                int productId = Integer.parseInt(m.group(1));
                String productName = m.group(2);
                int amount = Integer.parseInt(m.group(3));

                //add the stock to the list
                stocks.add( new Stock( productId, productName, amount) );
            } else {
                System.out.println("NO MATCH");
            }
        }

        //Close the input stream
        br.close();
    }

    public void start() throws IOException {

        while(true)
        {
            System.out.println("Waiting for client on port " +
                    welcomeSocket.getLocalPort() + "...");
            Socket cliSocket = null;


            cliSocket = welcomeSocket.accept();
            System.out.println("Just connected to "
                    + cliSocket.getRemoteSocketAddress());

            VendingServerThread cli = new VendingServerThread(cliSocket, stocks);
            cli.start();

            try {
                cli.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    public class VendingServerThread extends Thread
    {
        private Socket cliSpecificSocket;
        private List<Stock> stocks;


        public VendingServerThread(Socket cliSpecificSocket, List<Stock> stocks)
        {
            this.cliSpecificSocket = cliSpecificSocket;
            this.stocks = stocks;
        }

        public String getStockListString()
        {
            StringBuffer str = new StringBuffer();
            str.append("ITEM LIST\n");
            for( Stock s : stocks)
            {
                str.append(s.toString() + "\n");
            }

            return str.toString();
        }

        public void run()
        {
            BufferedReader inFromClient = null;
            DataOutputStream outToClient = null;

            try {
                inFromClient = new BufferedReader(
                        new InputStreamReader(cliSpecificSocket.getInputStream()));

                outToClient = new DataOutputStream(cliSpecificSocket.getOutputStream());

                //outToClient.writeBytes("Thank you for connecting to "
                  //      + cliSpecificSocket.getLocalSocketAddress() + '\n');

            } catch (IOException e) {
                e.printStackTrace();
            }


            while(true)
            {
                try
                {
                    outToClient.writeBytes("Choose a message type (GET ITEM (L)IST, (G)ET ITEM, (Q)UIT): " + '\n');
                    outToClient.flush();

                    String clientRcvdMsg = inFromClient.readLine();

                    if( clientRcvdMsg.equals("L"))
                    {
                        outToClient.writeBytes( getStockListString() + "\n");
                        outToClient.flush();
                    }
                    else
                    {
                        outToClient.writeBytes("Invalid command \n");
                        outToClient.flush();
                    }
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