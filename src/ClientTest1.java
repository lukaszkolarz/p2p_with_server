import Client.Client;

public class ClientTest1 {

    public static void main(String[] args) {

        Client client = new Client();
        client.send("Hello from the other side");
    }
}
