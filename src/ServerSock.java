import java.io.IOException;
import java.net.ServerSocket;


public class ServerSock {
    private ServerSocket server;
    int port;

    public ServerSock(int port){
        this.port = port;
    }


    public void newServerSocket(){
        try {
            server = new ServerSocket(this.port);
            server.setReuseAddress(true);
        } catch (IOException e) {
            System.out.println("Cannot listen on port: " + port);
        }
    }


    public void acceptNewThreadConnection(){
        while(true) {
            SrvThread w;
            try{
                w = new SrvThread(server.accept());
                Thread t = new Thread(w);
                t.start();
            } catch (IOException e) {
                System.out.println("Accept failed");
                System.exit(-1);
            }
        }
    }
}
