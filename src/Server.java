public class Server {
    public static void main(String[] args) {
        ServerSock serverSocket =  new ServerSock(8000);
        serverSocket.newServerSocket();
        serverSocket.acceptNewThreadConnection();
    }
}

