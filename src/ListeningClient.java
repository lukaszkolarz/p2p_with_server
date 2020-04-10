import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class ListeningClient extends Thread{
    private BufferedReader in;
    private String message;
    private Client client;
    private boolean haveMessage = false;

    public ListeningClient(BufferedReader in, Client client){
        this.in = in;
        this.client = client;
        message = "";
    }

    @Override
    public void run() {
        String input;
        while (true) {
            input = receiveString();
            try{
            if (input.equals("new connection")) {
                String peerName = receiveString();
                int response = JOptionPane.showConfirmDialog(null,
                        "Do you want to play with " + peerName + "?",
                        "Checks", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    this.client.accept(peerName);
                } else {
                    this.client.refuse(peerName);
                }
            } else if (input.equals("invitation refused")) {
                JOptionPane.showMessageDialog(null,
                        client.getOpponentName() + " refused your invitation",
                        "Refuse", JOptionPane.INFORMATION_MESSAGE);
            } else {
                setMessage(input);
            }
        } catch (NullPointerException e){
                Thread.currentThread().interrupt();
                return;
            }
        }
    }


    private String receiveString(){
        String line = null;
        try{
            line = in.readLine();
        } catch (IOException e){
            System.out.println("Read failed");
        }
        return line;
    }

    public synchronized String getMessage(){
        while(!this.haveMessage){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.haveMessage = false;
        notifyAll();
        return this.message;
    }

    public synchronized void setMessage(String sms){
        while(this.haveMessage){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.haveMessage = true;
        this.message = sms;
        notifyAll();
    }
}
