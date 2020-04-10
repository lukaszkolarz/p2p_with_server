import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientServerSocket {
    private Socket client;
    private ServerSocket server;
    private PrintWriter out;
    private BufferedReader in;
    int port;

    public ClientServerSocket(String port){
        this.port = Integer.parseInt(port);
        try {
            server = new ServerSocket(this.port);
            server.setReuseAddress(true);
        } catch (IOException e) {
            System.out.println("Cannot listen on port: " + port);
        }
    }


    public void acceptConnection() {
        try {
            client = server.accept();
        } catch (IOException e) {
            System.out.println("Accept failed");
        }
    }

    public void initSocket(){
        try {
            in = new BufferedReader((new InputStreamReader(client.getInputStream())));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Read failed!");
            System.exit(-1);
        }
    }

        public String receiveString(){
            String line = null;
            try{
                line = in.readLine();
            } catch (IOException e){
                System.out.println("Read failed");
            }
            return line;
        }

        public void sendString(String line){ out.println(line); }
}
