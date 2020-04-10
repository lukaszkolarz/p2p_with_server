import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SrvThread implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String name;
    private ServerSingleton communicator;
    private ArrayList<String> names;
    private Integer namesSize;
    private SrvThread opponent = null;
    private boolean isConnected = false, waiting = false;

    public SrvThread(Socket client) {
        names = new ArrayList<>();
        communicator = ServerSingleton.getSingleton();
        this.client = client;
    }

    public void run() {
        streamInit();
        name = read();
        communicator.addNewThread(this, name);
        String message = "";

        while (true) {
            message = read();
            if (message.equals("refresh")) {
                sendAllNames();
            } else if (message.equals("connect")) {
                sendInvitation();
            } else if (message.equals("accept")) {
                accept();
                break;
            } else if (message.equals("refuse")) {
                refuse();
            } else {
                System.out.println("Invalid input");
            }
        }
        try {
            communicator.removeByName(this.name);
            client.close();
            in.close();
            out.close();
        } catch (IOException e) {
            String opponentName = read();
            System.out.println("Cannot close connection");
        }
    }

    private synchronized void sendInvitation(){
        String opponentName = read();
        if ((this.opponent = communicator.getThreadByName(opponentName)) != null){
            this.opponent.setOpponent(this);
            this.opponent.send("new connection");
            this.opponent.send(this.name);
            this.waiting = true;
            new Wait(this, name).start();
            }
        }

        private synchronized void accept(){
            this.opponent.send("invitation accepted");
            this.opponent.invitationAccepted();
        }

        private synchronized void refuse(){
            this.opponent.send("invitation refused");
            this.opponent.invitationRefused();
        }


    public void streamInit(){
        try{
            this.in = new BufferedReader((new InputStreamReader(client.getInputStream())));
            this.out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Read failed!");
            System.exit(-1);
        }
    }


    public String read(){
        String line = null;
        try{
            line = in.readLine();
        } catch (IOException e){
            System.out.println("Read failed");
        }
        return line;
    }


    public void send(String line){
        out.println(line);
    }


    private void updateNames(){
        names = communicator.getAllNames();
        namesSize = names.size();
    }

    private void sendAllNames(){
        updateNames();
        send(namesSize.toString());
        for (int i=0; i<namesSize; i++){
            send(names.get(i));
        }
    }

    public synchronized boolean invitationAccepted(){
        if (this.opponent != null){
            this.isConnected = true;
            this.send(this.opponent.getIp().substring(1));
            this.send("8123");
            communicator.removeByName(this.name);
            return true;
        }
        return false;
    }

    public synchronized void invitationRefused(){
        if (this.opponent!= null){
            this.opponent = null;
            this.waiting = false;
        }
    }

    public void setNullOpponent() {
        this.opponent = null;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void stopToWait() {
       this.waiting = false;
    }

    public synchronized String getIp() {
        InetAddress ip = this.client.getInetAddress();
        return ip.toString();
    }

    public Socket getSocket(){
        return this.client;
    }

    public void setOpponent(SrvThread opponent){
        this.opponent = opponent;
    }
}


    class Wait extends Thread {
        private SrvThread server;

        Wait(SrvThread server, String user) {
            this.server = server;
        }

    public void run() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!this.server.getIsConnected()){
            this.server.invitationRefused();
            this.server.setNullOpponent();
        }
        this.server.stopToWait();
    }

}

