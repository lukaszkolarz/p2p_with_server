package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The CLiSocket class creates a  client  to get another host from server and create
 * socket to be used in p2p communication
 */
public class CliSocket {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private int srvPort;
    private String srvIP;

    /**
     * constructor
     * @param srvIP server ip
     * @param srvPort port where server is listening
     */
    public CliSocket(String srvIP, int srvPort){
        this.srvPort = srvPort;
        this.srvIP = srvIP;
    }

    /**
     * creates socket
     */
    public void newClientSocket(){
        try {
            clientSocket = new Socket(srvIP, srvPort);
            clientSocket.setReuseAddress(true);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch (UnknownHostException e) {
            System.out.println("Unknown host: " + srvIP);
        }catch (IOException e) {
            System.out.println("No I/O");
            System.exit(1);
        }
    }

    /**
     * receives String from socket receiving buffer
     * @return received buffer as a String
     */
    public String receiveString(){
        String line = null;
        try{
            line  = in.readLine();
        } catch (IOException e) {
            System.out.println("Read failed");
        }
        return line;
    }

    /**
     * puts String to sending buffer
     * @param message will be put to sending buffer
     */
    public void sendString(String message){ out.println(message); }

    /**
     * @return input stream
     */
    public BufferedReader getIn(){ return this.in; }

    /**
     * @return stream to send objects
     * @throws IOException - if an I/O error occurs when creating the output stream or if the socket is not connected
     */
    public ObjectOutputStream getObjectOut() throws IOException {
        return new ObjectOutputStream(clientSocket.getOutputStream());
    }

    /**
     * @return stream to receive objects
     * @throws IOException - if an I/O error occurs when creating the output stream or if the socket is not connected
     */
    public ObjectInputStream getObjectIn() throws IOException {
        return new ObjectInputStream(clientSocket.getInputStream());
    }
}
