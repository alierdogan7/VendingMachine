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
            str.append("ITEM LIST\r\n");
            for( Stock s : stocks)
            {
                str.append(s.toString() + "\r\n");
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
                    String clientRcvdMsg = inFromClient.readLine();

                    if( clientRcvdMsg.equals("GET ITEM"))
                    {
                        //read the id and amount
                        clientRcvdMsg = inFromClient.readLine();

                        String tmp = (clientRcvdMsg.split(" "))[0];
                        int id = Integer.parseInt(tmp);

                        tmp = (clientRcvdMsg.split(" "))[1];
                        int amount = Integer.parseInt(tmp);

                        //SEARCH THE REQUESTED ITEM
                        boolean outOfStock = true;
                        for( Stock s : stocks)
                        {
                            if( s.getProductId() == id && s.getAmount() >= amount)
                            {
                                s.setAmount(s.getAmount() - amount);
                                outToClient.writeBytes("SUCCESS\r\n\r\n");
                                outToClient.flush();
                                outOfStock = false;
                                break;
                            }
                        }
                        // IF ITEM NOT FOUND
                        if ( outOfStock ) {
                            outToClient.writeBytes("OUT OF STOCK\r\n\r\n");
                            outToClient.flush();
                        }

                        //pass the empty line
                        inFromClient.readLine();
                    }
                    else if ( clientRcvdMsg.equals("GET ITEM LIST"))
                    {
                        inFromClient.readLine(); //pass the empty line
                        outToClient.writeBytes( getStockListString() + "\r\n"); //dont forget to append the last empty line
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