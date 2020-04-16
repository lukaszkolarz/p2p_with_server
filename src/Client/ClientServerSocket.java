package Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The ServerSocket implement server socket in ONE of two hosts which
 * want to connect i p2p way.
 */
public class ClientServerSocket {

    private Socket client;
    private ServerSocket server;
    private PrintWriter out;
    private BufferedReader in;
    int port;

    /**
     * constructor
     * @param port on this port client will listen
     */
    public ClientServerSocket(String port){
        this.port = Integer.parseInt(port);
        try {
            server = new ServerSocket(this.port);
            server.setReuseAddress(true);
        } catch (IOException e) {
            System.out.println("Cannot listen on port: " + port);
        }
    }

    /**
     * implementation interrupts of accepting connection
     */
    public void acceptConnection() {
        try {
            client = server.accept();
        } catch (IOException e) {
            System.out.println("Accept failed");
        }
    }

    /**
     * initializes sockets reading and writing buffer
     */
    public void initSocket(){
        try {
            in = new BufferedReader((new InputStreamReader(client.getInputStream())));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Read failed!");
            System.exit(-1);
        }
    }

    /**
     * receives String from socket receiving buffer
     * @return received buffer as a String
     */
        public String receiveString(){
            String line = null;
            try{
                line = in.readLine();
            } catch (IOException e){
                System.out.println("Read failed");
            }
            return line;
        }

    /**
     * puts String to sending buffer
     * @param line will be put to sending buffer
     */
    public void sendString(String line){ out.println(line); }

    /**
     * @return stream to send objects
     * @throws IOException - if an I/O error occurs when creating the output stream or if the socket is not connected
     */
    public ObjectOutputStream getObjectOut() throws IOException {
        return new ObjectOutputStream(client.getOutputStream());
    }

    /**
     * @return stream to receive objects
     * @throws IOException - if an I/O error occurs when creating the output stream or if the socket is not connected
     */
    public ObjectInputStream getObjectIn() throws IOException {
        return new ObjectInputStream(client.getInputStream());
    }
}
