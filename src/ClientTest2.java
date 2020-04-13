import Client.Client;

public class ClientTest2 {

    public static void main(String args[]){

        Client client = new Client();
        System.out.println(client.receiveFromHost());
    }
}
