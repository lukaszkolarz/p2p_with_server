import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CliSocket {
    private Socket cliSocket;
    private BufferedReader in;
    private PrintWriter out;
    private int srvPort;
    private String srvIP;

    public CliSocket(String srvIP, int srvPort){
        this.srvPort = srvPort;
        this.srvIP = srvIP;
    }

    public void newClientSocket(){
        try {
            cliSocket = new Socket(srvIP, srvPort);
            cliSocket.setReuseAddress(true);
            out = new PrintWriter(cliSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
        }catch (UnknownHostException e) {
            System.out.println("Unknown host: " + srvIP);
        }catch (IOException e) {
            System.out.println("No I/O");
            System.exit(1);
        }
    }

    public String receiveString(){
        String line = null;
        try{
            line  = in.readLine();
        } catch (IOException e) {
            System.out.println("Read failed");
        }
        return line;
    }

    public void sendString(String message){ out.println(message); }

    public BufferedReader getIn(){ return this.in; }
    public String getCliIp(){
        InetAddress tmp = cliSocket.getInetAddress();
        return tmp.getHostAddress();
    }

}
